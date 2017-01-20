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

import org.apache.commons.lang.UnhandledException;
import org.slf4j.Logger;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXCertPathValidatorResult;
import java.security.cert.PKIXParameters;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Uses PKIX algorithm to validate a certificate path.
 */
public class PKIXCertPathValidator implements X509CertPathValidator {
    private static final Logger LOG = getLogger(PKIXCertPathValidator.class);
    private static CertificateFactory certificateFactory;
    private static CertPathValidator certPathValidator;

    static {
        try {
            certificateFactory = CertificateFactory.getInstance("X.509");
            certPathValidator = CertPathValidator.getInstance("PKIX");
        } catch (NoSuchAlgorithmException e) {
            throw new UnhandledException("cannot build certificate path validator to validate X509 cert paths", e);
        } catch (CertificateException e) {
            throw new UnhandledException("cannot build certificate factory to validate X509 cert paths", e);
        }
    }

    private final TrustStoreProvider trustStoreProvider;

    @Inject
    public PKIXCertPathValidator(TrustStoreProvider trustStoreProvider) {
        this.trustStoreProvider = trustStoreProvider;
    }

    private static Date toDate(LocalDateTime localDate) {
        return Date.from(localDate.toInstant(ZoneOffset.UTC));
    }

    @Override
    public X509CertPathValidationResult isValidOn(List<X509Certificate> x509CertificateList, LocalDateTime validityDate) {
        try {
            CertPath certPath = certificateFactory.generateCertPath(x509CertificateList);
            PKIXParameters pkixParameters = new PKIXParameters(trustStoreProvider.getTrustStore());

            pkixParameters.setRevocationEnabled(false);
            pkixParameters.setDate(toDate(validityDate));

            PKIXCertPathValidatorResult result = (PKIXCertPathValidatorResult)
                    certPathValidator.validate(certPath, pkixParameters);

            return X509CertPathValidationResult.success(result.getPublicKey());

        } catch (CertificateException e) {
            throw new IllegalArgumentException("cannot build cert path object out of the provided X509 certificates", e);
        } catch (CertPathValidatorException e) {
            LOG.info("cannot validate the provided certificate path", e);
            return X509CertPathValidationResult.failure();

        } catch (KeyStoreException e) {
            throw new IllegalStateException("cannot initialize keystore to validate X509 certificate path", e);
        } catch (InvalidAlgorithmParameterException e) {
            throw new IllegalArgumentException("invalid parameters to validate X509 certificate path", e);
        }
    }

}
