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

package ch.alni.mockbuster.service;

public class ServiceConfiguration {

    private final String serviceId;
    private final long deliveryValidityInSeconds;
    private final long sessionNotOnOrAfterInSeconds;

    public ServiceConfiguration(String serviceId, long deliveryValidityInSeconds, long sessionNotOnOrAfterInSeconds) {
        this.serviceId = serviceId;
        this.deliveryValidityInSeconds = deliveryValidityInSeconds;
        this.sessionNotOnOrAfterInSeconds = sessionNotOnOrAfterInSeconds;
    }

    public String getServiceId() {
        return serviceId;
    }

    public long getDeliveryValidityInSeconds() {
        return deliveryValidityInSeconds;
    }

    /**
     * Returns the number of seconds after which the session established by this IDP for the SP should expire.
     * This method returns -1 if this IDP does not impose the session expiration on the SP.
     */
    public long getSessionNotOnOrAfterInSeconds() {
        return sessionNotOnOrAfterInSeconds;
    }

    public boolean isSessionPermanent() {
        return -1 == sessionNotOnOrAfterInSeconds;
    }
}
