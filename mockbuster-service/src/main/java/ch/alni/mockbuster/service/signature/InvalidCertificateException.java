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

package ch.alni.mockbuster.service.signature;

import java.security.cert.X509Certificate;

/**
 * The given certificate is invalid and cannot be verified.
 */
public class InvalidCertificateException extends RuntimeException {
    private final X509Certificate certificate;

    public InvalidCertificateException(X509Certificate certificate, Exception cause) {
        super("invalid certificate: " + certificate, cause);
        this.certificate = certificate;
    }

    public InvalidCertificateException(X509Certificate certificate, String message, Exception cause) {
        super(message, cause);
        this.certificate = certificate;
    }

    public X509Certificate getCertificate() {
        return certificate;
    }
}
