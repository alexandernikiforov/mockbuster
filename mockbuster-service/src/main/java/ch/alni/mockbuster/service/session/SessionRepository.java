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

import ch.alni.mockbuster.core.domain.NameId;

import java.util.List;

public interface SessionRepository {

    Session getCurrentSession();

    void removePrincipalFromAllSessions(NameId nameId);

    /**
     * Removes the prinicpal info from the sessions identified by the indices in the provided list.
     *
     * @param nameId           identity ID of the principal
     * @param sessionIndexList list of session indices
     */
    void removePrincipalFromSessions(NameId nameId, List<String> sessionIndexList);
}
