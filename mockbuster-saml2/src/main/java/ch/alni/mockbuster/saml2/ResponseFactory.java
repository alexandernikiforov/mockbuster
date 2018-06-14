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

package ch.alni.mockbuster.saml2;

import org.apache.commons.lang.Validate;
import org.oasis.saml2.assertion.*;
import org.oasis.saml2.protocol.AuthnRequestType;
import org.oasis.saml2.protocol.ResponseType;
import org.oasis.saml2.protocol.StatusCodeType;
import org.oasis.saml2.protocol.StatusType;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

/**
 * Response factory according to the rules of the Web Browser SSO profile.
 */
public final class ResponseFactory {
    private static final String SAML_VERSION = "2.0";
    private static final ObjectFactory assertionObjectFactory = new ObjectFactory();

    private static String createId() {
        return "_" + UUID.randomUUID().toString();
    }

    private ResponseFactory() {
    }

    public static ResponseType makeResponse(ResponseTypeParams params) {
        final AuthnRequestType authnRequestType = params.getAuthnRequestType();

        final String requestId = authnRequestType.getID();
        final String responseIssuer = params.getIdentityProviderId();

        // this must be present according to the profile rules
        final String requestIssuerId = authnRequestType.getIssuer().getValue();

        final Instant now = Instant.now();
        final Instant deliveryNotOnOrAfter = now.plus(params.getDeliveryValidityInSeconds(), ChronoUnit.SECONDS);

        final Instant sessionNotOnOrAfter = Optional.ofNullable(params.getSessionTimeoutInSeconds())
                .map(timeout -> now.plus(timeout, ChronoUnit.SECONDS))
                .orElse(null);

        final AttributeStatementType attributeStatementType = params.getAttributeStatementType();

        final String assertionConsumerServiceUrl = params.getAssertionConsumerServiceUrl();
        return ResponseType.builder()
                .withID(createId())
                .withIssueInstant(now)
                .withInResponseTo(requestId)
                .withDestination(assertionConsumerServiceUrl)
                .withVersion(SAML_VERSION)

                // issuer
                .withIssuer(NameIDType.builder()
                        .withFormat("urn:oasis:names:tc:SAML:2.0:nameid-format:entity")
                        .withValue(responseIssuer)
                        .build())

                // status
                .withStatus(StatusType.builder()
                        .withStatusCode(StatusCodeType.builder()
                                .withValue(SamlResponseStatus.SUCCESS.getValue())
                                .build())
                        .build())

                .addAssertion(AssertionType.builder()
                        .withID(createId())
                        .withIssueInstant(now)
                        .withIssuer(NameIDType.builder()
                                .withFormat("urn:oasis:names:tc:SAML:2.0:nameid-format:entity")
                                .withValue(responseIssuer)
                                .build())

                        .withSubject(SubjectType.builder()
                                .withContent(
                                        assertionObjectFactory.createNameID(params.getSubjectIdentityId()),

                                        assertionObjectFactory.createSubjectConfirmation(SubjectConfirmationType.builder()
                                                .withMethod("urn:oasis:names:tc:SAML:2.0:cm:bearer")
                                                .withSubjectConfirmationData(SubjectConfirmationDataType.builder()
                                                        .withNotOnOrAfter(deliveryNotOnOrAfter)
                                                        .withRecipient(assertionConsumerServiceUrl)
                                                        .withInResponseTo(requestId)
                                                        .build())
                                                .build()))
                                .build())

                        .withConditions(ConditionsType.builder()
                                .addAudienceRestriction(AudienceRestrictionType.builder()
                                        .withAudience(requestIssuerId)
                                        .build())
                                .build())

                        .addAuthnStatement(AuthnStatementType.builder()
                                .withAuthnInstant(Instant.now())
                                .withSessionNotOnOrAfter(sessionNotOnOrAfter)
                                .withSessionIndex(params.getSessionIndex())
                                .withAuthnContext(AuthnContextType.builder()
                                        .withContent(
                                                assertionObjectFactory
                                                        .createAuthnContextClassRef("urn:oasis:names:tc:SAML:2.0:ac:classes:PasswordProtectedTransport")
                                        )
                                        .build())
                                .build())

                        .addAttributeStatement(attributeStatementType)
                        .build())

                .build();
    }

    /**
     * Produces an error authn response with the given status. There are no assertions in the response.
     */
    public static ResponseType makeErrorResponse(ResponseTypeParams params, SamlResponseStatus responseStatus) {
        final AuthnRequestType authnRequestType = params.getAuthnRequestType();
        final String identityProviderId = params.getIdentityProviderId();

        Validate.notNull(authnRequestType, "authnRequestType cannot be null");
        Validate.notNull(identityProviderId, "identityProviderId cannot be null");

        final String requestId = authnRequestType.getID();
        final Instant now = Instant.now();

        return ResponseType.builder()
                .withID(createId())
                .withIssueInstant(now)
                .withInResponseTo(requestId)
                .withVersion(SAML_VERSION)

                // issuer
                .withIssuer(NameIDType.builder()
                        .withFormat("urn:oasis:names:tc:SAML:2.0:nameid-format:entity")
                        .withValue(identityProviderId)
                        .build())

                // status
                .withStatus(StatusType.builder()
                        .withStatusCode(StatusCodeType.builder()
                                .withValue(responseStatus.getValue())
                                .build())
                        .build())
                .build();
    }
}
