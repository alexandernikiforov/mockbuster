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

package ch.alni.mockbuster.saml2;

public enum SamlResponseStatus {
    REQUEST_DENIED("urn:oasis:names:tc:SAML:2.0:status:RequestDenied"),
    AUTHN_FAILED("urn:oasis:names:tc:SAML:2.0:status:AuthnFailed"),
    SUCCESS("urn:oasis:names:tc:SAML:2.0:status:Success"),
    INVALID_NAMEID_POLICY("urn:oasis:names:tc:SAML:2.0:status:InvalidNameIDPolicy"),
    UNKNOWN_PRINCIPAL("urn:oasis:names:tc:SAML:2.0:status:UnknownPrincipal");

    private final String value;

    SamlResponseStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
