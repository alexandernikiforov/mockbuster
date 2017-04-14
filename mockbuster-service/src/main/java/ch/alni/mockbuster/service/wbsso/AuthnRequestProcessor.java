/*
 * Mockbuster SAML2 IDP
 * Copyright (C) 2016  Alexander Nikiforov
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ch.alni.mockbuster.service.wbsso;

import org.apache.commons.lang.BooleanUtils;
import org.oasis.saml2.assertion.NameIDType;
import org.oasis.saml2.protocol.AuthnRequestType;
import org.oasis.saml2.protocol.ResponseType;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Consumer;

import javax.inject.Inject;

import ch.alni.mockbuster.core.Principal;
import ch.alni.mockbuster.core.PrincipalRepository;
import ch.alni.mockbuster.service.ServiceResponse;
import ch.alni.mockbuster.service.authentication.AuthnRequestRepository;
import ch.alni.mockbuster.service.events.ServiceEventPublisher;

@Component
public class AuthnRequestProcessor {
    private final AuthnRequestRepository authnRequestRepository;
    private final PrincipalRepository principalRepository;
    private final ResponseAssembler responseAssembler;

    @Inject
    public AuthnRequestProcessor(AuthnRequestRepository authnRequestRepository,
                                 PrincipalRepository principalRepository,
                                 ResponseAssembler responseAssembler) {
        this.authnRequestRepository = authnRequestRepository;
        this.principalRepository = principalRepository;
        this.responseAssembler = responseAssembler;
    }

    @EventListener
    public void onAuthnRequestValidated(SamlRequestValidated<AuthnRequestType> event) {
        AuthnRequestType authnRequestType = event.getSamlRequestType();

        findNameIDType(authnRequestType)
                // the service provider supplied an identity
                .map(identity -> withServiceResponseIfIdentityIsProvided(authnRequestType, identity))

                .orElse(withServiceResponseIfIdentityIsMissing(authnRequestType))

                // and call the resulting function on the service response
                .accept(event.getServiceResponse());
    }

    private Consumer<ServiceResponse> withServiceResponseIfIdentityIsProvided(AuthnRequestType authnRequestType,
                                                                              NameIDType identity) {
        final String nameId = identity.getValue();

        return principalRepository.findByNameId(nameId)
                // yes - authenticate
                .map(principal -> authenticate(principal, authnRequestType))

                // no - respond with an error
                .orElse(sendAuthnFailed(authnRequestType));
    }

    private Consumer<ServiceResponse> withServiceResponseIfIdentityIsMissing(AuthnRequestType authnRequestType) {
        final boolean allowCreate = Optional.ofNullable(authnRequestType.getNameIDPolicy())
                .map(nameIDPolicyType -> BooleanUtils.isTrue(nameIDPolicyType.isAllowCreate()))
                .orElse(false);

        final boolean forceAuthn = BooleanUtils.isTrue(authnRequestType.isForceAuthn());

        if (forceAuthn) {
            return requireUserInteraction(authnRequestType);
        } else if (allowCreate) {
            // from session or let the user select
            return authnRequestRepository.findPrincipal()
                    .map(principal -> authenticate(principal, authnRequestType))
                    .orElse(requireUserInteraction(authnRequestType));
        } else {
            // from session or deny
            return authnRequestRepository.findPrincipal()
                    .map(principal -> authenticate(principal, authnRequestType))
                    .orElse(sendAuthnFailed(authnRequestType));
        }
    }

    private Consumer<ServiceResponse> authenticate(Principal principal, AuthnRequestType authnRequestType) {
        return serviceResponse -> {
            ResponseType responseType = responseAssembler.toAuthenticatedResponseType(principal, authnRequestType);

            ServiceEventPublisher.getInstance().publish(new SamlResponsePrepared<>(
                    serviceResponse,
                    authnRequestType,
                    responseType));
        };
    }

    private Consumer<ServiceResponse> sendAuthnFailed(AuthnRequestType authnRequestType) {
        return serviceResponse -> {
            ResponseType responseType = responseAssembler.toStatusResponse(authnRequestType, SamlResponseStatus.AUTHN_FAILED);

            ServiceEventPublisher.getInstance().publish(new SamlResponsePrepared<>(
                    serviceResponse,
                    authnRequestType,
                    responseType));
        };
    }


    private Consumer<ServiceResponse> requireUserInteraction(AuthnRequestType authnRequestType) {
        return serviceResponse -> {
            authnRequestRepository.storeAuthnRequest(authnRequestType);

            // stop processing and request the user interaction
            serviceResponse.sendUserInteractionRequired();
        };
    }

    /**
     * Finds the identity in the Subject element.
     */
    private Optional<NameIDType> findNameIDType(AuthnRequestType authnRequestType) {
        return Optional.ofNullable(authnRequestType.getSubject())
                .map(subjectType -> subjectType.getContent()
                        .stream()
                        .filter(jaxbElement -> jaxbElement.getValue() instanceof NameIDType)
                        .findFirst()
                        .map(jaxbElement -> ((NameIDType) jaxbElement.getValue())))
                .orElse(Optional.empty())
                ;
    }

}
