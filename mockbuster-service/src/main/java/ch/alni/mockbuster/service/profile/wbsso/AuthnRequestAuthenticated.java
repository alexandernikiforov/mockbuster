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

package ch.alni.mockbuster.service.profile.wbsso;

import ch.alni.mockbuster.core.domain.Principal;
import ch.alni.mockbuster.service.ServiceResponse;
import ch.alni.mockbuster.service.events.ServiceEvent;
import org.oasis.saml2.protocol.AuthnRequestType;

/**
 * Is triggered when a authn request fails.
 */
public class AuthnRequestAuthenticated implements ServiceEvent {
    private final AuthnRequestType authnRequest;
    private final Principal principal;
    private final ServiceResponse serviceResponse;

    public AuthnRequestAuthenticated(AuthnRequestType authnRequest, Principal principal, ServiceResponse serviceResponse) {
        this.authnRequest = authnRequest;
        this.principal = principal;
        this.serviceResponse = serviceResponse;
    }

    public AuthnRequestType getAuthnRequest() {
        return authnRequest;
    }

    public ServiceResponse getServiceResponse() {
        return serviceResponse;
    }

    public Principal getPrincipal() {
        return principal;
    }
}
