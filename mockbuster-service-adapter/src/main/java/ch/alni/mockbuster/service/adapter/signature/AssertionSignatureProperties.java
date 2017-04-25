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

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import ch.alni.mockbuster.service.saml2.Saml2NamespaceUri;
import ch.alni.mockbuster.signature.SignatureProperties;

import static ch.alni.mockbuster.service.adapter.signature.XPathUtils.path;

/**
 * Strategy to place signature on the SAML 2.0 Response.
 */
public class AssertionSignatureProperties implements SignatureProperties {
    private final static String NEXT_SIBLING =
            "/" + path(new QName(Saml2NamespaceUri.SAML2_PROTOCOL_NAMESPACE_URI, "AuthnRequest")) +
                    "/" + path(new QName(Saml2NamespaceUri.SAML2_ASSERTION_NAMESPACE_URI, "Issuer")) +
                    "/following-sibling::*[position() = 1]";

    private final static String PARENT_NODE =
            "/" + path(new QName(Saml2NamespaceUri.SAML2_PROTOCOL_NAMESPACE_URI, "Response"));

    private final Document document;
    private final String assertionId;

    public AssertionSignatureProperties(Document document, String assertionId) {
        this.document = document;
        this.assertionId = assertionId;
    }

    @Override
    public String getReferenceUri(Document document) {
        return assertionId;
    }

    @Override
    public Node getParentNode(Document document) {
        return this.document.getDocumentElement();
    }

    @Override
    public Optional<Node> findNextSiblingNode(Document document) {
        XPath xPath = XPathFactory.newInstance().newXPath();
        return findNextSibling(xPath, this.document);
    }

    private Optional<Node> findNextSibling(XPath xPath, Document document) {
        QName authnRequestQName = new QName(Saml2NamespaceUri.SAML2_PROTOCOL_NAMESPACE_URI, "Response");
        return Stream.of(
                new QName(Saml2NamespaceUri.SAML2_PROTOCOL_NAMESPACE_URI, "Extensions"),
                new QName(Saml2NamespaceUri.SAML2_ASSERTION_NAMESPACE_URI, "Subject"),
                new QName(Saml2NamespaceUri.SAML2_PROTOCOL_NAMESPACE_URI, "NameIDPolicy"),
                new QName(Saml2NamespaceUri.SAML2_ASSERTION_NAMESPACE_URI, "Conditions"),
                new QName(Saml2NamespaceUri.SAML2_PROTOCOL_NAMESPACE_URI, "RequestedAuthnContext"),
                new QName(Saml2NamespaceUri.SAML2_PROTOCOL_NAMESPACE_URI, "Scoping")
        )
                .map(qName -> ElementPath.startWithElement(authnRequestQName)
                        .add(qName)
                        .evaluateAsNode(document, xPath)
                )

                .filter(Objects::nonNull)
                .findFirst();

    }

}
