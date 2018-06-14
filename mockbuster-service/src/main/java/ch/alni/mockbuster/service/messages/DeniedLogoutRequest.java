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

import ch.alni.mockbuster.saml2.SamlResponseStatus;
import org.oasis.saml2.protocol.LogoutRequestType;

/**
 * Logout request containing parsed SAML request object that could not be validated and will be denied.
 */
public class DeniedLogoutRequest implements SamlRequest {
    private final ServiceRequest request;
    private final LogoutRequestType logoutRequestType;
    private final SamlResponseStatus samlResponseStatus;

    protected DeniedLogoutRequest(ServiceRequest request, LogoutRequestType logoutRequestType, SamlResponseStatus samlResponseStatus) {
        this.request = request;
        this.logoutRequestType = logoutRequestType;
        this.samlResponseStatus = samlResponseStatus;
    }

    public LogoutRequestType getLogoutRequestType() {
        return logoutRequestType;
    }

    public SamlResponseStatus getSamlResponseStatus() {
        return samlResponseStatus;
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
