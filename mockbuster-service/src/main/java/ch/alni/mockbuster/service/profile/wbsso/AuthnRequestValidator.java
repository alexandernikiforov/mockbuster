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

import ch.alni.mockbuster.core.domain.AssertionConsumerService;
import ch.alni.mockbuster.core.domain.IdentityProvider;
import ch.alni.mockbuster.core.domain.ServiceProvider;
import ch.alni.mockbuster.service.ServiceRequestTicket;
import ch.alni.mockbuster.service.events.EventBus;
import ch.alni.mockbuster.service.profile.validation.SamlRequestValidationResult;
import org.oasis.saml2.protocol.AuthnRequestType;
import org.slf4j.Logger;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

import static org.slf4j.LoggerFactory.getLogger;

@Component
public class AuthnRequestValidator {
    private static final Logger LOG = getLogger(AuthnRequestValidator.class);

    private final EventBus eventBus;

    @Inject
    public AuthnRequestValidator(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @EventListener
    public void onAuthnRequestReceived(AuthnRequestReceived event) {
        final IdentityProvider identityProvider = event.getIdentityProvider();
        final ServiceProvider serviceProvider = event.getServiceProvider();
        final AuthnRequestType authnRequestType = event.getAuthnRequest();
        final AssertionConsumerService returnDestination = event.getReturnDestination();
        final ServiceRequestTicket serviceRequestTicket = event.getServiceRequestTicket();

        AuthnRequestValidation requestValidation = new AuthnRequestValidation(identityProvider, serviceProvider);
        SamlRequestValidationResult validationResult = requestValidation.validateRequest(authnRequestType);

        if (validationResult.isValid()) {
            eventBus.publish(
                    new AuthnRequestValidated(
                            authnRequestType,
                            identityProvider,
                            serviceProvider,
                            returnDestination,
                            serviceRequestTicket
                    ));
        } else {
            LOG.info("cannot validate AuthnRequest with ID {}, error: {}",
                    authnRequestType.getID(),
                    validationResult.getErrorMessages()
            );

            eventBus.publish(
                    new AuthnRequestFailed(
                            authnRequestType,
                            identityProvider,
                            serviceProvider,
                            returnDestination,
                            serviceRequestTicket,
                            validationResult.getResponseStatus()
                    ));
        }
    }
}