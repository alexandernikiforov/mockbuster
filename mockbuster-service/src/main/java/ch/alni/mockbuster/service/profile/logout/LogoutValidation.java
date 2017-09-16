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

package ch.alni.mockbuster.service.profile.logout;

import ch.alni.mockbuster.saml2.SamlResponseStatus;
import ch.alni.mockbuster.service.profile.validation.SamlRequestValidationResult;
import ch.alni.mockbuster.service.profile.validation.SamlRequestValidator;
import org.oasis.saml2.protocol.LogoutRequestType;

import java.util.Collections;

import static ch.alni.mockbuster.service.profile.validation.SamlRequestValidationResultFactory.makeInvalid;
import static ch.alni.mockbuster.service.profile.validation.SamlRequestValidationResultFactory.makeValid;

public class LogoutValidation {

    private final SamlRequestValidator<LogoutRequestType> validator =
            new SamlRequestValidator<>(Collections.singletonList(this::validateSessionIndex));

    public SamlRequestValidationResult validateRequest(LogoutRequestType logoutRequestType) {
        return validator.validate(logoutRequestType);
    }

    private SamlRequestValidationResult validateSessionIndex(LogoutRequestType logoutRequestType) {
        if (logoutRequestType.getSessionIndex().isEmpty()) {
            return makeInvalid(
                    "request MUST include at least one <SessionIndex> element",
                    SamlResponseStatus.REQUEST_DENIED
            );
        } else {
            return makeValid();
        }
    }
}
