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

package ch.alni.mockbuster.signature.enveloped;

import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.Optional;

import javax.inject.Inject;
import javax.xml.crypto.KeySelector;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMValidateContext;

import ch.alni.mockbuster.signature.SignatureLocation;
import ch.alni.mockbuster.signature.SignatureValidationService;

import static org.slf4j.LoggerFactory.getLogger;

@Component
public class EnvelopedSignatureValidationService implements SignatureValidationService {
    private static final Logger LOG = getLogger(EnvelopedSignatureValidationService.class);

    private final KeySelector keySelector;

    @Inject
    public EnvelopedSignatureValidationService(KeySelector keySelector) {
        this.keySelector = keySelector;
    }

    @Override
    public boolean containsValidSignature(Document document, SignatureLocation signatureLocation) {

        Optional<Node> signatureNodeOptional = signatureLocation.findSignatureNode();
        if (!signatureNodeOptional.isPresent()) {
            LOG.info("cannot find signature in the dom");
            return false;
        }

        Node signatureNode = signatureNodeOptional.get();

        DOMValidateContext validateContext = new DOMValidateContext(keySelector, signatureNode);

        XMLSignatureFactory factory = XMLSignatureFactory.getInstance("DOM");

        try {
            XMLSignature signature = factory.unmarshalXMLSignature(validateContext);
            return signature.validate(validateContext);
        } catch (MarshalException e) {
            LOG.info("cannot unmarshall signature", e);
            return false;

        } catch (XMLSignatureException e) {
            LOG.info("invalid signature", e);
            return false;
        }
    }

}
