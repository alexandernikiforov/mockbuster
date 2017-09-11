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

import org.oasis.saml2.protocol.ObjectFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.StringReader;
import java.io.StringWriter;

public final class Saml2ProtocolObjects {
    private final static JAXBContext jaxbContext = jaxbContext();
    private final static Schema schema = schema();

    private Saml2ProtocolObjects() {
    }

    public static <T> T unmarshal(String content, Class<T> objectType) throws JAXBException {
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        unmarshaller.setSchema(schema);

        JAXBElement<T> jaxbElement = unmarshaller.unmarshal(new StreamSource(new StringReader(content)), objectType);
        return jaxbElement.getValue();
    }

    public static Document jaxbElementToDocument(Object jaxbElement) throws JAXBException {
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setSchema(schema);

        Document document = createNewDocument();

        marshaller.marshal(jaxbElement, new DOMResult(document));

        return document;
    }

    /**
     * Transforms the given protocol document to string with validation.
     *
     * @throws JAXBException if the protocol document DOM does not represent a valid SAML2 protocol object
     */
    public static <T> String protocolDocumentToString(Document protocolDocument, Class<T> objectType) throws JAXBException {
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        unmarshaller.setSchema(schema);

        JAXBElement<T> jaxbElement = unmarshaller.unmarshal(new DOMSource(protocolDocument), objectType);

        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setSchema(schema);

        StringWriter stringWriter = new StringWriter();
        marshaller.marshal(jaxbElement, new StreamResult(stringWriter));

        return stringWriter.toString();
    }

    private static JAXBContext jaxbContext() {
        try {
            return JAXBContext.newInstance(ObjectFactory.class);
        } catch (JAXBException e) {
            throw new IllegalStateException("cannot create JAXB context", e);
        }
    }

    private static Schema schema() {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        schemaFactory.setResourceResolver(new Saml2CoreResourceResolver());

        try {
            return schemaFactory.newSchema(new Source[]{
                            new StreamSource(Saml2ProtocolObjects.class.getResourceAsStream("/saml2/saml-schema-protocol-2.0.xsd"))
                    }
            );
        } catch (SAXException e) {
            throw new IllegalStateException("cannot create schema for SAML2 validation", e);
        }
    }

    private static Document createNewDocument() {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);

        try {
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            return documentBuilder.newDocument();
        } catch (ParserConfigurationException e) {
            throw new IllegalStateException("cannot create a dom builder", e);
        }
    }
}
