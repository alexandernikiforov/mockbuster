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

import ch.alni.mockbuster.saml2.Saml2ProtocolObjects;
import ch.alni.mockbuster.service.ServiceResponse;
import ch.alni.mockbuster.signature.enveloped.EnvelopedSigner;
import org.oasis.saml2.protocol.ObjectFactory;
import org.oasis.saml2.protocol.ResponseType;
import org.slf4j.Logger;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import javax.inject.Inject;
import javax.xml.bind.JAXBException;

import static org.slf4j.LoggerFactory.getLogger;

@Component
public class AuthnResponseSender {
    private static final Logger LOG = getLogger(AuthnResponseSender.class);
    private final ResponseSigner responseSigner;
    private final ObjectFactory objectFactory = new ObjectFactory();

    @Inject
    public AuthnResponseSender(EnvelopedSigner envelopedSigner) {
        responseSigner = new ResponseSigner(envelopedSigner);
    }

    @EventListener
    public void onAuthnResponse(AuthnResponsePrepared event) {
        ResponseType responseType = event.getResponseType();
        ServiceResponse serviceResponse = event.getServiceResponse();

        try {
            Document document = Saml2ProtocolObjects.jaxbElementToDocument(
                    objectFactory.createResponse(responseType)
            );

            responseSigner.signResponse(document);

            String response = Saml2ProtocolObjects.protocolDocumentToString(document, ResponseType.class);

            serviceResponse.sendResponse(responseType.getDestination(), response);
        } catch (JAXBException e) {
            LOG.error("invalid response type");
            throw new IllegalStateException(e);
        }
    }

}
