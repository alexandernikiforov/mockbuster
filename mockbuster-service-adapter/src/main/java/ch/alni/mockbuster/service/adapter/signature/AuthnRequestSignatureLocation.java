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

package ch.alni.mockbuster.service.adapter.signature;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.Optional;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import ch.alni.mockbuster.service.saml2.Saml2NamespaceUri;
import ch.alni.mockbuster.signature.SignatureLocation;

/**
 * Location of the signature in SAML 2.0 AuthnRequest
 */
public class AuthnRequestSignatureLocation implements SignatureLocation {
    private final Document document;

    public AuthnRequestSignatureLocation(Document document) {
        this.document = document;
    }

    @Override
    public Optional<Node> findSignatureNode() {
        XPath xPath = XPathFactory.newInstance().newXPath();

        QName authnRequestQName = new QName(Saml2NamespaceUri.SAML2_PROTOCOL_NAMESPACE_URI, "AuthnRequest");
        QName signatureQName = new QName(Saml2NamespaceUri.XMLSIG_CORE_NAMESPACE_URI, "Signature");

        Node node = ElementPath.startWithElement(authnRequestQName).add(signatureQName).evaluateAsNode(document, xPath);

        return Optional.ofNullable(node);
    }
}
