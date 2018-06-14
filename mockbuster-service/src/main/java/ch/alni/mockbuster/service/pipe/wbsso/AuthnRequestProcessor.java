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

package ch.alni.mockbuster.service.pipe.wbsso;

import ch.alni.mockbuster.core.domain.*;
import ch.alni.mockbuster.saml2.AttributeStatements;
import ch.alni.mockbuster.saml2.AuthnRequestTypeFunctions;
import ch.alni.mockbuster.saml2.ResponseFactory;
import ch.alni.mockbuster.saml2.ResponseTypeParams;
import ch.alni.mockbuster.service.messages.*;
import ch.alni.mockbuster.service.session.Session;
import org.apache.commons.lang.BooleanUtils;
import org.oasis.saml2.assertion.AttributeStatementType;
import org.oasis.saml2.assertion.NameIDType;
import org.oasis.saml2.protocol.AuthnRequestType;
import org.oasis.saml2.protocol.ResponseType;

import static ch.alni.mockbuster.core.domain.AssertionConsumerServicePredicates.hasSameIndex;
import static ch.alni.mockbuster.core.domain.AssertionConsumerServicePredicates.hasSameUrl;

/**
 * Process the given authn request and produces a SAML response.
 */
public final class AuthnRequestProcessor {

    public static SamlResponse processAuthRequest(Session session, AuthnRequest authnRequest) {

        // find identity
        return AuthnRequestTypeFunctions.findSubjectIdentity(authnRequest.getAuthnRequestType())
                .map(nameIDType -> new NameId(nameIDType.getFormat(), nameIDType.getValue()))
                // the SP provided principal identity in the request
                .map(subjectIdentity -> respondIfIdentityIsProvided(session, authnRequest, subjectIdentity))
                // no identity provided, let the user decide who he is
                .orElseGet(() -> requireUserInteraction(authnRequest));
    }

    private static SamlResponse respondIfIdentityIsProvided(Session session, AuthnRequest authnRequest, NameId subjectIdentity) {
        // have we already established this identity in the provided session?
        return session.getIdentities()
                .stream()
                .filter(principal -> principal.getNameId().equals(subjectIdentity))
                .findFirst()

                // yes - authenticate
                .map(principal -> authenticate(session, authnRequest, principal))

                // no - let the user select the identity
                .orElseGet(() -> requireUserInteraction(authnRequest));
    }

    private static SamlResponse authenticate(final Session session, final AuthnRequest authnRequest, final Principal principal) {
        final AuthnRequestType authnRequestType = authnRequest.getAuthnRequestType();

        if (BooleanUtils.isTrue(authnRequestType.isForceAuthn())) {
            // the client is forcing us to re-authenticate
            return new PostponedSamlResponse(new UserAuthenticationRequiredResponse(authnRequest, principal));
        } else {
            // create response from what we have
            final IdentityProvider identityProvider = authnRequest.getIdentityProvider();
            final ServiceProvider serviceProvider = authnRequest.getServiceProvider();

            final AttributeStatementType attributeStatementType =
                    AttributeStatements.toAttributeStatementType(principal.getAttributeStatement());

            final NameId nameId = principal.getNameId();

            final String assertionConsumerServiceUrl = serviceProvider.getAssertionConsumerServices()
                    .stream()
                    .filter(hasSameUrl(authnRequestType.getAssertionConsumerServiceURL())
                            .or(hasSameIndex(authnRequestType.getAssertionConsumerServiceIndex())))
                    .findFirst()
                    .map(AssertionConsumerService::getUrl)
                    .orElseThrow(() -> new IllegalArgumentException("either url or index should be provided"));

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

            return new AuthnResponse(responseType);
        }
    }

    private static SamlResponse requireUserInteraction(AuthnRequest authnRequest) {
        return new PostponedSamlResponse(new UserSelectionRequiredResponse(authnRequest));
    }
}
