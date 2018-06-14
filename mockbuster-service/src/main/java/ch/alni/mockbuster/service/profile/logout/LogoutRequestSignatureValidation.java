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


import ch.alni.mockbuster.core.domain.ServiceProvider;
import ch.alni.mockbuster.saml2.Saml2NamespaceUri;
import ch.alni.mockbuster.saml2.Saml2ProtocolObjects;
import ch.alni.mockbuster.service.profile.common.SamlRequestSignatureValidator;
import ch.alni.mockbuster.service.profile.validation.SamlRequestValidationResult;
import ch.alni.mockbuster.signature.pkix.X509Certificates;
import ch.alni.mockbuster.signature.xpath.XPaths;
import org.oasis.saml2.protocol.LogoutRequestType;
import org.oasis.saml2.protocol.ObjectFactory;
import org.slf4j.Logger;
import org.w3c.dom.Document;

import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import java.security.cert.X509Certificate;
import java.util.List;

import static ch.alni.mockbuster.saml2.SamlResponseStatus.REQUEST_DENIED;
import static ch.alni.mockbuster.service.profile.validation.SamlRequestValidationResultFactory.makeInvalid;
import static ch.alni.mockbuster.service.profile.validation.SamlRequestValidationResultFactory.makeValid;
import static org.slf4j.LoggerFactory.getLogger;

public class LogoutRequestSignatureValidation {
    private static final Logger LOG = getLogger(LogoutRequestSignatureValidation.class);

    private final static String SIGNATURE_PATH = XPaths.toAbsolutePath(
            new QName(Saml2NamespaceUri.SAML2_PROTOCOL_NAMESPACE_URI, "LogoutRequest"),
            new QName(Saml2NamespaceUri.XMLSIG_CORE_NAMESPACE_URI, "Signature")
    );

    private final static SamlRequestSignatureValidator signatureValidator = new SamlRequestSignatureValidator(SIGNATURE_PATH);

    private final ServiceProvider serviceProvider;

    public LogoutRequestSignatureValidation(ServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

    public SamlRequestValidationResult validateSignature(LogoutRequestType logoutRequestType) {
        final ObjectFactory objectFactory = new ObjectFactory();
        try {
            Document document = Saml2ProtocolObjects.jaxbElementToDocument(
                    objectFactory.createLogoutRequest(logoutRequestType)
            );

            List<X509Certificate> certificateList = X509Certificates.gatherCertificates(serviceProvider.getCertificates());

            if (signatureValidator.validateSignature(document, certificateList, true)) {
                return makeValid();
            } else {
                LOG.info("service provider not found or unknown; LogoutRequest with ID {} will be denied", logoutRequestType.getID());
                return makeInvalid("invalid signature(s) found", REQUEST_DENIED);
            }
        } catch (JAXBException e) {
            // this cannot happen
            throw new IllegalStateException("cannot create DOM from a JAXB object", e);
        }

    }
}
