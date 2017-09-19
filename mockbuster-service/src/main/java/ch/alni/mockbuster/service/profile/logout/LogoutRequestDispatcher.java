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
import ch.alni.mockbuster.core.domain.PrincipalRepository;
import ch.alni.mockbuster.saml2.SamlResponseStatus;
import ch.alni.mockbuster.service.ServiceResponse;
import ch.alni.mockbuster.service.events.EventBus;
import org.oasis.saml2.protocol.LogoutRequestType;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.function.Consumer;

@Component
public class LogoutRequestDispatcher {
    private final PrincipalRepository principalRepository;
    private final EventBus eventBus;

    @Inject
    public LogoutRequestDispatcher(PrincipalRepository principalRepository, EventBus eventBus) {
        this.principalRepository = principalRepository;
        this.eventBus = eventBus;
    }

    @EventListener
    public void onLogoutRequestReceived(LogoutRequestReceived event) {
        LogoutRequestType logoutRequestType = event.getLogoutRequestType();
        ServiceResponse serviceResponse = event.getServiceResponse();

        LogoutRequests.findNameId(logoutRequestType)
                .flatMap(principalRepository::findByNameId)
                .map(principal -> logout(principal, logoutRequestType))
                .orElseGet(() -> logoutWithPrincipalNotFound(logoutRequestType))
                .accept(serviceResponse);
    }

    private Consumer<ServiceResponse> logout(Principal principal, LogoutRequestType logoutRequestType) {
        return serviceResponse ->
                eventBus.publish(new LogoutRequestPrincipalIdentified(logoutRequestType, serviceResponse, principal));
    }

    private Consumer<ServiceResponse> logoutWithPrincipalNotFound(LogoutRequestType logoutRequestType) {
        return serviceResponse -> eventBus.publish(new LogoutRequestDenied(logoutRequestType, serviceResponse,
                SamlResponseStatus.UNKNOWN_PRINCIPAL));
    }

}
