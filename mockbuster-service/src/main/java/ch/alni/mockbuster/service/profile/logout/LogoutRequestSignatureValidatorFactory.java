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
import ch.alni.mockbuster.service.profile.common.SamlRequestSignatureValidator;
import ch.alni.mockbuster.signature.xpath.XPaths;

import javax.xml.namespace.QName;

class LogoutRequestSignatureValidatorFactory {
    private final static String SIGNATURE_PATH = XPaths.toAbsolutePath(
            new QName(Saml2NamespaceUri.SAML2_PROTOCOL_NAMESPACE_URI, "LogoutRequest"),
            new QName(Saml2NamespaceUri.XMLSIG_CORE_NAMESPACE_URI, "Signature")
    );

    private LogoutRequestSignatureValidatorFactory() {
    }

    static SamlRequestSignatureValidator make() {
        return new SamlRequestSignatureValidator(SIGNATURE_PATH);
    }
}
