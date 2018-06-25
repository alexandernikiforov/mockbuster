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

package ch.alni.mockbuster.service.profile.core;

import ch.alni.mockbuster.core.domain.*;
import ch.alni.mockbuster.saml2.*;
import ch.alni.mockbuster.service.session.Session;
import ch.alni.mockbuster.service.session.SessionRepository;
import org.apache.commons.lang.BooleanUtils;
import org.oasis.saml2.assertion.AttributeStatementType;
import org.oasis.saml2.assertion.NameIDType;
import org.oasis.saml2.protocol.AuthnRequestType;
import org.oasis.saml2.protocol.ResponseType;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class IdentityService {

    private final SessionRepository sessionRepository;

    @Inject
    public IdentityService(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    public IdentityServiceResponse process(FailedAuthnRequest failedAuthnRequest) {
        final SamlResponseStatus samlResponseStatus = failedAuthnRequest.getResponseStatus();
        final IdentityProvider identityProvider = failedAuthnRequest.getIdentityProvider();
        final AuthnRequestType authnRequestType = failedAuthnRequest.getAuthnRequestType();
        final ServiceProvider serviceProvider = failedAuthnRequest.getServiceProvider();
        final AssertionConsumerService assertionConsumerService = failedAuthnRequest.getReturnDestination();

        final ResponseType responseType = ResponseFactory.makeErrorResponse(
                new ResponseTypeParams.Builder()
                        .setIdentityProviderId(identityProvider.getEntityId())
                        .setAuthnRequestType(authnRequestType)
                        .build(),
                samlResponseStatus
        );

        return new AuthnResponse(responseType, serviceProvider, assertionConsumerService);
    }

    public IdentityServiceResponse process(AuthnRequest authnRequest) {
        final AuthnRequestType authnRequestType = authnRequest.getAuthnRequestType();

        // find identity
        return AuthnRequestTypeFunctions.findSubjectIdentity(authnRequestType)
                .map(nameIDType -> new NameId(nameIDType.getFormat(), nameIDType.getValue()))
                // the SP provided principal identity in the request
                .map(subjectIdentity -> respondIfIdentityIsProvided(authnRequest, subjectIdentity))
                // no identity provided, let the user decide who he is
                .orElseGet(this::requireUserInteraction);
    }

    private IdentityServiceResponse respondIfIdentityIsProvided(AuthnRequest authnRequest, NameId subjectIdentity) {
        Session session = sessionRepository.getCurrentSession();

        // have we already established this identity in the provided session?
        return session.getIdentities()
                .stream()
                .filter(principal -> principal.getNameId().equals(subjectIdentity))
                .findFirst()

                // yes - authenticate
                .map(principal -> authenticate(session, authnRequest, principal))

                // no - let the user select the identity
                .orElseGet(this::requireUserInteraction);
    }

    private IdentityServiceResponse authenticate(final Session session, final AuthnRequest authnRequest, final Principal principal) {
        final AuthnRequestType authnRequestType = authnRequest.getAuthnRequestType();

        if (BooleanUtils.isTrue(authnRequestType.isForceAuthn())) {
            // the client is forcing us to re-authenticate
            return new UserAuthenticationRequiredResponse(IdentityIds.uniqueId(), principal);
        } else {
            // create response from what we have
            final IdentityProvider identityProvider = authnRequest.getIdentityProvider();
            final ServiceProvider serviceProvider = authnRequest.getServiceProvider();
            final AssertionConsumerService assertionConsumerService = authnRequest.getReturnDestination();

            final AttributeStatementType attributeStatementType =
                    AttributeStatements.toAttributeStatementType(principal.getAttributeStatement());

            final NameId nameId = principal.getNameId();

            final String assertionConsumerServiceUrl = assertionConsumerService.getUrl();

            final ResponseType responseType = ResponseFactory.makeResponse(
                    new ResponseTypeParams.Builder()
                            .setAuthnRequestType(authnRequestType)

                            .setIdentityProviderId(identityProvider.getEntityId())
                            .setDeliveryValidityInSeconds(identityProvider.getDeliveryValidityInSeconds())

                            .setAssertionConsumerServiceUrl(assertionConsumerServiceUrl)

                            .setSessionTimeoutInSeconds(session.getTimeoutInSeconds())
                            .setSessionIndex(session.getIndex())

                            .setAttributeStatementType(attributeStatementType)

                            .setSubjectIdentityId(NameIDType.builder()
                                    .withFormat(nameId.getNameIdFormat())
                                    .withValue(nameId.getNameId())
                                    .build())
                            .build()
            );

            return new AuthnResponse(responseType, serviceProvider, authnRequest.getReturnDestination());
        }
    }

    private IdentityServiceResponse requireUserInteraction() {
        return new UserSelectionRequiredResponse(IdentityIds.uniqueId());
    }

}
