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

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.List;

/**
 * Describes the service signature configuration.
 */
public interface SignatureConfiguration {

    /**
     * Returns the certificate chain to validate the signatures of the Mockbuster service. The first
     * certificate in the resulting list is the chain root.
     */
    List<X509Certificate> getSignatureValidatingCertPath();

    /**
     * Returns the signing key for the signatures made by the Mockbuster service.
     */
    PrivateKey getSigningKey();

    /**
     * Which signature do we want.
     */
    String getSignatureMethodUri();

    /**
     * Which digest do we want.
     */
    String getDigestMethodUri();
}
