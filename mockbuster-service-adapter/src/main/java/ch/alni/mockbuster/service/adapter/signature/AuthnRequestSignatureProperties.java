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

import javax.xml.namespace.QName;

import ch.alni.mockbuster.service.saml2.Saml2NamespaceUri;

import static ch.alni.mockbuster.service.adapter.signature.XPathUtils.path;

/**
 * Strategy to place signature on the SAML 2.0 AuthnRequest.
 */
public class AuthnRequestSignatureProperties extends AbstractSignatureProperties {
    private final static String NEXT_SIBLING =
            "/" + path(new QName(Saml2NamespaceUri.SAML2_PROTOCOL_NAMESPACE_URI, "AuthnRequest")) +
                    "/" + path(new QName(Saml2NamespaceUri.SAML2_ASSERTION_NAMESPACE_URI, "Issuer")) +
                    "/following-sibling::*[position() = 1]";

    private final static String PARENT_NODE =
            "/" + path(new QName(Saml2NamespaceUri.SAML2_PROTOCOL_NAMESPACE_URI, "AuthnRequest"));

    public AuthnRequestSignatureProperties() {
        super(PARENT_NODE, PARENT_NODE, NEXT_SIBLING);
    }

}
