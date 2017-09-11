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

package ch.alni.mockbuster.service.profile.logout;

import ch.alni.mockbuster.saml2.Saml2NamespaceUri;
import ch.alni.mockbuster.signature.SignatureLocation;
import ch.alni.mockbuster.signature.enveloped.EnvelopedSigner;
import ch.alni.mockbuster.signature.xpath.XPaths;
import org.w3c.dom.Document;

import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;


public class LogoutResponseSigner {
    // where to place signature
    private final static String RESPONSE_NODE_PATH = XPaths.toAbsolutePath(
            new QName(Saml2NamespaceUri.SAML2_PROTOCOL_NAMESPACE_URI, "LogoutResponse"));

    // next sibling is
    private final static String RESPONSE_NEXT_SIBLING_PATH = XPaths.toAbsolutePath(
            new QName(Saml2NamespaceUri.SAML2_PROTOCOL_NAMESPACE_URI, "LogoutResponse"),
            new QName(Saml2NamespaceUri.SAML2_ASSERTION_NAMESPACE_URI, "Issuer"))
            + "/following-sibling::*[position() = 1]";

    private final EnvelopedSigner envelopedSigner;

    public LogoutResponseSigner(EnvelopedSigner envelopedSigner) {
        this.envelopedSigner = envelopedSigner;
    }

    public void signResponse(Document responseDocument) throws JAXBException {
        // sign the response itself
        envelopedSigner.sign(responseDocument,
                RESPONSE_NODE_PATH,
                new SignatureLocation(RESPONSE_NODE_PATH, RESPONSE_NEXT_SIBLING_PATH));
    }
}
