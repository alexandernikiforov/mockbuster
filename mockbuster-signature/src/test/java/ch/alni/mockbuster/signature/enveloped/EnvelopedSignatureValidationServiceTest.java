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

package ch.alni.mockbuster.signature.enveloped;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

import java.io.InputStreamReader;

import ch.alni.mockbuster.dom.Documents;
import ch.alni.mockbuster.service.signature.SamlResponseSignatureLocation;
import ch.alni.mockbuster.signature.pkix.X509CertPathBasedKeySelector;

import static org.fest.assertions.Assertions.assertThat;

public class EnvelopedSignatureValidationServiceTest {

    private EnvelopedSignatureValidationService signatureValidation;

    @Before
    public void setUp() throws Exception {
        X509CertPathBasedKeySelector keySelector = new X509CertPathBasedKeySelector(null);

        signatureValidation = new EnvelopedSignatureValidationService(keySelector);
    }

    @Test
    public void containsValidEnvelopedSignature() throws Exception {
        Document document = Documents.readToDocument(
                new InputStreamReader(getClass().getResourceAsStream("/auth_response-test-signed.xml"), "UTF-8"));

        assertThat(signatureValidation.containsValidSignature(document, new SamlResponseSignatureLocation())).isTrue();
    }

}