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

package ch.alni.mockbuster.core;

import org.oasis.saml2.assertion.AttributeStatementType;

/**
 * How to represent a principal
 */
public class Principal {

    private final String nameId;
    private final String nameIdFormat;
    private final String displayRepresentation;

    private final AttributeStatementType attributeStatementType;

    public Principal(String nameId, String nameIdFormat, String displayRepresentation, AttributeStatementType attributeStatementType) {
        this.nameId = nameId;
        this.nameIdFormat = nameIdFormat;
        this.displayRepresentation = displayRepresentation;
        this.attributeStatementType = attributeStatementType;
    }

    public String getNameId() {
        return nameId;
    }

    public String getDisplayRepresentation() {
        return displayRepresentation;
    }

    public AttributeStatementType getAttributeStatementType() {
        return attributeStatementType;
    }

    public String getNameIdFormat() {
        return nameIdFormat;
    }
}
