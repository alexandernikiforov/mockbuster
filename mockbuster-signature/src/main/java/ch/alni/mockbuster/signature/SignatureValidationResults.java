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

/**
 * Functions with SignatureValidationResult.
 */
public final class SignatureValidationResults {

    private SignatureValidationResults() {
    }

    public static boolean isSignatureValid(SignatureValidationResult result, boolean signatureRequired) {
        if (result.hasPassedCoreValidation()) {
            // if validation is passed, then ok
            return true;
        } else if (result.isSignatureFound()) {
            // otherwise false if there is a signature
            return false;
        } else {
            // if there is no signature, check if the signature is required
            return !signatureRequired;
        }
    }
}
