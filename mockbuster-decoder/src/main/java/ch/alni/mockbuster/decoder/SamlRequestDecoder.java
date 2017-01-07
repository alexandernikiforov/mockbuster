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
import org.apache.commons.lang.UnhandledException;
import org.oasis.saml2.protocol.RequestAbstractType;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import javax.inject.Inject;

/**
 * Decodes the SAML2 requests.
 */
@Component
public class SamlRequestDecoder {

    private final Saml2Serializer serializer;

    @Inject
    public SamlRequestDecoder(Saml2Serializer serializer) {
        this.serializer = serializer;
    }

    public <T extends RequestAbstractType> T decode(String encodedSamlRequest, Class<T> clazz) {
        byte[] request = Base64.decodeBase64(encodedSamlRequest);
        try {
            Reader reader = new InputStreamReader(new ByteArrayInputStream(request), "UTF-8");
            return serializer.deserialize(reader, clazz);

        } catch (UnsupportedEncodingException e) {
            throw new UnhandledException(e);
        }
    }

}
