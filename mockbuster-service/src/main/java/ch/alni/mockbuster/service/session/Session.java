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
import java.util.Set;

public interface Session {

    String getIndex();

    /**
     * Stores the latest AuthnRequest.
     */
    void storeAuthnRequest(AuthnRequestType authnRequestType);

    /**
     * Returns stored principal identities.
     *
     * @return
     */
    Set<Principal> getIdentities();

    /**
     * Returns the session timeout value in seconds. The session is permanent if this value is null.
     */
    Long getTimeoutInSeconds();

    /**
     * Tries to find the stored AuthnRequest.
     */
    Optional<AuthnRequestType> findStoredAuthnRequest();

    void storeIdentity(Principal principal);

    /**
     * Removes identity for this principal from the given session.
     */
    void clearIdentity(Principal principal);

}
