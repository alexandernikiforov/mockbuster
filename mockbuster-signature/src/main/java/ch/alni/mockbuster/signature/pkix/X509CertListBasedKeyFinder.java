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

import org.slf4j.Logger;

import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import java.security.Key;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Key finder that looks up the first found X509 certificate in the key info and matches it against
 * a pre-defined list of certificates. If the certificate matches, its key is returned as the non-empty result.
 */
public class X509CertListBasedKeyFinder implements Function<KeyInfo, Optional<Key>> {
    private static final Logger LOG = getLogger(X509CertListBasedKeyFinder.class);

    private final Supplier<List<X509Certificate>> certificateListSupplier;

    public X509CertListBasedKeyFinder(Supplier<List<X509Certificate>> certificateListSupplier) {
        this.certificateListSupplier = certificateListSupplier;
    }

    @Override
    public Optional<Key> apply(KeyInfo keyInfo) {
        return findCertInKeyInfo(keyInfo)
                .filter(this::matchAgainstSuppliedList)
                .map(Certificate::getPublicKey)
                ;
    }

    private Optional<X509Certificate> findCertInKeyInfo(KeyInfo keyInfo) {
        for (Object xmlStructure : keyInfo.getContent()) {
            if (xmlStructure instanceof X509Data) {
                return findCertInX509Data((X509Data) xmlStructure);
            }

            LOG.warn("do not support xml structure of type {} for signature validation", xmlStructure.getClass().getName());
        }

        return Optional.empty();
    }

    private Optional<X509Certificate> findCertInX509Data(X509Data x509Data) {
        for (Object xmlStructure : x509Data.getContent()) {
            if (xmlStructure instanceof X509Certificate) {
                return Optional.of((X509Certificate) xmlStructure);
            }

            LOG.warn("do not support X509Data structure of type {} for signature validation", xmlStructure.getClass().getName());
        }

        return Optional.empty();
    }

    private boolean matchAgainstSuppliedList(X509Certificate x509Certificate) {
        return certificateListSupplier.get().stream().anyMatch(x509Certificate::equals);
    }

}
