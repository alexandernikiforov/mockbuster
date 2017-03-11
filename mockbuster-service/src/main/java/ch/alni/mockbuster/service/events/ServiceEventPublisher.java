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

package ch.alni.mockbuster.service.events;

import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

public final class ServiceEventPublisher {
    private static final Logger LOG = getLogger(ServiceEventPublisher.class);

    private static ServiceEventPublisher instance = new ServiceEventPublisher();

    private static volatile EventBus eventBus;

    private ServiceEventPublisher() {
    }

    public static ServiceEventPublisher getInstance() {
        return instance;
    }

    public static void connect(EventBus eventBus) {
        ServiceEventPublisher.eventBus = eventBus;

        LOG.info("service event publisher is connected to the event bus");
    }

    public void publish(ServiceEvent serviceEvent) {
        if (null != eventBus) {
            eventBus.publish(serviceEvent);
        } else {
            LOG.warn("cannot publish event {} since the event bus is not connected", serviceEvent);
        }
    }
}
