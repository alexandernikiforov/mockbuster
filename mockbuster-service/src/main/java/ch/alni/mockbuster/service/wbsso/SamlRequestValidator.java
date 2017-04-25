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
import org.oasis.saml2.protocol.RequestAbstractType;
import org.slf4j.Logger;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import javax.inject.Inject;
import javax.xml.transform.TransformerException;

import ch.alni.mockbuster.service.dom.Documents;
import ch.alni.mockbuster.service.events.ServiceEventPublisher;
import ch.alni.mockbuster.service.signature.SignatureService;

import static org.slf4j.LoggerFactory.getLogger;

@Component
public class SamlRequestValidator {
    private static final Logger LOG = getLogger(SamlRequestValidator.class);

    private final SignatureService signatureService;

    @Inject
    public SamlRequestValidator(SignatureService signatureService) {
        this.signatureService = signatureService;
    }

    @EventListener
    public void onAuthnRequestParsed(SamlRequestParsed<AuthnRequestType> event) {
        handleRequest(event);
    }

    @EventListener
    public void onLogoutRequestParsed(SamlRequestParsed<LogoutRequestType> event) {
        handleRequest(event);
    }

    private <T extends RequestAbstractType> void handleRequest(SamlRequestParsed<T> event) {
        String request = event.getServiceRequest().getRequest();

        try {
            Document document = Documents.toDocument(request);

            if (signatureService.isRequestSignatureValid(document)) {
                ServiceEventPublisher.getInstance().publish(new SamlRequestValidated<>(
                        event.getServiceRequest(),
                        event.getServiceResponse(),
                        event.getSamlRequestType()
                ));
            } else {
                ServiceEventPublisher.getInstance()
                        .publish(new InvalidSignature<>(
                                event.getServiceRequest(),
                                event.getServiceResponse(),
                                event.getSamlRequestType()
                        ));
            }

        } catch (TransformerException e) {
            LOG.info("cannot parse incoming SAML request", e);
            event.getServiceResponse().sendInvalidRequest();
        }
    }

}
