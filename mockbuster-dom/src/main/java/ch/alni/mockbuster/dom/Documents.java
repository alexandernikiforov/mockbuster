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

package ch.alni.mockbuster.dom;

import org.apache.commons.lang.UnhandledException;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Static utility class to work with DOM documents.
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

    public static void toWriter(Document document, Writer writer) throws TransformerException {
        TransformerFactory transformerFactory = transformerFactoryLocal.get();
        DOMSource source = new DOMSource(document);

        try {
            StreamResult result = new StreamResult(writer);

            Transformer transformer = transformerFactory.newTransformer();
            // pretty print
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(source, result);

        } catch (TransformerConfigurationException e) {
            throw new UnhandledException(e);
        }
    }

    public static Document readToDocument(Reader reader) throws IOException, SAXException {
        DocumentBuilder documentBuilder = documentBuilderThreadLocal.get();

        try {
            return documentBuilder.parse(new InputSource(reader));
        } finally {
            // important
            documentBuilder.reset();
        }
    }

    public static Document createNewDocument() {
        DocumentBuilder documentBuilder = documentBuilderThreadLocal.get();

        try {
            return documentBuilder.newDocument();
        } finally {
            // important
            documentBuilder.reset();
        }
    }
}
