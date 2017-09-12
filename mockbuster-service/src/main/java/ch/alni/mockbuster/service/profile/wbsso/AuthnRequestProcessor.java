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

import ch.alni.mockbuster.core.domain.Principal;
import ch.alni.mockbuster.service.ServiceResponse;
import ch.alni.mockbuster.service.events.EventBus;
import ch.alni.mockbuster.service.profile.common.SamlResponseStatus;
import ch.alni.mockbuster.service.session.Session;
import ch.alni.mockbuster.service.session.SessionRepository;
import org.oasis.saml2.protocol.AuthnRequestType;
import org.oasis.saml2.protocol.ResponseType;
import org.slf4j.Logger;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

import static org.slf4j.LoggerFactory.getLogger;

@Component
public class AuthnRequestProcessor {
    private static final Logger LOG = getLogger(AuthnRequestProcessor.class);

    private final ResponseFactory responseFactory;
    private final SessionRepository sessionRepository;
    private final EventBus eventBus;

    @Inject
    public AuthnRequestProcessor(ResponseFactory responseFactory,
                                 SessionRepository sessionRepository,
                                 EventBus eventBus) {
        this.sessionRepository = sessionRepository;
        this.eventBus = eventBus;
        this.responseFactory = responseFactory;
    }

    @EventListener
    public void onAuthnRequestAuthenticated(AuthnRequestAuthenticated event) {
        AuthnRequestType authnRequest = event.getAuthnRequest();
        ServiceResponse serviceResponse = event.getServiceResponse();
        Principal principal = event.getPrincipal();

        Session currentSession = sessionRepository.getCurrentSession();
        currentSession.storeIdentity(principal);

        // create the response
        ResponseType responseType = responseFactory.makeResponse(authnRequest, principal, currentSession);

        eventBus.publish(new AuthnResponsePrepared(responseType, serviceResponse));
    }

    @EventListener
    public void onPrincipalAuthenticationRequired(PrincipalAuthenticationRequired event) {
        AuthnRequestType authnRequestType = event.getAuthnRequest();
        ServiceResponse serviceResponse = event.getServiceResponse();
        Principal principal = event.getPrincipal();

        Session currentSession = sessionRepository.getCurrentSession();
        currentSession.storeAuthnRequestWithPrincipal(authnRequestType, principal);

        serviceResponse.sendUserInteractionRequired();
    }

    @EventListener
    public void onAuthnRequestInvalidSignature(AuthnRequestIncorrectlySigned event) {
        AuthnRequestType authnRequestType = event.getAuthnRequestType();
        ServiceResponse serviceResponse = event.getServiceResponse();

        ResponseType responseType = responseFactory.makeResponse(authnRequestType, SamlResponseStatus.REQUEST_DENIED);

        eventBus.publish(new AuthnResponsePrepared(responseType, serviceResponse));
    }

    @EventListener
    public void onPrincipalNotFound(PrincipalNotFound event) {
        AuthnRequestType authnRequestType = event.getAuthnRequestType();
        ServiceResponse serviceResponse = event.getServiceResponse();

        ResponseType responseType = responseFactory.makeResponse(authnRequestType, SamlResponseStatus.UNKNOWN_PRINCIPAL);

        eventBus.publish(new AuthnResponsePrepared(responseType, serviceResponse));
    }

    @EventListener
    public void onUserInteractionRequired(UserInteractionRequired event) {
        AuthnRequestType authnRequestType = event.getAuthnRequestType();
        ServiceResponse serviceResponse = event.getServiceResponse();

        Session currentSession = sessionRepository.getCurrentSession();
        currentSession.storeAuthnRequest(authnRequestType);

        serviceResponse.sendUserInteractionRequired();
    }

}