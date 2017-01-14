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

import org.apache.commons.lang.Validate;

import java.security.PublicKey;

/**
 * TODO: javadoc
 */
public class X509CertPathValidationResult {
    private final boolean valid;

    private final PublicKey targetPublicKey;


    private X509CertPathValidationResult(boolean valid, PublicKey targetPublicKey) {
        this.valid = valid;
        this.targetPublicKey = targetPublicKey;
    }

    public static X509CertPathValidationResult success(PublicKey targetPublicKey) {
        return new X509CertPathValidationResult(true, targetPublicKey);
    }

    public static X509CertPathValidationResult failure() {
        return new X509CertPathValidationResult(false, null);
    }

    public boolean isValid() {
        return valid;
    }

    public PublicKey getTargetPublicKey() {
        Validate.isTrue(valid, "cannot return the target public key for failed validation");
        return targetPublicKey;
    }
}
