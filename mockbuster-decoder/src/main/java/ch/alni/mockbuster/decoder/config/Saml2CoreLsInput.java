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

package ch.alni.mockbuster.decoder.config;

import org.apache.commons.lang.UnhandledException;
import org.w3c.dom.ls.LSInput;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

public class Saml2CoreLsInput implements LSInput {
    private static final String ENCODING = "UTF-8";
    private final String resourceName;
    private final String systemId;
    private final String baseUri;
    private final String publicId;

    Saml2CoreLsInput(String resourceName, String systemId, String baseUri, String publicId) {
        this.resourceName = resourceName;
        this.systemId = systemId;
        this.baseUri = baseUri;
        this.publicId = publicId;
    }

    @Override
    public Reader getCharacterStream() {
        try {
            return new InputStreamReader(getClass().getResourceAsStream("/saml2/" + resourceName), ENCODING);
        } catch (UnsupportedEncodingException e) {
            throw new UnhandledException(e);
        }
    }

    @Override
    public void setCharacterStream(Reader characterStream) {
    }

    @Override
    public InputStream getByteStream() {
        return null;
    }

    @Override
    public void setByteStream(InputStream byteStream) {
    }

    @Override
    public String getStringData() {
        return null;
    }

    @Override
    public void setStringData(String stringData) {
    }

    @Override
    public String getSystemId() {
        return systemId;
    }

    @Override
    public void setSystemId(String systemId) {
    }

    @Override
    public String getPublicId() {
        return publicId;
    }

    @Override
    public void setPublicId(String publicId) {
    }

    @Override
    public String getBaseURI() {
        return baseUri;
    }

    @Override
    public void setBaseURI(String baseURI) {
    }

    @Override
    public String getEncoding() {
        return ENCODING;
    }

    @Override
    public void setEncoding(String encoding) {
    }

    @Override
    public boolean getCertifiedText() {
        return false;
    }

    @Override
    public void setCertifiedText(boolean certifiedText) {
    }
}
