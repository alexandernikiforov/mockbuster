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

package ch.alni.mockbuster.service.pipe;

import ch.alni.mockbuster.service.messages.*;
import ch.alni.mockbuster.service.pipe.logout.LogoutRequestProcessor;
import ch.alni.mockbuster.service.pipe.wbsso.AuthnRequestProcessor;
import ch.alni.mockbuster.service.pipe.wbsso.DeniedAuthnRequestProcessor;
import ch.alni.mockbuster.service.session.Session;
import ch.alni.mockbuster.service.session.SessionRepository;

public class Processor {

    private final SessionRepository sessionRepository;

    public Processor(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    /**
     * Processes the given SAML request and produces a SAML response.
     */
    public SamlResponse process(SamlRequest samlRequest) {
        // create a new dispatcher for each request
        SamlRequestDispatcher dispatcher = new SamlRequestDispatcher();
        samlRequest.accept(dispatcher);

        return dispatcher.result;
    }

    private class SamlRequestDispatcher implements SamlRequestVisitor {

        private SamlResponse result;

        @Override
        public void visit(AuthnRequest authnRequest) {
            final Session session = sessionRepository.getCurrentSession();
            result = AuthnRequestProcessor.processAuthRequest(session, authnRequest);
        }

        @Override
        public void visit(DeniedAuthnRequest deniedAuthnRequest) {
            result = DeniedAuthnRequestProcessor.processDeniedAuthnRequest(deniedAuthnRequest);
        }

        @Override
        public void visit(LogoutRequest logoutRequest) {
            result = LogoutRequestProcessor.processLogoutRequest(sessionRepository, logoutRequest);
        }

        @Override
        public void visit(DeniedLogoutRequest deniedLogoutRequest) {

        }
    }

}
