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
import org.junit.Test;
import org.oasis.saml2.assertion.NameIDType;
import org.oasis.saml2.assertion.SubjectType;
import org.oasis.saml2.protocol.AuthnRequestType;
import org.oasis.saml2.protocol.NameIDPolicyType;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import static org.assertj.core.api.Assertions.assertThat;

public class AuthnRequestsTest {

    private static final String NAME_ID_VALUE = "12";
    private static final String NAME_ID_FORMAT = "urn:oasis:names:tc:SAML:2.0:nameid-format:persistent";

    @Test
    public void getSubjectIdentity() throws Exception {
        JAXBElement<NameIDType> nameIDTypeJAXBElement = new JAXBElement<NameIDType>(
                new QName(Saml2NamespaceUri.SAML2_ASSERTION_NAMESPACE_URI, "NameID"),
                NameIDType.class,
                NameIDType.builder()
                        .withFormat(NAME_ID_FORMAT)
                        .withValue(NAME_ID_VALUE)
                        .build());

        AuthnRequestType authnRequestType = AuthnRequestType.builder()
                .withSubject(SubjectType.builder()
                        .withContent(nameIDTypeJAXBElement)
                        .build())
                .withNameIDPolicy(NameIDPolicyType.builder()
                        .withAllowCreate(true)
                        .build())
                .build();

        NameId subjectIdentity = AuthnRequests.getSubjectIdentity(authnRequestType).orElse(null);
        assertThat(subjectIdentity).isEqualTo(new NameId(NAME_ID_FORMAT, NAME_ID_VALUE));

    }

}