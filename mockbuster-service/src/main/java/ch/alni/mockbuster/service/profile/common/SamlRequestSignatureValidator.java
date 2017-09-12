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

package ch.alni.mockbuster.service.profile.common;

import ch.alni.mockbuster.signature.SignatureValidationResult;
import ch.alni.mockbuster.signature.SignatureValidationResults;
import ch.alni.mockbuster.signature.enveloped.EnvelopedSignatureValidator;
import ch.alni.mockbuster.signature.pkix.X509CertListBasedKeyFinder;
import org.w3c.dom.Document;

import java.security.cert.X509Certificate;
import java.util.List;

public class SamlRequestSignatureValidator {
    private final EnvelopedSignatureValidator validator = new EnvelopedSignatureValidator();

    private final String signaturePath;

    public SamlRequestSignatureValidator(String signaturePath) {
        this.signaturePath = signaturePath;
    }

    public boolean validateSignature(Document authnRequestDocument, List<X509Certificate> certificateList, boolean wantRequestSigned) {
        X509CertListBasedKeyFinder keyFinder = new X509CertListBasedKeyFinder(() -> certificateList);

        SignatureValidationResult result = validator.validateXmlSignature(authnRequestDocument, signaturePath, keyFinder);

        return SignatureValidationResults.isSignatureValid(result, wantRequestSigned);
    }

}