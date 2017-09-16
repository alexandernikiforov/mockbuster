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

public enum NameIdFormat {
    ENTITY("urn:oasis:names:tc:SAML:2.0:nameid-format:entity"),
    UNSPECIFIED("urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified");

    private final String value;

    NameIdFormat(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
