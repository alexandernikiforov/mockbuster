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

package ch.alni.mockbuster.service.profile.core;

import ch.alni.mockbuster.core.domain.AssertionConsumerService;
import ch.alni.mockbuster.core.domain.ServiceProvider;
import org.oasis.saml2.protocol.ResponseType;

/**
 * Prepared authn response.
 */
public class AuthnResponse implements IdentityServiceResponse {
    private final ResponseType responseType;
    private final ServiceProvider serviceProvider;
    private final AssertionConsumerService returnDestination;

    public AuthnResponse(ResponseType responseType,
                         ServiceProvider serviceProvider,
                         AssertionConsumerService returnDestination) {
        this.responseType = responseType;
        this.serviceProvider = serviceProvider;
        this.returnDestination = returnDestination;
    }

    public ResponseType getResponseType() {
        return responseType;
    }

    public AssertionConsumerService getReturnDestination() {
        return returnDestination;
    }

    public ServiceProvider getServiceProvider() {
        return serviceProvider;
    }
}
