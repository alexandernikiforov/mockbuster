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

import org.oasis.saml2.assertion.AssertionType;
import org.oasis.saml2.assertion.AudienceRestrictionType;
import org.oasis.saml2.assertion.AuthnContextType;
import org.oasis.saml2.assertion.AuthnStatementType;
import org.oasis.saml2.assertion.ConditionsType;
import org.oasis.saml2.assertion.NameIDType;
import org.oasis.saml2.assertion.ObjectFactory;
import org.oasis.saml2.assertion.SubjectConfirmationType;
import org.oasis.saml2.assertion.SubjectType;
import org.oasis.saml2.protocol.AuthnRequestType;
import org.oasis.saml2.protocol.ResponseType;
import org.oasis.saml2.protocol.StatusCodeType;
import org.oasis.saml2.protocol.StatusType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

import ch.alni.mockbuster.core.Principal;

/**
 * Response assembler
 */
@Component
class ResponseAssembler {
    private static final String SAML_VERSION = "2.0";
    private final ObjectFactory assertionObjectFactory = new ObjectFactory();
    @Value("${mockbuster.issuer:mockbuster}")
    private String responseIssuer;

    ResponseType toResponseType(Principal principal, AuthnRequestType request) {
        String requestId = request.getID();

        return ResponseType.builder()
                .withID("_" + UUID.randomUUID().toString())
                .withIssueInstant(Instant.now())
                .withInResponseTo(requestId)
                .withDestination(request.getAssertionConsumerServiceURL())
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
                        .withID("_" + UUID.randomUUID().toString())
                        .withIssueInstant(Instant.now())
                        .withIssuer(NameIDType.builder()
                                .withFormat("urn:oasis:names:tc:SAML:2.0:nameid-format:entity")
                                .withValue(responseIssuer)
                                .build())

                        .withSubject(SubjectType.builder()
                                .withContent(
                                        assertionObjectFactory.createNameID(NameIDType.builder()
                                                .withFormat(principal.getNameIdFormat())
                                                .withValue(principal.getNameId())
                                                .build()),
                                        assertionObjectFactory.createSubjectConfirmation(SubjectConfirmationType.builder()
                                                .withMethod("urn:oasis:names:tc:SAML:2.0:cm:bearer")
                                                .build()))
                                .build())

                        .withConditions(ConditionsType.builder()
                                // TODO set the boundaries
                                .withNotBefore(Instant.now())
                                .withNotOnOrAfter(Instant.now())
                                .addAudienceRestriction(AudienceRestrictionType.builder()
                                        .withAudience(request.getAssertionConsumerServiceURL())
                                        .build())
                                .build())

                        .addAuthnStatement(AuthnStatementType.builder()
                                .withAuthnInstant(Instant.now())
                                .withAuthnContext(AuthnContextType.builder()
                                        .withContent(
                                                assertionObjectFactory
                                                        .createAuthnContextClassRef("urn:oasis:names:tc:SAML:2.0:ac:classes:PasswordProtectedTransport")
                                        )
                                        .build())
                                .build())

                        .addAttributeStatement(principal.getAttributeStatementType())
                        .build())

                .build();

    }
}
