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

import ch.alni.mockbuster.service.ServiceResponse;
import ch.alni.mockbuster.service.events.ServiceEvent;
import org.oasis.saml2.protocol.AuthnRequestType;

/**
 * Is triggered when a authn request fails.
 */
public class PrincipalNotFound implements ServiceEvent {
    private final AuthnRequestType authnRequest;
    private final ServiceResponse serviceResponse;

    public PrincipalNotFound(AuthnRequestType authnRequest, ServiceResponse serviceResponse) {
        this.authnRequest = authnRequest;
        this.serviceResponse = serviceResponse;
    }

    public AuthnRequestType getAuthnRequestType() {
        return authnRequest;
    }

    public ServiceResponse getServiceResponse() {
        return serviceResponse;
    }
}
