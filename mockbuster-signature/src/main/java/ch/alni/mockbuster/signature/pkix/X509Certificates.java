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

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

public final class X509Certificates {
    private X509Certificates() {
    }

    public static List<X509Certificate> gatherCertificates(List<String> encodedCertificates) {
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X509");
            return encodedCertificates.stream()
                    .map(encodedCertificate -> toX509Certificate(certificateFactory, encodedCertificate))
                    .collect(Collectors.toList());
        } catch (CertificateException e) {
            throw new IllegalStateException("cannot create certificate factory", e);
        }

    }

    private static X509Certificate toX509Certificate(CertificateFactory certificateFactory, String encodedCertificate) {
        byte[] decodedCertificate = Base64.getDecoder().decode(encodedCertificate);
        try {
            return (X509Certificate) certificateFactory.generateCertificate(new ByteArrayInputStream(decodedCertificate));
        } catch (CertificateException e) {
            throw new IllegalStateException("cannot decode certificate", e);
        }
    }
}
