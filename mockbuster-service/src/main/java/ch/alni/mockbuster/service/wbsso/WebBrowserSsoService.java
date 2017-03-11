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

package ch.alni.mockbuster.service.wbsso;

import org.springframework.stereotype.Service;

import javax.inject.Inject;

import ch.alni.mockbuster.service.MockbusterService;
import ch.alni.mockbuster.service.ServiceRequest;
import ch.alni.mockbuster.service.ServiceResponse;
import ch.alni.mockbuster.service.events.EventBus;

@Service
class WebBrowserSsoService implements MockbusterService {
    private final EventBus eventBus;

    @Inject
    WebBrowserSsoService(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void authenticate(ServiceRequest serviceRequest, ServiceResponse serviceResponse) {
        eventBus.publish(new AuthnRequestReceived(serviceRequest, serviceResponse));
    }

    @Override
    public void logout(ServiceRequest serviceRequest, ServiceResponse serviceResponse) {
        eventBus.publish(new LogoutRequestReceived(serviceRequest, serviceResponse));
    }
}
