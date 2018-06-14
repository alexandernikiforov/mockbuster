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

import ch.alni.mockbuster.core.domain.Principal;

public class UserAuthenticationRequiredResponse implements ServiceResponse {
    private final AuthnRequest authnRequest;
    private final Principal principal;

    public UserAuthenticationRequiredResponse(AuthnRequest authnRequest, Principal principal) {
        this.authnRequest = authnRequest;
        this.principal = principal;
    }

    public AuthnRequest getAuthnRequest() {
        return authnRequest;
    }

    public Principal getPrincipal() {
        return principal;
    }

    @Override
    public void accept(ServiceResponseVisitor visitor) {
        visitor.visit(this);
    }
}
