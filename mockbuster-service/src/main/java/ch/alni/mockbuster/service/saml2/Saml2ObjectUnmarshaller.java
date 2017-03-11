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

import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;

public class Saml2ObjectUnmarshaller {
    private final JAXBContext jaxbContext;
    private final Schema schema;

    public Saml2ObjectUnmarshaller(JAXBContext jaxbContext, Schema schema) {
        this.jaxbContext = jaxbContext;
        this.schema = schema;
    }

    public <T> T unmarshal(String content, Class<T> objectType) throws JAXBException {
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        unmarshaller.setSchema(schema);

        JAXBElement<T> element = unmarshaller.unmarshal(
                new StreamSource(new StringReader(content)), objectType);
        return element.getValue();
    }

}
