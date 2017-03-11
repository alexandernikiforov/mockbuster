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

package ch.alni.mockbuster.service.wbsso;

import org.oasis.saml2.protocol.LogoutRequestType;
import org.oasis.saml2.protocol.ResponseType;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import ch.alni.mockbuster.service.events.ServiceEventPublisher;
import ch.alni.mockbuster.service.response.CommonResponseBuilder;

@Component
public class LogoutRequestProcessor {

    @EventListener
    public void onLogoutRequestValidated(SamlRequestValidated<LogoutRequestType> event) {
        LogoutRequestType logoutRequestType = event.getSamlRequestType();

        // do logout

        // build response
        ResponseType responseType = new CommonResponseBuilder().build(logoutRequestType);

        ServiceEventPublisher.getInstance().publish(
                new LogoutResponsePrepared(
                        event.getServiceResponse(),
                        responseType
                ));
    }
}
