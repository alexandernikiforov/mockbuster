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

import ch.alni.mockbuster.signature.SignatureConfiguration;
import ch.alni.mockbuster.signature.SignatureLocation;
import ch.alni.mockbuster.signature.SignatureValidationResult;
import ch.alni.mockbuster.signature.dom.Documents;
import ch.alni.mockbuster.signature.pkix.X509CertListBasedKeyFinder;
import ch.alni.mockbuster.signature.xpath.XPaths;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.w3c.dom.Document;

import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.namespace.QName;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.slf4j.LoggerFactory.getLogger;

public class EnvelopedSignerTest {
    private static final Logger LOG = getLogger(EnvelopedSignerTest.class);

    private EnvelopedSigner envelopedSigner;
    private EnvelopedSignatureValidator envelopedSignatureValidator;

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

        envelopedSigner = new EnvelopedSigner(signatureConfiguration);

        List<X509Certificate> certificateList = Collections.singletonList(testCert);

        // validate against the same certificate as while signing
        envelopedSignatureValidator = new EnvelopedSignatureValidator(new X509CertListBasedKeyFinder(() ->
                certificateList
        ));

    }

    @Test
    public void sign() throws Exception {
        String request = IOUtils.toString(getClass().getResourceAsStream("/authn_request.xml"), "UTF-8");

        Document document = Documents.toDocument(request);

        envelopedSigner.sign(document,
                XPaths.toAbsolutePath(new QName("urn:oasis:names:tc:SAML:2.0:protocol", "AuthnRequest")),
                new SignatureLocation(
                        XPaths.toAbsolutePath(new QName("urn:oasis:names:tc:SAML:2.0:protocol", "AuthnRequest")),
                        XPaths.toAbsolutePath(
                                new QName("urn:oasis:names:tc:SAML:2.0:protocol", "AuthnRequest"),
                                new QName("urn:oasis:names:tc:SAML:2.0:assertion", "Issuer"))
                                + "/following-sibling::*[position() = 1]"
                )
        );

        String signedRequest = Documents.toString(document);

        LOG.info(signedRequest);

        SignatureValidationResult result = envelopedSignatureValidator.validateXmlSignature(document,
                XPaths.toAbsolutePath(
                        new QName("urn:oasis:names:tc:SAML:2.0:protocol", "AuthnRequest"),
                        new QName("http://www.w3.org/2000/09/xmldsig#", "Signature")));

        assertThat(result.hasPassedCoreValidation()).isTrue();
    }

}