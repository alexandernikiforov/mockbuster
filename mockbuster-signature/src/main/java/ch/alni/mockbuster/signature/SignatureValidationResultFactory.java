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

import java.util.List;

public final class SignatureValidationResultFactory {
    private static final SignatureValidationResult NOT_FOUND =
            new SignatureValidationResult.Builder()
                    .setSignatureFound(false)
                    .setCoreValidity(false)
                    .setSignatureValueValidity(false)
                    .build();

    private static final SignatureValidationResult VALID =
            new SignatureValidationResult.Builder()
                    .setSignatureFound(true)
                    .setCoreValidity(true)
                    .setSignatureValueValidity(true)
                    .build();

    private SignatureValidationResultFactory() {
    }

    public static SignatureValidationResult makeValid() {
        return VALID;
    }

    public static SignatureValidationResult makeNotFound() {
        return NOT_FOUND;
    }

    public static SignatureValidationResult makeInvalid(boolean signatureValueValidity, List<String> invalidReferences) {
        return new SignatureValidationResult.Builder()
                .setSignatureFound(true)
                .setCoreValidity(false)
                .setSignatureValueValidity(signatureValueValidity)
                .setInvalidReferences(invalidReferences)
                .build();
    }

}
