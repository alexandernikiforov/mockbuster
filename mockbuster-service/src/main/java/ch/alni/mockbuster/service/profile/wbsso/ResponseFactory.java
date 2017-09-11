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

package ch.alni.mockbuster.service.profile.wbsso;

import ch.alni.mockbuster.core.domain.Principal;
import ch.alni.mockbuster.saml2.AttributeStatements;
import ch.alni.mockbuster.service.ServiceConfiguration;
import ch.alni.mockbuster.service.profile.common.SamlResponseStatus;
import org.oasis.saml2.assertion.*;
import org.oasis.saml2.protocol.AuthnRequestType;
import org.oasis.saml2.protocol.ResponseType;
import org.oasis.saml2.protocol.StatusCodeType;
import org.oasis.saml2.protocol.StatusType;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

/**
 * Response factory according to the rules of the Web Browser SSO profile.
 */
class ResponseFactory {
    private static final String SAML_VERSION = "2.0";
    private final ObjectFactory assertionObjectFactory = new ObjectFactory();

    private final ServiceConfiguration serviceConfiguration;

    public ResponseFactory(ServiceConfiguration serviceConfiguration) {
        this.serviceConfiguration = serviceConfiguration;
    }

    private static String createId() {
        return "_" + UUID.randomUUID().toString();
    }

    ResponseType makeResponse(AuthnRequestType request, Principal principal) {

        final String requestId = request.getID();
        final String responseIssuer = serviceConfiguration.getServiceId();

        // this must be present according to the profile rules
        final String requestIssuerId = request.getIssuer().getValue();

        final Instant now = Instant.now();
        final Instant deliveryNotOnOrAfter = now.plus(serviceConfiguration.getDeliveryValidityInSeconds(), ChronoUnit.SECONDS);

        final Instant sessionNotOnOrAfter = serviceConfiguration.isSessionPermanent() ? null :
                now.plus(serviceConfiguration.getSessionNotOnOrAfterInSeconds(), ChronoUnit.SECONDS);

        final String assertionConsumerServiceUrl = request.getAssertionConsumerServiceURL();

        final AttributeStatementType attributeStatementType =
                AttributeStatements.toAttributeStatementType(principal.getAttributeStatement());

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
                                .withValue("urn:oasis:names:tc:SAML:2.0:status:Success")
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
                                        assertionObjectFactory.createNameID(NameIDType.builder()
                                                .withFormat(principal.getNameId().getNameIdFormat())
                                                .withValue(principal.getNameId().getNameId())
                                                .build()),

                                        assertionObjectFactory.createSubjectConfirmation(SubjectConfirmationType.builder()
                                                .withMethod("urn:oasis:names:tc:SAML:2.0:cm:bearer")
                                                .withSubjectConfirmationData(SubjectConfirmationDataType.builder()
                                                        .withNotBefore(now)
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

    ResponseType makeResponse(AuthnRequestType request, SamlResponseStatus responseStatus) {
        final String requestId = request.getID();
        final String responseIssuer = serviceConfiguration.getServiceId();
        final Instant now = Instant.now();

        return ResponseType.builder()
                .withID(createId())
                .withIssueInstant(now)
                .withInResponseTo(requestId)
                .withVersion(SAML_VERSION)

                // issuer
                .withIssuer(NameIDType.builder()
                        .withFormat("urn:oasis:names:tc:SAML:2.0:nameid-format:entity")
                        .withValue(responseIssuer)
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
