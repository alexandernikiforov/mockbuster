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

import ch.alni.mockbuster.saml2.SamlResponseStatus;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SamlRequestValidationResult {
    private final boolean valid;
    private final List<String> errorMessages = new ArrayList<>();
    private final SamlResponseStatus responseStatus;

    SamlRequestValidationResult(boolean valid, List<String> errorMessages, SamlResponseStatus responseStatus) {
        this.valid = valid;
        this.responseStatus = responseStatus;
        this.errorMessages.addAll(errorMessages);
    }

    public boolean isValid() {
        return valid;
    }

    public List<String> getErrorMessages() {
        return Collections.unmodifiableList(errorMessages);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    /**
     * Response status that should be returned when validation ends with the given result.
     */
    public SamlResponseStatus getResponseStatus() {
        return responseStatus;
    }
}
