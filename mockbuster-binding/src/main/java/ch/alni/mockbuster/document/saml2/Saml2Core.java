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

package ch.alni.mockbuster.document.saml2;

import org.w3c.dom.Document;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.dom.DOMResult;
import javax.xml.validation.Schema;

import ch.alni.mockbuster.document.Documents;


public class Saml2Core {
    private final JAXBContext jaxbContext;
    private final Schema schema;

    public Saml2Core(JAXBContext jaxbContext, Schema schema) {
        this.jaxbContext = jaxbContext;
        this.schema = schema;
    }

    <T> T documentToObject(Document document, Class<T> clazz) {
        try {
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            unmarshaller.setSchema(schema);

            JAXBElement<T> element = unmarshaller.unmarshal(
                    document,
                    clazz);

            return element.getValue();

        } catch (JAXBException e) {
            throw new IllegalArgumentException("cannot parse the SAML request", e);
        }
    }

    Document objectToDocument(JAXBElement<?> samlObject) {
        try {
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setSchema(schema);

            Document document = Documents.createNewDocument();

            marshaller.marshal(samlObject, new DOMResult(document));

            return document;
        } catch (JAXBException e) {
            throw new IllegalArgumentException("cannot marshall the SAML 2.0 samlObject", e);
        }
    }

}
