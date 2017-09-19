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

import ch.alni.mockbuster.core.domain.IdentityProvider;
import ch.alni.mockbuster.core.domain.IdentityProviderRepository;
import ch.alni.mockbuster.core.domain.ServiceProvider;
import ch.alni.mockbuster.core.domain.ServiceProviderRepository;
import ch.alni.mockbuster.saml2.Saml2ProtocolObjects;
import ch.alni.mockbuster.saml2.SamlResponseStatus;
import ch.alni.mockbuster.service.MockbusterSsoService;
import ch.alni.mockbuster.service.ServiceResponse;
import ch.alni.mockbuster.service.events.EventBus;
import ch.alni.mockbuster.service.profile.common.SamlRequestSignatureValidator;
import ch.alni.mockbuster.service.profile.common.SamlRequests;
import ch.alni.mockbuster.service.profile.validation.SamlRequestValidationResult;
import ch.alni.mockbuster.signature.pkix.X509Certificates;
import org.oasis.saml2.protocol.AuthnRequestType;
import org.oasis.saml2.protocol.ObjectFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import javax.inject.Inject;
import javax.xml.bind.JAXBException;
import java.security.cert.X509Certificate;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

@Service
class WebBrowserSsoService implements MockbusterSsoService {
    private static final Logger LOG = getLogger(WebBrowserSsoService.class);

    private final ObjectFactory objectFactory = new ObjectFactory();
    private final SamlRequestSignatureValidator signatureValidator = AuthnRequestSignatureValidatorFactory.make();

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
    public void authenticate(String serviceRequest, ServiceResponse serviceResponse) {
        LOG.info("a new AuthnRequest received");
        try {
            AuthnRequestType authnRequestType = Saml2ProtocolObjects.unmarshal(serviceRequest, AuthnRequestType.class);

            ServiceProvider serviceProvider = SamlRequests.findIssuerId(authnRequestType)
                    .flatMap(serviceProviderRepository::findByEntityId)
                    .orElse(null);

            IdentityProvider identityProvider = identityProviderRepository.getIdentityProvider();

            if (null != serviceProvider) {
                Document document = Saml2ProtocolObjects.jaxbElementToDocument(
                        objectFactory.createAuthnRequest(authnRequestType)
                );

                SamlRequestValidationResult validationResult = new AuthnRequestValidation(identityProvider, serviceProvider)
                        .validateRequest(authnRequestType);

                if (validationResult.isValid()) {
                    List<X509Certificate> certificateList = X509Certificates.gatherCertificates(serviceProvider.getCertificates());

                    if (signatureValidator.validateSignature(document, certificateList, identityProvider.isWantAuthnRequestsSigned())) {
                        eventBus.publish(new AuthnRequestReceived(authnRequestType, serviceProvider, serviceResponse));
                    } else {
                        LOG.info("invalid or non existing signature; AuthnRequest with ID {} will be denied", authnRequestType.getID());
                        eventBus.publish(new AuthnRequestFailed(authnRequestType, serviceResponse, SamlResponseStatus.REQUEST_DENIED));
                    }
                } else {
                    LOG.info("cannot validate AuthnRequest with ID {}, error: {}",
                            authnRequestType.getID(),
                            validationResult.getErrorMessages()
                    );

                    eventBus.publish(new AuthnRequestFailed(authnRequestType, serviceResponse, validationResult.getResponseStatus()));
                }

            } else {
                LOG.info("service provider not found or unknown; AuthnRequest with ID {} will be denied", authnRequestType.getID());
                eventBus.publish(new AuthnRequestFailed(authnRequestType, serviceResponse, SamlResponseStatus.REQUEST_DENIED));
            }

        } catch (JAXBException e) {
            LOG.info("cannot parse AuthnRequest", e);
            serviceResponse.sendInvalidRequest();
        }
    }

}
