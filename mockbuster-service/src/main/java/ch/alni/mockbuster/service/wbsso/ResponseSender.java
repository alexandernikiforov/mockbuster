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

import org.oasis.saml2.protocol.ResponseType;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import javax.inject.Inject;
import javax.xml.transform.TransformerException;

import ch.alni.mockbuster.service.dom.Documents;
import ch.alni.mockbuster.service.saml2.ResponseMarshaller;
import ch.alni.mockbuster.service.signature.SignatureService;

@Component
public class ResponseSender {
    private final SignatureService signatureService;
    private final ResponseMarshaller responseMarshaller;

    @Inject
    public ResponseSender(SignatureService signatureService, ResponseMarshaller responseMarshaller) {
        this.signatureService = signatureService;
        this.responseMarshaller = responseMarshaller;
    }

    @EventListener
    public void onLogoutResponse(LogoutResponsePrepared event) {
        String response = prepareResponse(event.getResponseType());

        event.getServiceResponse().sendAuthenticated(response);
    }

    @EventListener
    public void onInvalidSignatureResponse(InvalidSignatureResponsePrepared event) {
        String response = prepareResponse(event.getResponseType());

        event.getServiceResponse().sendRequestDenied(response);
    }

    private String prepareResponse(ResponseType responseType) {
        // to DOM
        Document document = responseMarshaller.objectToDocument(responseType);

        // signature
        signatureService.signResponseDocument(document);

        try {
            return Documents.toString(document);
        } catch (TransformerException e) {
            throw new IllegalStateException("cannot write prepared SAML response", e);
        }
    }
}
