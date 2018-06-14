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

import ch.alni.mockbuster.core.domain.*;
import ch.alni.mockbuster.saml2.*;
import ch.alni.mockbuster.service.ServiceRequestTicket;
import ch.alni.mockbuster.service.events.EventBus;
import ch.alni.mockbuster.service.events.ServiceEvent;
import ch.alni.mockbuster.service.session.Session;
import ch.alni.mockbuster.service.session.SessionRepository;
import org.apache.commons.lang.BooleanUtils;
import org.oasis.saml2.assertion.AttributeStatementType;
import org.oasis.saml2.assertion.NameIDType;
import org.oasis.saml2.protocol.AuthnRequestType;
import org.oasis.saml2.protocol.ResponseType;
import org.slf4j.Logger;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

import static org.slf4j.LoggerFactory.getLogger;

@Component
public class AuthnRequestDispatcher {
    private static final Logger LOG = getLogger(AuthnRequestDispatcher.class);

    private final SessionRepository sessionRepository;
    private final EventBus eventBus;

    @Inject
    public AuthnRequestDispatcher(SessionRepository sessionRepository, EventBus eventBus) {
        this.sessionRepository = sessionRepository;
        this.eventBus = eventBus;
    }

    @EventListener
    public void onAuthRequestFailed(AuthnRequestFailed event) {
        final SamlResponseStatus samlResponseStatus = event.getResponseStatus();
        final IdentityProvider identityProvider = event.getIdentityProvider();
        final AuthnRequestType authnRequestType = event.getAuthnRequestType();
        final ServiceProvider serviceProvider = event.getServiceProvider();
        final AssertionConsumerService assertionConsumerService = event.getReturnDestination();
        final ServiceRequestTicket serviceRequestTicket = event.getServiceRequestTicket();

        final ResponseType responseType = ResponseFactory.makeErrorResponse(
                new ResponseTypeParams.Builder()
                        .setIdentityProviderId(identityProvider.getEntityId())
                        .setAuthnRequestType(authnRequestType)
                        .build(),
                samlResponseStatus
        );

        eventBus.publish(new AuthnResponsePrepared(responseType, serviceProvider, assertionConsumerService, serviceRequestTicket));
    }

    @EventListener
    public void onAuthnRequestValidated(AuthnRequestValidated event) {
        final AuthnRequestType authnRequestType = event.getAuthnRequest();

        // find identity
        final ServiceEvent resultingEvent = AuthnRequestTypeFunctions.findSubjectIdentity(authnRequestType)
                .map(nameIDType -> new NameId(nameIDType.getFormat(), nameIDType.getValue()))
                // the SP provided principal identity in the request
                .map(subjectIdentity -> respondIfIdentityIsProvided(event, subjectIdentity))
                // no identity provided, let the user decide who he is
                .orElseGet(() -> requireUserInteraction(event));

        eventBus.publish(resultingEvent);
    }

    private ServiceEvent respondIfIdentityIsProvided(AuthnRequestValidated event, NameId subjectIdentity) {
        Session session = sessionRepository.getCurrentSession();

        // have we already established this identity in the provided session?
        return session.getIdentities()
                .stream()
                .filter(principal -> principal.getNameId().equals(subjectIdentity))
                .findFirst()

                // yes - authenticate
                .map(principal -> authenticate(session, event, principal))

                // no - let the user select the identity
                .orElseGet(() -> requireUserInteraction(event));
    }

    private ServiceEvent authenticate(final Session session, final AuthnRequestValidated event, final Principal principal) {
        final AuthnRequestType authnRequestType = event.getAuthnRequest();

        if (BooleanUtils.isTrue(authnRequestType.isForceAuthn())) {
            // the client is forcing us to re-authenticate
            return new UserAuthenticationRequired(event, principal);
        } else {
            // create response from what we have
            final IdentityProvider identityProvider = event.getIdentityProvider();
            final ServiceProvider serviceProvider = event.getServiceProvider();
            final AssertionConsumerService assertionConsumerService = event.getReturnDestination();

            final AttributeStatementType attributeStatementType =
                    AttributeStatements.toAttributeStatementType(principal.getAttributeStatement());

            final NameId nameId = principal.getNameId();

            final String assertionConsumerServiceUrl = assertionConsumerService.getUrl();

            final ResponseType responseType = ResponseFactory.makeResponse(
                    new ResponseTypeParams.Builder()
                            .setAuthnRequestType(authnRequestType)

                            .setIdentityProviderId(identityProvider.getEntityId())
                            .setDeliveryValidityInSeconds(identityProvider.getDeliveryValidityInSeconds())

                            .setAssertionConsumerServiceUrl(assertionConsumerServiceUrl)

                            .setSessionTimeoutInSeconds(session.getTimeoutInSeconds())
                            .setSessionIndex(session.getIndex())

                            .setAttributeStatementType(attributeStatementType)

                            .setSubjectIdentityId(NameIDType.builder()
                                    .withFormat(nameId.getNameIdFormat())
                                    .withValue(nameId.getNameId())
                                    .build())
                            .build()
            );

            return new AuthnResponsePrepared(responseType, serviceProvider, event.getReturnDestination(), event.getServiceRequestTicket());
        }
    }

    private ServiceEvent requireUserInteraction(AuthnRequestValidated event) {
        return new UserSelectionRequired(event);
    }
}
