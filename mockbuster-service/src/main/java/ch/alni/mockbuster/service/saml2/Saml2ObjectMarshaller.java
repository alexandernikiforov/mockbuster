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

package ch.alni.mockbuster.service.saml2;

import org.oasis.saml2.protocol.ObjectFactory;
import org.w3c.dom.Document;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.dom.DOMResult;
import javax.xml.validation.Schema;

import ch.alni.mockbuster.service.dom.Documents;

public abstract class Saml2ObjectMarshaller<T> {
    private final ObjectFactory objectFactory = new ObjectFactory();

    private final JAXBContext jaxbContext;
    private final Schema schema;

    public Saml2ObjectMarshaller(JAXBContext jaxbContext, Schema schema) {
        this.jaxbContext = jaxbContext;
        this.schema = schema;
    }

    public Document objectToDocument(T samlObject) {
        try {
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setSchema(schema);

            JAXBElement<T> jaxbElement = createJaxbElement(samlObject);
            Document document = Documents.createNewDocument();

            marshaller.marshal(jaxbElement, new DOMResult(document));

            return document;
        } catch (JAXBException e) {
            throw new IllegalArgumentException("cannot marshall the SAML 2.0 samlObject", e);
        }
    }

    public final ObjectFactory getObjectFactory() {
        return objectFactory;
    }

    abstract protected JAXBElement<T> createJaxbElement(T samlObject);
}
