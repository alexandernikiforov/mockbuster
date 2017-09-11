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


import ch.alni.mockbuster.saml2.Saml2NamespaceUri;
import ch.alni.mockbuster.saml2.Saml2ProtocolObjects;
import ch.alni.mockbuster.signature.SignatureValidationResult;
import ch.alni.mockbuster.signature.enveloped.EnvelopedSignatureValidator;
import ch.alni.mockbuster.signature.xpath.XPaths;
import org.oasis.saml2.protocol.AuthnRequestType;
import org.oasis.saml2.protocol.ObjectFactory;
import org.w3c.dom.Document;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;

public class AuthnRequestSignatureValidator {
    private final static String SIGNATURE_PATH = XPaths.toAbsolutePath(
            new QName(Saml2NamespaceUri.SAML2_PROTOCOL_NAMESPACE_URI, "AuthnRequest"),
            new QName(Saml2NamespaceUri.XMLSIG_CORE_NAMESPACE_URI, "Signature")
    );
    private final ObjectFactory objectFactory = new ObjectFactory();
    private final EnvelopedSignatureValidator validator;

    public AuthnRequestSignatureValidator(EnvelopedSignatureValidator validator) {
        this.validator = validator;
    }

    public boolean validateSignature(AuthnRequestType authnRequest) throws JAXBException {
        JAXBElement<AuthnRequestType> authnRequestJaxbElement = objectFactory.createAuthnRequest(authnRequest);

        Document document = Saml2ProtocolObjects.jaxbElementToDocument(authnRequestJaxbElement);
        SignatureValidationResult result = validator.validateXmlSignature(document, SIGNATURE_PATH);

        return result.hasPassedCoreValidation();
    }
}
