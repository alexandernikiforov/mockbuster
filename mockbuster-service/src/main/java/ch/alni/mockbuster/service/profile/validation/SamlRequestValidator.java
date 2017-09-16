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

package ch.alni.mockbuster.service.profile.validation;

import org.oasis.saml2.protocol.RequestAbstractType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class SamlRequestValidator<T extends RequestAbstractType> {

    private List<Function<T, SamlRequestValidationResult>> validationList = new ArrayList<>();

    public SamlRequestValidator(List<Function<T, SamlRequestValidationResult>> validationList) {
        this.validationList.addAll(validationList);
    }

    public SamlRequestValidationResult validate(T request) {
        // find the first validation that is invalid or otherwise return a valid result
        return validationList.stream()
                .map(validation -> validation.apply(request))
                .filter(result -> !result.isValid())
                .findFirst()
                .orElseGet(SamlRequestValidationResultFactory::makeValid);
    }

}
