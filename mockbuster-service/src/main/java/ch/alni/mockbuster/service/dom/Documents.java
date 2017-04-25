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

package ch.alni.mockbuster.service.dom;

import org.w3c.dom.Document;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * Utility class to work with DOM and strings.
 */
public final class Documents {
    // reusing dom builder for within a single thread's context
    private static final ThreadLocal<DocumentBuilder> documentBuilderThreadLocal =
            ThreadLocal.withInitial(() -> {
                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                documentBuilderFactory.setNamespaceAware(true);

                try {
                    return documentBuilderFactory.newDocumentBuilder();
                } catch (ParserConfigurationException e) {
                    throw new IllegalStateException("cannot create a dom builder", e);
                }
            });
    private static final ThreadLocal<TransformerFactory> transformerFactoryLocal =
            ThreadLocal.withInitial(TransformerFactory::newInstance);

    private Documents() {
    }

    /**
     * Creates a new document.
     */
    public static Document createNewDocument() {
        DocumentBuilder documentBuilder = Documents.documentBuilderThreadLocal.get();

        try {
            return documentBuilder.newDocument();
        } finally {
            // important
            documentBuilder.reset();
        }
    }

    public static Document toDocument(String xml) throws TransformerException {
        TransformerFactory transformerFactory = transformerFactoryLocal.get();
        StreamSource source = new StreamSource(new StringReader(xml));
        DOMResult result = new DOMResult(createNewDocument());

        Transformer transformer = transformerFactory.newTransformer();
        // pretty print
        transformer.transform(source, result);

        return (Document) result.getNode();
    }

    /**
     * Transforms this DOM representation to string.
     */
    public static String toString(Document document) throws TransformerException {
        TransformerFactory transformerFactory = transformerFactoryLocal.get();
        DOMSource source = new DOMSource(document);

        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);

        Transformer transformer = transformerFactory.newTransformer();
        // pretty print
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(source, result);

        return writer.toString();

    }
}
