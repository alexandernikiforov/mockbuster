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

import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;

/**
 * Path evaluation with fluent interface.
 */
public class ElementPath {
    private List<String> elementList = new ArrayList<>();

    private ElementPath() {
    }

    public static ElementPath startWithElement(QName qName) {
        ElementPath elementPath = new ElementPath();
        elementPath.elementList.add(createElement(qName));
        return elementPath;
    }

    private static String createElement(QName qName) {
        return format("*[local-name()='%s' and namespace-uri()='%s']", qName.getLocalPart(), qName.getNamespaceURI());
    }

    public ElementPath add(QName qName) {
        elementList.add(createElement(qName));
        return this;
    }

    public Node evaluateAsNode(Document document, XPath xPath) {
        try {
            return (Node) xPath.compile(toElementPath()).evaluate(document, XPathConstants.NODE);
        } catch (XPathExpressionException e) {
            return null;
        }
    }

    private String toElementPath() {
        return elementList.stream()
                .map(element -> "/" + element)
                .collect(Collectors.joining());
    }


}
