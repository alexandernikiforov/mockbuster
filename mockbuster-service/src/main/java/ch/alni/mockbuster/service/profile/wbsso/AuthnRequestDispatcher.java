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

import ch.alni.mockbuster.core.domain.NameId;
import ch.alni.mockbuster.core.domain.Principal;
import ch.alni.mockbuster.core.domain.PrincipalRepository;
import ch.alni.mockbuster.core.domain.ServiceProvider;
import ch.alni.mockbuster.saml2.SamlResponseStatus;
import ch.alni.mockbuster.service.ServiceResponse;
import ch.alni.mockbuster.service.events.EventBus;
import org.apache.commons.lang.BooleanUtils;
import org.oasis.saml2.protocol.AuthnRequestType;
import org.slf4j.Logger;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.function.Consumer;

import static org.slf4j.LoggerFactory.getLogger;

@Component
public class AuthnRequestDispatcher {
    private static final Logger LOG = getLogger(AuthnRequestDispatcher.class);

    private final PrincipalRepository principalRepository;
    private final EventBus eventBus;

    @Inject
    public AuthnRequestDispatcher(PrincipalRepository principalRepository, EventBus eventBus) {
        this.principalRepository = principalRepository;
        this.eventBus = eventBus;
    }

    @EventListener
    public void onAuthnRequestReceived(AuthnRequestReceived event) {
        AuthnRequestType authnRequest = event.getAuthnRequest();
        ServiceResponse serviceResponse = event.getServiceResponse();
        ServiceProvider serviceProvider = event.getServiceProvider();

        AuthnRequests.getSubjectIdentity(authnRequest)
                .map(subjectIdentity -> withServiceResponseIfIdentityIsProvided(authnRequest, serviceProvider, subjectIdentity))
                .orElseGet(() -> requireUserInteraction(authnRequest))
                .accept(serviceResponse);
    }

    private Consumer<ServiceResponse> withServiceResponseIfIdentityIsProvided(AuthnRequestType authnRequest,
                                                                              ServiceProvider serviceProvider,
                                                                              NameId subjectIdentity) {
        // have we already mapped this identity to a principal?
        return principalRepository.findByNameId(subjectIdentity)
                // yes - authenticate
                .map(principal -> authenticate(authnRequest, serviceProvider, principal))

                // no - respond with an error
                .orElseGet(() -> sendUnknownPrincipal(authnRequest));
    }

    private Consumer<ServiceResponse> requireUserInteraction(AuthnRequestType authnRequest) {
        return serviceResponse -> eventBus.publish(new UserInteractionRequired(authnRequest, serviceResponse));
    }

    private Consumer<ServiceResponse> authenticate(AuthnRequestType authnRequest, ServiceProvider serviceProvider, Principal principal) {
        if (BooleanUtils.isTrue(authnRequest.isForceAuthn())) {
            return serviceResponse -> eventBus.publish(new PrincipalAuthenticationRequired(authnRequest, principal, serviceResponse));
        } else {
            return serviceResponse ->
                    eventBus.publish(new AuthnRequestAuthenticated(authnRequest, serviceProvider, principal, serviceResponse));
        }
    }

    private Consumer<ServiceResponse> sendUnknownPrincipal(AuthnRequestType authnRequest) {
        return serviceResponse -> eventBus.publish(new AuthnRequestFailed(
                authnRequest,
                serviceResponse,
                SamlResponseStatus.UNKNOWN_PRINCIPAL)
        );
    }


}
