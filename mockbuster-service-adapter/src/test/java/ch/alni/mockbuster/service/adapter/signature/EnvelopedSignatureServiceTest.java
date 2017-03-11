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

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.w3c.dom.Document;

import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.List;

import javax.xml.crypto.dsig.DigestMethod;

import ch.alni.mockbuster.service.dom.Documents;
import ch.alni.mockbuster.signature.enveloped.EnvelopedSignatureService;
import ch.alni.mockbuster.signature.enveloped.EnvelopedSignatureValidationService;
import ch.alni.mockbuster.signature.enveloped.SignatureConfiguration;
import ch.alni.mockbuster.signature.pkix.PKIXCertPathValidator;
import ch.alni.mockbuster.signature.pkix.X509CertPathBasedKeySelector;

import static org.fest.assertions.Assertions.assertThat;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Candidate for the integration test.
 */
public class EnvelopedSignatureServiceTest {
    private static final Logger LOG = getLogger(EnvelopedSignatureServiceTest.class);

    private EnvelopedSignatureService signatureService;
    private EnvelopedSignatureValidationService signatureValidationService;

    @Before
    public void setUp() throws Exception {
        final KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(getClass().getResourceAsStream("/test.p12"), "test1234".toCharArray());

        final X509Certificate testCert = (X509Certificate) keyStore.getCertificate("test");
        final PrivateKey privateKey = (PrivateKey) keyStore.getKey("test", "test1234".toCharArray());

        SignatureConfiguration signatureConfiguration = new SignatureConfiguration() {

            @Override
            public List<X509Certificate> getSignatureValidatingCertPath() {
                return Collections.singletonList(testCert);
            }

            @Override
            public PrivateKey getSigningKey() {
                return privateKey;
            }

            @Override
            public String getSignatureMethodUri() {
                return "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256";
            }

            @Override
            public String getDigestMethodUri() {
                return DigestMethod.SHA256;
            }
        };

        signatureService = new EnvelopedSignatureService(signatureConfiguration);

        KeyStore truststore = KeyStore.getInstance("JKS");
        truststore.load(getClass().getResourceAsStream("/truststore.jks"), "test1234".toCharArray());

        PKIXCertPathValidator validator = new PKIXCertPathValidator(() -> truststore);
        X509CertPathBasedKeySelector keySelector = new X509CertPathBasedKeySelector(validator);

        signatureValidationService = new EnvelopedSignatureValidationService(keySelector);

    }

    @Test
    public void sign() throws Exception {
        String request = IOUtils.toString(getClass().getResourceAsStream("/authn_request.xml"), "UTF-8");

        Document document = Documents.toDocument(request);

        signatureService.sign(document, new AuthnRequestSignatureProperties(document));

        String signedRequest = Documents.toString(document);

        LOG.info(signedRequest);

        boolean result = signatureValidationService.containsValidSignature(document, new AuthnRequestSignatureLocation(document));

        assertThat(result).isTrue();
    }

}