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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public class ServiceProvider {
    private String id;

    // public ID of this service provider as would appear in the metadata
    private String entityId;

    private String displayName;

    private boolean wantAssertionSigned;

    private List<AssertionConsumerService> assertionConsumerServiceList = new ArrayList<>();

    private List<String> certificateList = new ArrayList<>();

    public ServiceProvider() {
        id = UUID.randomUUID().toString();
    }

    public ServiceProvider(String entityId, String displayName, boolean wantAssertionSigned) {
        id = UUID.randomUUID().toString();

        this.entityId = entityId;
        this.displayName = displayName;
        this.wantAssertionSigned = wantAssertionSigned;
    }

    public String getId() {
        return id;
    }

    public String getEntityId() {
        return entityId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Stream<AssertionConsumerService> getAssertionConsumerServices() {
        return assertionConsumerServiceList.stream();
    }

    public List<String> getCertificates() {
        return Collections.unmodifiableList(certificateList);
    }

    public boolean isWantAssertionSigned() {
        return wantAssertionSigned;
    }
}
