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

import org.oasis.saml2.assertion.NameIDType;
import org.oasis.saml2.protocol.AuthnRequestType;
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

    @Inject
    public AuthnRequestProcessor(AuthnRequestRepository authnRequestRepository, PrincipalRepository principalRepository) {
        this.authnRequestRepository = authnRequestRepository;
        this.principalRepository = principalRepository;
    }

    @EventListener
    public void onAuthnRequestValidated(SamlRequestValidated<AuthnRequestType> event) {
        AuthnRequestType authnRequestType = event.getSamlRequestType();

        findNameIDType(authnRequestType)
                .map(NameIDType::getValue)
                // is there an authn request already stored for this subject?
                .filter(authnRequestRepository::isAuthnRequestStoredForNameId)
                // find the principal for this name ID
                .flatMap(principalRepository::findByNameId)
                // and send back authenticated
                .map(principal -> authenticate(principal, authnRequestType))
                // otherwise require the user interaction
                .orElse(requireUserInteraction(authnRequestType))
                .accept(event.getServiceResponse());
    }

    private Consumer<ServiceResponse> authenticate(Principal principal, AuthnRequestType authnRequestType) {
        return serviceResponse -> {
            // build response object
            ServiceEventPublisher.getInstance().publish(new AuthenticatedResponsePrepared(
                    serviceResponse,
                    null));
        };
    }

    private Consumer<ServiceResponse> requireUserInteraction(AuthnRequestType authnRequestType) {
        return serviceResponse -> {
            authnRequestRepository.storeAuthRequest(authnRequestType);
            // stop processing and request the user interaction
            serviceResponse.sendUserInteractionRequired();
        };
    }

    private Optional<NameIDType> findNameIDType(AuthnRequestType authnRequestType) {
        return authnRequestType.getSubject().getContent().stream()
                .filter(jaxbElement -> jaxbElement.getValue() instanceof NameIDType)
                .findFirst()
                .map(jaxbElement -> ((NameIDType) jaxbElement.getValue()))
                ;
    }

}
