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

import ch.alni.mockbuster.saml2.Saml2ProtocolObjects;
import ch.alni.mockbuster.service.MockbusterLogoutService;
import ch.alni.mockbuster.service.ServiceResponse;
import ch.alni.mockbuster.service.events.EventBus;
import ch.alni.mockbuster.signature.enveloped.EnvelopedSignatureValidator;
import org.oasis.saml2.protocol.LogoutRequestType;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import javax.inject.Inject;
import javax.xml.bind.JAXBException;

import static org.slf4j.LoggerFactory.getLogger;

@Service
public class LocalLogoutService implements MockbusterLogoutService {
    private static final Logger LOG = getLogger(LocalLogoutService.class);

    private final EventBus eventBus;
    private final LogoutRequestSignatureValidator logoutRequestSignatureValidator;

    @Inject
    public LocalLogoutService(EventBus eventBus, EnvelopedSignatureValidator envelopedSignatureValidator) {
        this.eventBus = eventBus;
        logoutRequestSignatureValidator = new LogoutRequestSignatureValidator(envelopedSignatureValidator);
    }

    @Override
    public void logout(String serviceRequest, ServiceResponse serviceResponse) {
        try {
            LogoutRequestType logoutRequestType = Saml2ProtocolObjects.unmarshal(serviceRequest, LogoutRequestType.class);

            Document document = Saml2ProtocolObjects.jaxbElementToDocument(logoutRequestType);

            if (logoutRequestSignatureValidator.validateSignature(document)) {
                eventBus.publish(new LogoutRequestReceived(logoutRequestType, serviceResponse));
            } else {
                eventBus.publish(new LogoutRequestIncorrectlySigned(logoutRequestType, serviceResponse));
            }

        } catch (JAXBException e) {
            LOG.info("cannot parse the logout request", e);
            serviceResponse.sendInvalidRequest();
        }


    }
}
