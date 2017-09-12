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

package ch.alni.mockbuster.service.session;

import ch.alni.mockbuster.core.domain.Principal;
import org.oasis.saml2.protocol.AuthnRequestType;

import java.util.Optional;

public interface Session {

    String getIndex();

    /**
     * Stores the latest AuthnRequest.
     */
    void storeAuthnRequest(AuthnRequestType authnRequestType);

    void storeAuthnRequestWithPrincipal(AuthnRequestType authnRequestType, Principal principal);

    /**
     * Tries to find the stored AuthnRequest.
     */
    Optional<AuthnRequestType> findStoredAuthnRequest();

    Optional<Principal> findStoredPrincipal();

    void storeIdentity(Principal principal);

    Optional<Principal> findIdentity();

    void clearIdentity();

}
