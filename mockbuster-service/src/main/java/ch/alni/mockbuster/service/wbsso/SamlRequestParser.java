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

import javax.inject.Inject;
import javax.xml.bind.JAXBException;

import ch.alni.mockbuster.service.events.EventBus;
import ch.alni.mockbuster.service.saml2.Saml2ObjectUnmarshaller;

import static org.slf4j.LoggerFactory.getLogger;

@Component
public class SamlRequestParser {
    private static final Logger LOG = getLogger(SamlRequestParser.class);

    private final Saml2ObjectUnmarshaller unmarshaller;
    private final EventBus eventBus;

    @Inject
    public SamlRequestParser(Saml2ObjectUnmarshaller unmarshaller, EventBus eventBus) {
        this.unmarshaller = unmarshaller;
        this.eventBus = eventBus;
    }

    @EventListener
    public void onAuthRequestReceived(AuthnRequestReceived event) {
        handleRequest(event, AuthnRequestType.class);
    }

    @EventListener
    public void onLogoutRequestReceived(LogoutRequestReceived event) {
        handleRequest(event, LogoutRequestType.class);
    }

    private <T extends RequestAbstractType> void handleRequest(SamlRequestReceived event, Class<T> clazz) {
        String request = event.getServiceRequest().getRequest();

        try {
            T samlRequestType = unmarshaller.unmarshal(request, clazz);
            eventBus.publish(new SamlRequestParsed<>(
                    event.getServiceRequest(),
                    event.getServiceResponse(),
                    samlRequestType
            ));
        } catch (JAXBException e) {
            LOG.info("cannot parse incoming SAML request as of type " + clazz.getName(), e);
            event.getServiceResponse().sendInvalidRequest();
        }
    }
}
