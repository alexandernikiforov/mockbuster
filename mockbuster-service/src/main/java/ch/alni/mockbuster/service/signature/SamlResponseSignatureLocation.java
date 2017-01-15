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

package ch.alni.mockbuster.service.signature;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Strategy to place signature on the SAML response.
 */
public class SamlResponseSignatureLocation implements SignatureLocation {

    @Override
    public Node getParentNode(Document document) {
        return document.getDocumentElement();
    }

    @Override
    public Node getNextSiblingNode(Document document) {
        // add before Status
        NodeList nodeList = document.getElementsByTagNameNS("urn:oasis:names:tc:SAML:2.0:protocol",
                "Status");

        if (nodeList.getLength() < 1) {
            throw new IllegalArgumentException("provided dom is not a valid SAML response, it does not contain Status element");
        }

        return nodeList.item(0);
    }
}
