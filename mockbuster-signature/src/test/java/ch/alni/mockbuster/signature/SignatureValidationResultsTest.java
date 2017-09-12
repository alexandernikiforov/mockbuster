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

import org.junit.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class SignatureValidationResultsTest {

    @Test
    public void testIsSignatureValidWithNotFound() throws Exception {
        SignatureValidationResult result = SignatureValidationResultFactory.makeNotFound();

        assertThat(SignatureValidationResults.isSignatureValid(result, true)).isFalse();
        assertThat(SignatureValidationResults.isSignatureValid(result, false)).isTrue();
    }

    @Test
    public void testIsSignatureValidWithValid() throws Exception {
        SignatureValidationResult result = SignatureValidationResultFactory.makeValid();

        assertThat(SignatureValidationResults.isSignatureValid(result, true)).isTrue();
        assertThat(SignatureValidationResults.isSignatureValid(result, false)).isTrue();
    }

    @Test
    public void testIsSignatureValidWithInvalid() throws Exception {
        SignatureValidationResult result = SignatureValidationResultFactory.makeInvalid(
                false, Arrays.asList("ref1", "ref2")
        );

        assertThat(SignatureValidationResults.isSignatureValid(result, true)).isFalse();
        assertThat(SignatureValidationResults.isSignatureValid(result, false)).isFalse();
    }


}