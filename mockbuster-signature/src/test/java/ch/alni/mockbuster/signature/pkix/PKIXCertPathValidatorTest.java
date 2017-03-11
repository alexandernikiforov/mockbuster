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

package ch.alni.mockbuster.signature.pkix;

import org.junit.Before;
import org.junit.Test;

import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class PKIXCertPathValidatorTest {
    private PKIXCertPathValidator validator;
    private KeyStore truststore;

    @Before
    public void setUp() throws Exception {
        truststore = KeyStore.getInstance("JKS");
        truststore.load(getClass().getResourceAsStream("/truststore.jks"), "test1234".toCharArray());

        validator = new PKIXCertPathValidator(() -> truststore);
    }

    @Test
    public void testDummy() throws Exception {
        final KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(getClass().getResourceAsStream("/test.p12"), "test1234".toCharArray());

        X509Certificate testCert = (X509Certificate) keyStore.getCertificate("test");
        PrivateKey privateKey = (PrivateKey) keyStore.getKey("test", "test1234".toCharArray());

        assertThat(testCert).isNotNull();
        assertThat(privateKey).isNotNull();
    }

    @Test
    public void isValidOn() throws Exception {
        final KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(getClass().getResourceAsStream("/test.jks"), "test1234".toCharArray());

        X509Certificate testCert = (X509Certificate) keyStore.getCertificate("test_cert");
        X509Certificate testCa = (X509Certificate) keyStore.getCertificate("test_ca");

        List<X509Certificate> certificateList = Arrays.asList(testCert, testCa);

        X509CertPathValidationResult result = validator.isValidOn(certificateList, LocalDateTime.of(2017, 1, 21, 0, 0, 0));

        assertThat(result.isValid()).isEqualTo(true);
    }

    @Test
    public void isValidOnOnlyIntermediate() throws Exception {
        final KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(getClass().getResourceAsStream("/test.jks"), "test1234".toCharArray());

        X509Certificate testCert = (X509Certificate) keyStore.getCertificate("test_cert");

        List<X509Certificate> certificateList = Collections.singletonList(testCert);

        X509CertPathValidationResult result = validator.isValidOn(certificateList, LocalDateTime.of(2017, 1, 21, 0, 0, 0));

        assertThat(result.isValid()).isEqualTo(true);
    }

    @Test
    public void isValidOnInvalidDate() throws Exception {
        final KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(getClass().getResourceAsStream("/test.jks"), "test1234".toCharArray());

        X509Certificate testCert = (X509Certificate) keyStore.getCertificate("test_cert");
        X509Certificate testCa = (X509Certificate) keyStore.getCertificate("test_ca");

        List<X509Certificate> certificateList = Arrays.asList(testCert, testCa);

        X509CertPathValidationResult result = validator.isValidOn(certificateList, LocalDateTime.of(2017, 1, 19, 0, 0, 0));

        assertThat(result.isValid()).isEqualTo(false);
    }


}