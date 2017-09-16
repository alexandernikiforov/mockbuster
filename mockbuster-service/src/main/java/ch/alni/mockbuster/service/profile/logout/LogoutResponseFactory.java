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

package ch.alni.mockbuster.service.profile.logout;

import ch.alni.mockbuster.saml2.SamlResponseStatus;
import ch.alni.mockbuster.service.ServiceConfiguration;
import org.oasis.saml2.assertion.NameIDType;
import org.oasis.saml2.protocol.LogoutRequestType;
import org.oasis.saml2.protocol.StatusCodeType;
import org.oasis.saml2.protocol.StatusResponseType;
import org.oasis.saml2.protocol.StatusType;

import java.time.Instant;
import java.util.UUID;

/**
 * Response assembler according to the rules of the Web Browser SSO profile.
 */
class LogoutResponseFactory {
    private static final String SAML_VERSION = "2.0";

    private final ServiceConfiguration serviceConfiguration;

    LogoutResponseFactory(ServiceConfiguration serviceConfiguration) {
        this.serviceConfiguration = serviceConfiguration;
    }

    StatusResponseType makeStatusResponse(LogoutRequestType request, SamlResponseStatus responseStatus) {
        final String requestId = request.getID();
        final String responseIssuer = serviceConfiguration.getServiceId();
        final Instant now = Instant.now();

        return StatusResponseType.builder()
                .withID("_" + UUID.randomUUID().toString())
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
