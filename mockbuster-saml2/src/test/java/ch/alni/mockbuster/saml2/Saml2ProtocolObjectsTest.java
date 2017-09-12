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

import org.junit.Test;
import org.oasis.saml2.assertion.NameIDType;
import org.oasis.saml2.protocol.AuthnRequestType;
import org.oasis.saml2.protocol.ObjectFactory;

import java.time.Instant;

public class Saml2ProtocolObjectsTest {
    private final ObjectFactory objectFactory = new ObjectFactory();

    @Test
    public void testJaxbElementToDocument() throws Exception {
        AuthnRequestType authnRequestType = AuthnRequestType.builder()
                .withID("identifier_1")
                .withVersion("2.0")
                .withIssueInstant(Instant.now())
                .withAssertionConsumerServiceIndex(0)
                .withIssuer(
                        NameIDType.builder()
                                .withValue("https://sp.example.com/SAML2")
                                .build()
                )
                .build();

        Saml2ProtocolObjects.jaxbElementToDocument(objectFactory.createAuthnRequest(authnRequestType));
    }

}