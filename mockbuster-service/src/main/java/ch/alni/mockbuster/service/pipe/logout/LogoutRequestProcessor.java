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

package ch.alni.mockbuster.service.pipe.logout;

import ch.alni.mockbuster.core.domain.IdentityProvider;
import ch.alni.mockbuster.core.domain.NameId;
import ch.alni.mockbuster.saml2.LogoutResponseFactory;
import ch.alni.mockbuster.saml2.SamlResponseStatus;
import ch.alni.mockbuster.service.messages.DeniedLogoutRequest;
import ch.alni.mockbuster.service.messages.LogoutRequest;
import ch.alni.mockbuster.service.messages.LogoutResponse;
import ch.alni.mockbuster.service.messages.SamlResponse;
import ch.alni.mockbuster.service.session.SessionRepository;
import org.oasis.saml2.assertion.NameIDType;
import org.oasis.saml2.protocol.LogoutRequestType;
import org.oasis.saml2.protocol.StatusResponseType;

import java.util.List;

public final class LogoutRequestProcessor {
    private LogoutRequestProcessor() {
    }

    public static SamlResponse processLogoutRequest(SessionRepository sessionRepository, LogoutRequest logoutRequest) {
        final LogoutRequestType logoutRequestType = logoutRequest.getLogoutRequestType();
        final NameIDType nameIDType = logoutRequestType.getNameID();
        final IdentityProvider identityProvider = logoutRequest.getIdentityProvider();

        final NameId nameId = new NameId(nameIDType.getFormat(), nameIDType.getValue());

        List<String> sessionIndexList = logoutRequestType.getSessionIndex();
        if (sessionIndexList.isEmpty()) {
            sessionRepository.removePrincipalFromAllSessions(nameId);
        } else {
            sessionRepository.removePrincipalFromSessions(nameId, sessionIndexList);
        }

        final StatusResponseType statusResponseType = LogoutResponseFactory.makeStatusResponse(
                identityProvider.getEntityId(),
                logoutRequestType,
                SamlResponseStatus.SUCCESS
        );

        return new LogoutResponse(statusResponseType);
    }

    public static SamlResponse processDeniedLogoutRequest(DeniedLogoutRequest deniedLogoutRequest) {
        final IdentityProvider identityProvider = deniedLogoutRequest.getIdentityProvider();

    }
