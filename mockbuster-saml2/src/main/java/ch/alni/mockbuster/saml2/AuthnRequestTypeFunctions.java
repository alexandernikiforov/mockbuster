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

package ch.alni.mockbuster.saml2;

import org.oasis.saml2.assertion.NameIDType;
import org.oasis.saml2.protocol.AuthnRequestType;

import javax.xml.namespace.QName;
import java.util.Optional;

/**
 * Functions to work with AuthnRequestType.
 */
public final class AuthnRequestTypeFunctions {

    private AuthnRequestTypeFunctions() {
    }

    public static Optional<NameIDType> findSubjectIdentity(AuthnRequestType authnRequestType) {
        return Optional.ofNullable(authnRequestType.getSubject())
                .flatMap(subjectType -> subjectType.getContent().stream()
                        .filter(jaxbElement -> jaxbElement.getName()
                                .equals(new QName(Saml2NamespaceUri.SAML2_ASSERTION_NAMESPACE_URI, "NameID")))
                        .map(jaxbElement -> (NameIDType) jaxbElement.getValue())
                        .findFirst());
    }
}
