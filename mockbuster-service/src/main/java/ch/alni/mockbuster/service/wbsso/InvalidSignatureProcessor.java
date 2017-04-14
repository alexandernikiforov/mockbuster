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

import org.oasis.saml2.protocol.AuthnRequestType;
import org.oasis.saml2.protocol.LogoutRequestType;
import org.oasis.saml2.protocol.ResponseType;
import org.oasis.saml2.protocol.StatusResponseType;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

import ch.alni.mockbuster.service.events.ServiceEventPublisher;

@Component
public class InvalidSignatureProcessor {
    private final ResponseAssembler responseAssembler;

    @Inject
    public InvalidSignatureProcessor(ResponseAssembler responseAssembler) {
        this.responseAssembler = responseAssembler;
    }

    @EventListener
    public void onAuthnRequestInvalidSignature(InvalidSignature<AuthnRequestType> event) {
        AuthnRequestType samlRequestType = event.getSamlRequestType();
        ResponseType responseType = responseAssembler.toStatusResponse(samlRequestType, SamlResponseStatus.REQUEST_DENIED);

        ServiceEventPublisher.getInstance().publish(new SamlResponsePrepared<>(
                event.getServiceResponse(),
                samlRequestType,
                responseType));
    }

    @EventListener
    public void onLogoutRequestInvalidSignature(InvalidSignature<LogoutRequestType> event) {
        LogoutRequestType samlRequestType = event.getSamlRequestType();
        StatusResponseType responseType = responseAssembler.toStatusResponse(samlRequestType, SamlResponseStatus.REQUEST_DENIED);

        ServiceEventPublisher.getInstance().publish(new SamlResponsePrepared<>(
                event.getServiceResponse(),
                samlRequestType,
                responseType));
    }


}
