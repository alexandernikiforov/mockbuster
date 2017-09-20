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

package ch.alni.mockbuster.service.profile.wbsso;

import ch.alni.mockbuster.saml2.Saml2NamespaceUri;
import ch.alni.mockbuster.signature.SignatureLocation;
import ch.alni.mockbuster.signature.enveloped.EnvelopedSigner;
import ch.alni.mockbuster.signature.xpath.XPaths;
import org.w3c.dom.Document;

import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import java.text.MessageFormat;


class ResponseSigner {
    // where to place signature
    private final static String RESPONSE_NODE_PATH = XPaths.toAbsolutePath(
            new QName(Saml2NamespaceUri.SAML2_PROTOCOL_NAMESPACE_URI, "Response"));

    private final static String ASSERTION_NODE_PATH_TEMPLATE = XPaths.toAbsolutePath(
            new QName(Saml2NamespaceUri.SAML2_PROTOCOL_NAMESPACE_URI, "Response"),
            new QName(Saml2NamespaceUri.SAML2_ASSERTION_NAMESPACE_URI, "Assertion"))
            + "[{0}]";

    // next sibling is
    private final static String RESPONSE_NEXT_SIBLING_PATH = XPaths.toAbsolutePath(
            new QName(Saml2NamespaceUri.SAML2_PROTOCOL_NAMESPACE_URI, "Response"),
            new QName(Saml2NamespaceUri.SAML2_ASSERTION_NAMESPACE_URI, "Issuer"))
            + "/following-sibling::*[position() = 1]";

    private final static String ASSERTION_NEXT_SIBLING_PATH_TEMPLATE = XPaths.toAbsolutePath(
            new QName(Saml2NamespaceUri.SAML2_PROTOCOL_NAMESPACE_URI, "Response"),
            new QName(Saml2NamespaceUri.SAML2_ASSERTION_NAMESPACE_URI, "Assertion"))
            + "[{0}]"
            + XPaths.toAbsolutePath(new QName(Saml2NamespaceUri.SAML2_ASSERTION_NAMESPACE_URI, "Issuer"))
            + "/following-sibling::*[position() = 1]";

    private final EnvelopedSigner envelopedSigner;

    ResponseSigner(EnvelopedSigner envelopedSigner) {
        this.envelopedSigner = envelopedSigner;
    }

    void signResponse(Document responseDocument) {
        // sign the response itself
        envelopedSigner.sign(responseDocument,
                RESPONSE_NODE_PATH,
                new SignatureLocation(RESPONSE_NODE_PATH, RESPONSE_NEXT_SIBLING_PATH));
    }

    /**
     * Signs assertion in this document.
     *
     * @param responseDocument the basis document
     * @param index            index of the assertion (beginning with 1)
     * @throws JAXBException
     */
    void signAssertion(Document responseDocument, int index) {
        final String assertionNodePath = MessageFormat.format(ASSERTION_NODE_PATH_TEMPLATE, index);
        final String assertionNextSiblingPath = MessageFormat.format(ASSERTION_NEXT_SIBLING_PATH_TEMPLATE, index);

        envelopedSigner.sign(responseDocument, assertionNodePath, new SignatureLocation(assertionNodePath, assertionNextSiblingPath));
    }

}
