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

import ch.alni.mockbuster.core.domain.ServiceProvider;
import ch.alni.mockbuster.saml2.SamlResponseStatus;
import ch.alni.mockbuster.service.profile.validation.SamlRequestValidationResult;
import ch.alni.mockbuster.service.profile.validation.SamlRequestValidator;
import org.oasis.saml2.protocol.LogoutRequestType;

import java.util.Arrays;

import static ch.alni.mockbuster.service.profile.validation.SamlRequestValidationResultFactory.makeInvalid;
import static ch.alni.mockbuster.service.profile.validation.SamlRequestValidationResultFactory.makeValid;

public class LogoutRequestValidation {

    private final SamlRequestValidator<LogoutRequestType> validator;


    public LogoutRequestValidation(ServiceProvider serviceProvider) {
        LogoutRequestSignatureValidation signatureValidation = new LogoutRequestSignatureValidation(serviceProvider);

        validator = new SamlRequestValidator<>(Arrays.asList(
                this::validateIdentity, signatureValidation::validateSignature
        ));
    }

    public SamlRequestValidationResult validateRequest(LogoutRequestType logoutRequestType) {
        return validator.validate(logoutRequestType);
    }

    private SamlRequestValidationResult validateIdentity(LogoutRequestType logoutRequestType) {
        if (null == logoutRequestType.getNameID()) {
            return makeInvalid(
                    "nameID element of the principal MUST be provided to establish the principal's identity",
                    SamlResponseStatus.REQUEST_DENIED
            );
        } else {
            return makeValid();
        }
    }


}
