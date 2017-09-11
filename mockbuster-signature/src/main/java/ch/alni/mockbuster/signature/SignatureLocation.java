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

/**
 * Strategy to find the signature in the document.
 */
public class SignatureLocation {

    private final String pathToParentNode;
    private final String pathToNextSiblingNode;

    public SignatureLocation(String pathToParentNode, String pathToNextSiblingNode) {
        this.pathToParentNode = pathToParentNode;
        this.pathToNextSiblingNode = pathToNextSiblingNode;
    }

    public SignatureLocation(String pathToParentNode) {
        this.pathToParentNode = pathToParentNode;
        this.pathToNextSiblingNode = null;
    }

    public String getPathToParentNode() {
        return pathToParentNode;
    }

    public String getPathToNextSiblingNode() {
        return pathToNextSiblingNode;
    }
}
