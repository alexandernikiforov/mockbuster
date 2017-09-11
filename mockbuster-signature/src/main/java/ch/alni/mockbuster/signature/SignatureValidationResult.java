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

package ch.alni.mockbuster.signature;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Describes the result of XML signature validation.
 */
public class SignatureValidationResult {
    private final boolean signatureFound;
    private final boolean coreValidity;
    private final boolean signatureValueValidity;
    private final List<String> invalidReferences = new ArrayList<>();

    private SignatureValidationResult(Builder builder) {
        signatureFound = builder.signatureFound;
        coreValidity = builder.coreValidity;
        signatureValueValidity = builder.signatureValueValidity;
        invalidReferences.addAll(builder.invalidReferences);
    }

    /**
     * Returns true if the signature has passed the core validation.
     */
    public boolean hasPassedCoreValidation() {
        return coreValidity;
    }

    public boolean hasValidSignatureValue() {
        return signatureValueValidity;
    }

    public boolean isSignatureFound() {
        return signatureFound;
    }

    public List<String> getInvalidReferences() {
        return Collections.unmodifiableList(invalidReferences);
    }

    public static class Builder {
        private final List<String> invalidReferences = new ArrayList<>();
        private boolean signatureFound;
        private boolean coreValidity;
        private boolean signatureValueValidity;

        public Builder setSignatureFound(boolean signatureFound) {
            this.signatureFound = signatureFound;
            return this;
        }

        public Builder setCoreValidity(boolean coreValidity) {
            this.coreValidity = coreValidity;
            return this;
        }

        public Builder setSignatureValueValidity(boolean signatureValueValidity) {
            this.signatureValueValidity = signatureValueValidity;
            return this;
        }

        public Builder setInvalidReferences(Collection<String> invalidReferences) {
            this.invalidReferences.addAll(invalidReferences);
            return this;
        }

        public SignatureValidationResult build() {
            return new SignatureValidationResult(this);
        }
    }

}
