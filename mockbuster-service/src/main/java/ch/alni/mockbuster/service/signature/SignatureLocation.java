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

import java.util.Optional;

import javax.xml.crypto.dsig.XMLSignature;

/**
 * Strategy to find location of the signature.
 */
public abstract class SignatureLocation {

    public Optional<Node> findSignatureNode(Document document) {
        final Node parentNode = getParentNode(document);

        NodeList signatureNodeList = document.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature");

        for (int i = 0; i < signatureNodeList.getLength(); i++) {
            Node signatureNode = signatureNodeList.item(i);
            if (signatureNode.getParentNode().isEqualNode(parentNode) &&
                    signatureNode.getNextSibling().isEqualNode(getNextSiblingNode(document))) {

                return Optional.of(signatureNode);
            }
        }

        return Optional.empty();
    }

    public abstract Node getParentNode(Document document);

    public abstract Node getNextSiblingNode(Document document);
}
