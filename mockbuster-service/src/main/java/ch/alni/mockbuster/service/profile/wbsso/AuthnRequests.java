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

import ch.alni.mockbuster.core.domain.NameId;
import ch.alni.mockbuster.saml2.Saml2NamespaceUri;
import org.oasis.saml2.assertion.NameIDType;
import org.oasis.saml2.assertion.SubjectType;
import org.oasis.saml2.protocol.AuthnRequestType;

import javax.xml.namespace.QName;
import java.util.Optional;

public final class AuthnRequests {

    private AuthnRequests() {
    }

    public static Optional<NameId> getSubjectIdentity(AuthnRequestType authnRequestType) {
        return Optional.ofNullable(authnRequestType.getSubject())
                .flatMap(AuthnRequests::findNameIDType)
                .map(nameIDType -> new NameId(nameIDType.getFormat(), nameIDType.getValue()));
    }

    public static boolean isAllowCreate(AuthnRequestType authnRequestType) {
        return true;
    }

    private static Optional<NameIDType> findNameIDType(SubjectType subjectType) {
        return subjectType.getContent().stream()
                .filter(jaxbElement -> jaxbElement.getName()
                        .equals(new QName(Saml2NamespaceUri.SAML2_ASSERTION_NAMESPACE_URI, "NameID")))
                .map(jaxbElement -> (NameIDType) jaxbElement.getValue())
                .findFirst();
    }
}
