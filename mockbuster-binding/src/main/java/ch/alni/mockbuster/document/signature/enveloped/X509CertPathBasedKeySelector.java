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

package ch.alni.mockbuster.document.signature.enveloped;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.security.PublicKey;
import java.time.LocalDate;

import javax.inject.Inject;
import javax.xml.crypto.AlgorithmMethod;
import javax.xml.crypto.KeySelector;
import javax.xml.crypto.KeySelectorException;
import javax.xml.crypto.KeySelectorResult;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;

import static java.util.function.Function.identity;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Key selector that expects a chain of X509 certificates with the first one containing the public key to
 * be used to verify the signature.
 */
@Component
public class X509CertPathBasedKeySelector extends KeySelector {
    private static final Logger LOG = getLogger(X509CertPathBasedKeySelector.class);

    private final X509CertPathValidator x509CertPathValidator;

    @Inject
    public X509CertPathBasedKeySelector(X509CertPathValidator x509CertPathValidator) {
        this.x509CertPathValidator = x509CertPathValidator;
    }

    @Override
    public KeySelectorResult select(KeyInfo keyInfo, Purpose purpose, AlgorithmMethod method, XMLCryptoContext context)
            throws KeySelectorException {

        LocalDate today = LocalDate.now();

        return new X509CertPaths(keyInfo).stream()
                .map(x509CertificateList -> x509CertPathValidator.isValidOn(x509CertificateList, today))
                .filter(X509CertPathValidationResult::isValid)
                .map(result -> result.getTargetPublicKey())
                .filter(publicKey -> canBeUsedToValidateSignature(publicKey, method))
                .map(publicKey -> (KeySelectorResult) identity())
                .findFirst()

                // if we cannot find it, this is not an error (?)
                .orElse(null)
                ;
    }

    private boolean canBeUsedToValidateSignature(PublicKey publicKey, AlgorithmMethod algorithmMethod) {
        SignatureMethod signatureMethod = (SignatureMethod) algorithmMethod;
        String signatureAlgorithm = signatureMethod.getAlgorithm();
        String certificateAlgorithm = publicKey.getAlgorithm();

        return StringUtils.containsIgnoreCase(signatureAlgorithm, certificateAlgorithm);
    }
}
