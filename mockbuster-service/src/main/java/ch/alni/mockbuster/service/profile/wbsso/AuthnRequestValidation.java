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

package ch.alni.mockbuster.service.profile.wbsso;

import ch.alni.mockbuster.core.domain.IdentityProvider;
import ch.alni.mockbuster.core.domain.ServiceProvider;
import ch.alni.mockbuster.saml2.NameIdFormat;
import ch.alni.mockbuster.saml2.SamlResponseStatus;
import ch.alni.mockbuster.service.profile.validation.SamlRequestValidationResult;
import ch.alni.mockbuster.service.profile.validation.SamlRequestValidator;
import org.apache.commons.lang.StringUtils;
import org.oasis.saml2.assertion.NameIDType;
import org.oasis.saml2.protocol.AuthnRequestType;
import org.oasis.saml2.protocol.NameIDPolicyType;

import java.util.Arrays;
import java.util.Optional;

import static ch.alni.mockbuster.saml2.SamlResponseStatus.REQUEST_DENIED;
import static ch.alni.mockbuster.service.profile.validation.SamlRequestValidationResultFactory.makeInvalid;
import static ch.alni.mockbuster.service.profile.validation.SamlRequestValidationResultFactory.makeValid;

class AuthnRequestValidation {

    private final IdentityProvider identityProvider;

    private final SamlRequestValidator<AuthnRequestType> authnRequestTypeSamlRequestValidator;

    AuthnRequestValidation(IdentityProvider identityProvider, ServiceProvider serviceProvider) {
        this.identityProvider = identityProvider;

        AuthnRequestSignatureValidation signatureValidation = new AuthnRequestSignatureValidation(identityProvider, serviceProvider);

        authnRequestTypeSamlRequestValidator =
                new SamlRequestValidator<>(Arrays.asList(
                        this::validateSubject,
                        this::validateIssuerNameIdFormat,
                        this::validateNameIdPolicy,
                        signatureValidation::validateSignature
                ));
    }

    SamlRequestValidationResult validateRequest(AuthnRequestType authnRequestType) {
        return authnRequestTypeSamlRequestValidator.validate(authnRequestType);
    }

    private SamlRequestValidationResult validateIssuerNameIdFormat(AuthnRequestType authnRequestType) {
        boolean result = Optional.ofNullable(authnRequestType.getIssuer())
                .map(NameIDType::getFormat)
                .map(format -> StringUtils.equals(NameIdFormat.ENTITY.getValue(), format))
                .orElse(true);

        return result ?
                makeValid() :
                makeInvalid(
                        "the Format attribute MUST be omitted or have a value of urn:oasis:names:tc:SAML:2.0:nameid-format:entity.",
                        REQUEST_DENIED
                );
    }

    private SamlRequestValidationResult validateNameIdPolicy(AuthnRequestType authnRequestType) {
        String format = Optional.ofNullable(authnRequestType.getNameIDPolicy())
                .map(NameIDPolicyType::getFormat)
                .orElse(NameIdFormat.UNSPECIFIED.getValue());
        if (format.equals(NameIdFormat.UNSPECIFIED.getValue()) || StringUtils.equals(identityProvider.getNameIdPolicyFormat(), format)) {
            return makeValid();
        } else {
            return makeInvalid(
                    "the content is not understood by or acceptable to the identity provider",
                    SamlResponseStatus.INVALID_NAMEID_POLICY
            );
        }
    }

    private SamlRequestValidationResult validateSubject(AuthnRequestType authnRequestType) {
        boolean subjectConfirmationFound = Optional.ofNullable(authnRequestType.getSubject())
                .map(subjectType -> subjectType.getContent().stream()
                        .anyMatch(jaxbElement -> jaxbElement.getName().getLocalPart().equals("SubjectConfirmation"))
                )
                .orElse(false);

        if (subjectConfirmationFound) {
            return makeInvalid(
                    "the element <Subject> MUST NOT contain any <SubjectConfirmation> elements",
                    REQUEST_DENIED
            );
        } else {
            return makeValid();
        }
    }


}
