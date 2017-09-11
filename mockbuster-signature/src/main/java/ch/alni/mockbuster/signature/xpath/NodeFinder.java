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

package ch.alni.mockbuster.signature.xpath;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.util.Optional;

/**
 * Utility class to get nodes with XPath.
 */
public final class NodeFinder {

    public static Optional<Node> findNode(Document document, String path) {
        final XPath xPath = XPathFactory.newInstance().newXPath();
        try {
            return Optional.ofNullable((Node) xPath.compile(path).evaluate(document, XPathConstants.NODE));
        } catch (XPathExpressionException e) {
            return Optional.empty();
        }
    }

    public static Node getNode(Document document, String path) {
        final XPath xPath = XPathFactory.newInstance().newXPath();
        try {
            return (Node) xPath.compile(path).evaluate(document, XPathConstants.NODE);
        } catch (XPathExpressionException e) {
            throw new IllegalArgumentException("cannot find node for path " + path, e);
        }

    }

}
