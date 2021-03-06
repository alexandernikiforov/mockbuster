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

package ch.alni.mockbuster.service.messages;

import ch.alni.mockbuster.core.domain.IdentityProvider;
import ch.alni.mockbuster.core.domain.ServiceProvider;
import org.oasis.saml2.protocol.AuthnRequestType;

/**
 * Authentication request containing parsed SAML request object.
 */
public class AuthnRequest implements SamlRequest {

    private final ServiceRequest request;
    private final AuthnRequestType authnRequestType;
    private final ServiceProvider serviceProvider;
    private final IdentityProvider identityProvider;

    public AuthnRequest(ServiceRequest request, AuthnRequestType authnRequestType, ServiceProvider serviceProvider, IdentityProvider identityProvider) {
        this.request = request;
        this.authnRequestType = authnRequestType;
        this.serviceProvider = serviceProvider;
        this.identityProvider = identityProvider;
    }

    public AuthnRequestType getAuthnRequestType() {
        return authnRequestType;
    }

    public ServiceProvider getServiceProvider() {
        return serviceProvider;
    }

    public IdentityProvider getIdentityProvider() {
        return identityProvider;
    }

    @Override
    public ServiceRequest getServiceRequest() {
        return request;
    }

    @Override
    public void accept(SamlRequestVisitor visitor) {
        visitor.visit(this);
    }

}
