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

import java.util.UUID;

public class IdentityProvider {
    private String id;

    private String entityId;

    private boolean wantAuthnRequestsSigned;

    private String nameIdPolicyFormat;

    private long deliveryValidityInSeconds;


    public IdentityProvider() {
        id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public String getEntityId() {
        return entityId;
    }

    public boolean isWantAuthnRequestsSigned() {
        return wantAuthnRequestsSigned;
    }

    /**
     * Returns the format of NameIDs the will be created by this provider.
     */
    public String getNameIdPolicyFormat() {
        return nameIdPolicyFormat;
    }

    public long getDeliveryValidityInSeconds() {
        return deliveryValidityInSeconds;
    }
}
