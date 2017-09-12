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

package ch.alni.mockbuster.core.domain;

/**
 * How to represent a principal.
 */
public class Principal {

    private final NameId nameId;
    private final String displayRepresentation;

    private final String attributeStatement;

    public Principal(NameId nameId, String displayRepresentation, String attributeStatement) {
        this.nameId = nameId;
        this.displayRepresentation = displayRepresentation;
        this.attributeStatement = attributeStatement;
    }

    public NameId getNameId() {
        return nameId;
    }

    public String getAttributeStatement() {
        return attributeStatement;
    }

    public String getDisplayRepresentation() {
        return displayRepresentation;
    }
}
