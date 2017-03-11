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

package ch.alni.mockbuster.service.adapter.repository;

import org.oasis.saml2.assertion.AttributeStatementType;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.transform.stream.StreamSource;

public final class AttributeStatements {
    private final static JAXBContext jaxbContext;

    static {
        try {
            jaxbContext = JAXBContext.newInstance(AttributeStatementType.class);
        } catch (JAXBException e) {
            throw new IllegalStateException("cannot initialize JAXB context", e);
        }
    }

    private AttributeStatements() {
    }

    public static AttributeStatementType toAttributeStatementType(InputStream inputStream) {
        try {
            JAXBElement<AttributeStatementType> jaxbElement = jaxbContext.createUnmarshaller()
                    .unmarshal(new StreamSource(inputStream), AttributeStatementType.class);

            return jaxbElement.getValue();
        } catch (JAXBException e) {
            throw new IllegalStateException("cannot read attribute statement", e);
        }
    }
}
