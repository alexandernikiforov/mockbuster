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

package ch.alni.mockbuster.decoder;

import org.junit.Before;
import org.junit.Test;
import org.oasis.saml2.protocol.AuthnRequestType;
import org.oasis.saml2.protocol.ObjectFactory;

import java.io.InputStreamReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.validation.Schema;

import ch.alni.mockbuster.decoder.config.DecoderConfig;

import static org.fest.assertions.Assertions.assertThat;

public class Saml2SerializerTest {

    private Saml2Serializer saml2Serializer;

    @Before
    public void setUp() throws Exception {
        DecoderConfig decoderConfig = new DecoderConfig();
        JAXBContext jaxbContext = decoderConfig.jaxbContext();
        Schema schema = decoderConfig.schema();

        saml2Serializer = new Saml2Serializer(jaxbContext, schema);
    }

    @Test
    public void testSerialize() throws Exception {
        AuthnRequestType authnRequest = saml2Serializer.deserialize(
                new InputStreamReader(getClass().getResourceAsStream("/authn_request.xml"), "UTF-8"),
                AuthnRequestType.class);

        assertThat(authnRequest).isNotNull();
        assertThat(authnRequest.getID()).isEqualTo("identifier_1");
        assertThat(authnRequest.getIssuer().getValue()).isEqualTo("https://sp.example.com/SAML2");

        ObjectFactory objectFactory = new ObjectFactory();
        JAXBElement<AuthnRequestType> jaxbElement = objectFactory.createAuthnRequest(authnRequest);

        byte[] result = saml2Serializer.serialize(jaxbElement);
        assertThat(result).isNotNull();
    }

}