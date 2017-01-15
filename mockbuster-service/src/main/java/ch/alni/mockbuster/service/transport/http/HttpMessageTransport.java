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

package ch.alni.mockbuster.service.transport.http;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.UnhandledException;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Optional;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import ch.alni.mockbuster.dom.Documents;
import ch.alni.mockbuster.service.binding.InvalidRequestException;
import ch.alni.mockbuster.service.binding.MessageTransport;

/**
 * Implementation for the POST transport.
 */
@Component
public class HttpMessageTransport implements MessageTransport {
    private static final Charset DOCUMENT_CHARSET = Charset.forName("UTF-8");

    @Override
    public Document processRequest(String request) {
        return Optional.ofNullable(request)
                .map(this::decodeUrlEncodedValue)
                .map(Base64::decodeBase64)
                .map(this::parseBytes)
                .orElseThrow(() -> new InvalidRequestException("incoming request is empty"));
    }

    @Override
    public String encodeResponse(Document response) {
        return Optional.ofNullable(response)
                .map(this::toByteArray)
                .map(Base64::encodeBase64String)
                .map(this::urlEncode)
                .orElseThrow(() -> new IllegalArgumentException("cannot process empty response dom"));
    }

    private Document parseBytes(byte[] bytes) {
        try (Reader reader = new InputStreamReader(new ByteArrayInputStream(bytes), DOCUMENT_CHARSET)) {
            return Documents.readToDocument(reader);
        } catch (IOException e) {
            throw new UnhandledException(e);
        } catch (SAXException e) {
            throw new InvalidRequestException("incoming request is not in XML format", e);
        }
    }

    private byte[] toByteArray(Document document) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try (Writer writer = new OutputStreamWriter(outputStream, DOCUMENT_CHARSET)) {

            Documents.toWriter(document, writer);
            return outputStream.toByteArray();

        } catch (IOException | TransformerConfigurationException e) {
            throw new UnhandledException(e);
        } catch (TransformerException e) {
            // how can that be?
            throw new UnhandledException("cannot transform response dom to string", e);
        }
    }

    private String decodeUrlEncodedValue(String request) {
        try {
            return URLDecoder.decode(request, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new UnhandledException(e);
        }
    }

    private String urlEncode(String response) {
        try {
            return URLEncoder.encode(response, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new UnhandledException(e);
        }
    }

}
