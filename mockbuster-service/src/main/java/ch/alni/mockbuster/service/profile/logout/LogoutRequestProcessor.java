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

package ch.alni.mockbuster.service.profile.logout;


import ch.alni.mockbuster.core.domain.Principal;
import ch.alni.mockbuster.saml2.LogoutResponseFactory;
import ch.alni.mockbuster.saml2.SamlResponseStatus;
import ch.alni.mockbuster.service.ServiceConfiguration;
import ch.alni.mockbuster.service.ServiceResponseCallback;
import ch.alni.mockbuster.service.events.EventBus;
import ch.alni.mockbuster.service.session.Session;
import ch.alni.mockbuster.service.session.SessionRepository;
import org.oasis.saml2.protocol.LogoutRequestType;
import org.oasis.saml2.protocol.StatusResponseType;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class LogoutRequestProcessor {

    private final EventBus eventBus;
    private final LogoutResponseFactory logoutResponseFactory;
    private final SessionRepository sessionRepository;

    @Inject
    public LogoutRequestProcessor(EventBus eventBus,
                                  ServiceConfiguration serviceConfiguration,
                                  SessionRepository sessionRepository) {
        this.eventBus = eventBus;
        this.sessionRepository = sessionRepository;

        logoutResponseFactory = new LogoutResponseFactory(serviceConfiguration);
    }

    @EventListener
    public void onLogoutRequestDenied(LogoutRequestDenied event) {
        LogoutRequestType logoutRequestType = event.getLogoutRequestType();
        ServiceResponseCallback serviceResponse = event.getServiceResponse();

        StatusResponseType statusResponseType = logoutResponseFactory.makeStatusResponse(logoutRequestType,
                SamlResponseStatus.REQUEST_DENIED);

        eventBus.publish(new LogoutResponsePrepared(statusResponseType, serviceResponse));
    }

    @EventListener
    public void onLogoutRequestPrincipalIdentified(LogoutRequestPrincipalIdentified event) {
        LogoutRequestType logoutRequestType = event.getLogoutRequestType();
        ServiceResponseCallback serviceResponse = event.getServiceResponse();
        Principal principal = event.getPrincipal();

        sessionRepository.findAllSessionsForPrincipal(principal.getNameId()).forEach(Session::clearIdentity);

        StatusResponseType statusResponseType = logoutResponseFactory.makeStatusResponse(logoutRequestType,
                SamlResponseStatus.SUCCESS);

        eventBus.publish(new LogoutResponsePrepared(statusResponseType, serviceResponse));
    }

}
