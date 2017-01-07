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

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.nio.charset.Charset;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;

public class Saml2Serializer {
    private final JAXBContext jaxbContext;
    private final Schema schema;

    public Saml2Serializer(JAXBContext jaxbContext, Schema schema) {
        this.jaxbContext = jaxbContext;
        this.schema = schema;
    }

    <T> T deserialize(Reader reader, Class<T> clazz) {
        try {
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            unmarshaller.setSchema(schema);

            JAXBElement<T> element = unmarshaller.unmarshal(
                    new StreamSource(reader),
                    clazz);

            return element.getValue();

        } catch (JAXBException e) {
            throw new IllegalArgumentException("cannot parse the SAML request", e);
        }
    }

    byte[] serialize(JAXBElement<?> samlObject) {
        try {
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setSchema(schema);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            marshaller.marshal(samlObject, new OutputStreamWriter(outputStream, Charset.forName("UTF-8")));

            return outputStream.toByteArray();
        } catch (JAXBException e) {
            throw new IllegalArgumentException("cannot marshall the SAML samlObject", e);
        }
    }

}
