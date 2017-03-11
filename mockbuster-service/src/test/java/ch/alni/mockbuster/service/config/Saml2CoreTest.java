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

package ch.alni.mockbuster.service.config;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.oasis.saml2.protocol.AuthnRequestType;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.validation.Schema;

import ch.alni.mockbuster.service.dom.Documents;
import ch.alni.mockbuster.service.saml2.Saml2ObjectMarshaller;
import ch.alni.mockbuster.service.saml2.Saml2ObjectUnmarshaller;

import static org.fest.assertions.Assertions.assertThat;

public class Saml2CoreTest {

    private Saml2ObjectUnmarshaller saml2ObjectUnmarshaller;
    private Saml2ObjectMarshaller<AuthnRequestType> authnRequestMarshaller;

    @Before
    public void setUp() throws Exception {
        ServiceConfig serviceConfig = new ServiceConfig();
        JAXBContext jaxbContext = serviceConfig.jaxbContext();
        Schema schema = serviceConfig.schema();

        saml2ObjectUnmarshaller = new Saml2ObjectUnmarshaller(jaxbContext, schema);
        authnRequestMarshaller = new Saml2ObjectMarshaller<AuthnRequestType>(jaxbContext, schema) {
            @Override
            protected JAXBElement<AuthnRequestType> createJaxbElement(AuthnRequestType samlObject) {
                return getObjectFactory().createAuthnRequest(samlObject);
            }
        };
    }

    @Test
    public void testSerialize() throws Exception {
        String request = IOUtils.toString(getClass().getResourceAsStream("/authn_request.xml"), "UTF-8");

        AuthnRequestType authnRequest = saml2ObjectUnmarshaller.unmarshal(request, AuthnRequestType.class);

        assertThat(authnRequest).isNotNull();
        assertThat(authnRequest.getID()).isEqualTo("identifier_1");
        assertThat(authnRequest.getIssuer().getValue()).isEqualTo("https://sp.example.com/SAML2");

        String result = Documents.toString(authnRequestMarshaller.objectToDocument(authnRequest));

        assertThat(result).contains("identifier_1");
        assertThat(result).contains("https://sp.example.com/SAML2");
    }

}