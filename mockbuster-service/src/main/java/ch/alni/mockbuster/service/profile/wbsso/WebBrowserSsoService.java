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

import ch.alni.mockbuster.core.domain.*;
import ch.alni.mockbuster.saml2.Saml2ProtocolObjects;
import ch.alni.mockbuster.service.MockbusterSsoService;
import ch.alni.mockbuster.service.ServiceRequestTicket;
import ch.alni.mockbuster.service.events.EventBus;
import ch.alni.mockbuster.service.profile.common.SamlRequests;
import ch.alni.mockbuster.service.profile.validation.SamlRequestValidationResult;
import org.oasis.saml2.protocol.AuthnRequestType;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.xml.bind.JAXBException;

import static ch.alni.mockbuster.core.domain.AssertionConsumerServicePredicates.hasSameIndex;
import static ch.alni.mockbuster.core.domain.AssertionConsumerServicePredicates.hasSameUrl;
import static org.slf4j.LoggerFactory.getLogger;

@Service
class WebBrowserSsoService implements MockbusterSsoService {
    private static final Logger LOG = getLogger(WebBrowserSsoService.class);

    private final EventBus eventBus;
    private final ServiceProviderRepository serviceProviderRepository;
    private final IdentityProviderRepository identityProviderRepository;

    @Inject
    WebBrowserSsoService(EventBus eventBus,
                         ServiceProviderRepository serviceProviderRepository,
                         IdentityProviderRepository identityProviderRepository) {
        this.eventBus = eventBus;
        this.serviceProviderRepository = serviceProviderRepository;
        this.identityProviderRepository = identityProviderRepository;
    }

    @Override
    public void authenticate(String serviceRequest, ServiceRequestTicket serviceRequestTicket) {
        LOG.info("a new AuthnRequest received");
        try {
            AuthnRequestType authnRequestType = Saml2ProtocolObjects.unmarshal(serviceRequest, AuthnRequestType.class);

            IdentityProvider identityProvider = identityProviderRepository.getIdentityProvider();

            ServiceProvider serviceProvider = SamlRequests.findIssuerId(authnRequestType)
                    .flatMap(serviceProviderRepository::findByEntityId)
                    .orElse(null);

            if (null == serviceProvider) {
                LOG.info("service provider not found or unknown; AuthnRequest with ID {} will be denied", authnRequestType.getID());
                eventBus.publish(new AuthnRequestNotUnderstood(serviceRequest, serviceRequestTicket));
                return;
            }

            // do we recognize the return destination?
            AssertionConsumerService assertionConsumerService = getAssertionConsumerService(serviceProvider, authnRequestType);

            if (null == assertionConsumerService) {
                LOG.info("assertion consumer service cannot be found; cannot deduce the return destination");
                eventBus.publish(new AuthnRequestNotUnderstood(serviceRequest, serviceRequestTicket));
                return;
            }

            AuthnRequestValidation requestValidation = new AuthnRequestValidation(identityProvider, serviceProvider);
            SamlRequestValidationResult validationResult = requestValidation.validateRequest(authnRequestType);

            if (validationResult.isValid()) {
                eventBus.publish(
                        new AuthnRequestValidated(
                                authnRequestType,
                                identityProvider,
                                serviceProvider,
                                assertionConsumerService,
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
                                assertionConsumerService,
                                serviceRequestTicket,
                                validationResult.getResponseStatus()
                        ));
            }

        } catch (JAXBException e) {
            LOG.info("cannot parse AuthnRequest", e);
            eventBus.publish(new AuthnRequestNotUnderstood(serviceRequest, serviceRequestTicket));
        }
    }

    private AssertionConsumerService getAssertionConsumerService(ServiceProvider serviceProvider, AuthnRequestType authnRequestType) {
        final String assertionConsumerServiceUrl = authnRequestType.getAssertionConsumerServiceURL();
        final Integer assertionConsumerServiceIndex = authnRequestType.getAssertionConsumerServiceIndex();

        return serviceProvider.getAssertionConsumerServices()
                .stream()
                .filter(hasSameIndex(assertionConsumerServiceIndex).or(hasSameUrl(assertionConsumerServiceUrl)))
                .findFirst()
                .orElse(null);
    }

}
