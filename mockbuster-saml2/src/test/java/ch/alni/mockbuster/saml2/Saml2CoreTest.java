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

import org.fest.assertions.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.oasis.saml2.protocol.AuthnRequestType;
import org.oasis.saml2.protocol.ObjectFactory;
import org.w3c.dom.Document;

import java.io.InputStreamReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.validation.Schema;

import ch.alni.mockbuster.dom.DocumentSerializer;
import ch.alni.mockbuster.dom.Documents;
import ch.alni.mockbuster.saml2.config.Saml2CoreConfig;

import static org.fest.assertions.Assertions.assertThat;

public class Saml2CoreTest {

    private DocumentSerializer documentSerializer;

    @Before
    public void setUp() throws Exception {
        Saml2CoreConfig saml2CoreConfig = new Saml2CoreConfig();
        JAXBContext jaxbContext = saml2CoreConfig.jaxbContext();
        Schema schema = saml2CoreConfig.schema();

        documentSerializer = new DocumentSerializer(jaxbContext, schema);
    }

    @Test
    public void testSerialize() throws Exception {
        Document document = Documents.readToDocument(
                new InputStreamReader(getClass().getResourceAsStream("/authn_request.xml"), "UTF-8"));

        AuthnRequestType authnRequest = documentSerializer.documentToObject(document, AuthnRequestType.class);

        Assertions.assertThat(authnRequest).isNotNull();
        Assertions.assertThat(authnRequest.getID()).isEqualTo("identifier_1");
        Assertions.assertThat(authnRequest.getIssuer().getValue()).isEqualTo("https://sp.example.com/SAML2");

        ObjectFactory objectFactory = new ObjectFactory();
        JAXBElement<AuthnRequestType> jaxbElement = objectFactory.createAuthnRequest(authnRequest);

        StringWriter writer = new StringWriter();
        Documents.toWriter(documentSerializer.objectToDocument(jaxbElement), writer);

        String result = writer.toString();

        assertThat(result).contains("identifier_1");
        assertThat(result).contains("https://sp.example.com/SAML2");
    }

}