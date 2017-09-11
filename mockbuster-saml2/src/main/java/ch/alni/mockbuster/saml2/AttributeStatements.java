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

import org.oasis.saml2.assertion.AttributeStatementType;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.InputStream;
import java.io.StringReader;

public final class AttributeStatements {
    private final static JAXBContext jaxbContext;
    private final static Schema schema;

    static {
        try {
            jaxbContext = JAXBContext.newInstance(AttributeStatementType.class);
            schema = schema();

        } catch (JAXBException e) {
            throw new IllegalStateException("cannot initialize JAXB context", e);
        }
    }

    private AttributeStatements() {
    }

    public static AttributeStatementType toAttributeStatementType(InputStream inputStream) {
        try {
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            unmarshaller.setSchema(schema);

            JAXBElement<AttributeStatementType> jaxbElement = unmarshaller
                    .unmarshal(new StreamSource(inputStream), AttributeStatementType.class);

            return jaxbElement.getValue();
        } catch (JAXBException e) {
            throw new IllegalStateException("cannot read attribute statement", e);
        }
    }

    public static AttributeStatementType toAttributeStatementType(String attributeStatement) {
        try {
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            unmarshaller.setSchema(schema);

            JAXBElement<AttributeStatementType> jaxbElement = unmarshaller
                    .unmarshal(new StreamSource(new StringReader(attributeStatement)), AttributeStatementType.class);

            return jaxbElement.getValue();
        } catch (JAXBException e) {
            throw new IllegalStateException("cannot read attribute statement", e);
        }
    }


    private static Schema schema() {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        schemaFactory.setResourceResolver(new Saml2CoreResourceResolver());

        try {
            return schemaFactory.newSchema(new Source[]{
                            new StreamSource(Saml2ProtocolObjects.class.getResourceAsStream("/saml2/saml-schema-assertion-2.0.xsd"))
                    }
            );
        } catch (SAXException e) {
            throw new IllegalStateException("cannot create schema for SAML2 attribute statement validation", e);
        }
    }

}
