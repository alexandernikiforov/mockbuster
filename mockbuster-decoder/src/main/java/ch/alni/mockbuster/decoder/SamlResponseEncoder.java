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

import org.apache.commons.codec.binary.Base64;
import org.oasis.saml2.protocol.ObjectFactory;
import org.oasis.saml2.protocol.ResponseType;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.xml.bind.JAXBElement;

/**
 * Encodes the SAML request.
 */
@Component
public class SamlResponseEncoder {
    private final Saml2Serializer serializer;

    private final ObjectFactory objectFactory = new ObjectFactory();

    @Inject
    public SamlResponseEncoder(Saml2Serializer serializer) {
        this.serializer = serializer;
    }

    public String encodeSamlResponse(ResponseType response) {
        JAXBElement<ResponseType> jaxbElement = objectFactory.createResponse(response);

        byte[] binaryData = serializer.serialize(jaxbElement);
        return Base64.encodeBase64String(binaryData);
    }
}
