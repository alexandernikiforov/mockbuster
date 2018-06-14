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

import ch.alni.mockbuster.signature.SignatureValidationResult;
import ch.alni.mockbuster.signature.SignatureValidationResultFactory;
import ch.alni.mockbuster.signature.xpath.NodeFinder;
import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.crypto.*;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import java.security.Key;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Validates enveloped XML signatures.
 */
public final class EnvelopedSignatureValidator {
    private static final Logger LOG = getLogger(EnvelopedSignatureValidator.class);

    /**
     *
     */
    private EnvelopedSignatureValidator() {
    }

    /**
     * Validates signature in the given element.
     *
     * @param document               document to validate the signature in
     * @param pathToSignatureElement absolute XPath to the signature element
     * @param keyFinder              how to map keyInfo to the validating key
     * @return true if the signature is valid or false otherwise
     */
    public static SignatureValidationResult validateXmlSignature(Document document,
                                                                 String pathToSignatureElement,
                                                                 Function<KeyInfo, Optional<Key>> keyFinder) {
        return NodeFinder.findNode(document, pathToSignatureElement)
                .map(signatureNode -> validateSignatureNode(signatureNode, keyFinder))
                .orElseGet(SignatureValidationResultFactory::makeNotFound);
    }

    private static SignatureValidationResult validateSignatureNode(Node signatureNode, Function<KeyInfo, Optional<Key>> keyFinder) {
        KeySelector keySelector = getKeySelector(keyFinder);

        DOMValidateContext validateContext = new DOMValidateContext(keySelector, signatureNode);

        XMLSignatureFactory factory = XMLSignatureFactory.getInstance("DOM");

        try {
            XMLSignature signature = factory.unmarshalXMLSignature(validateContext);
            boolean coreValidity = signature.validate(validateContext);
            if (!coreValidity) {
                LOG.debug("signature did not pass core validation");
                boolean signatureValueValid = validateSignatureValue(validateContext, signature);
                List<String> references = validateReferences(validateContext, signature);

                return SignatureValidationResultFactory.makeInvalid(signatureValueValid, references);
            } else {
                LOG.debug("signature passed core validation");
                return SignatureValidationResultFactory.makeValid();
            }
        } catch (MarshalException e) {
            LOG.info("cannot unmarshal signature", e);
            return SignatureValidationResultFactory.makeNotFound();

        } catch (XMLSignatureException e) {
            LOG.info("unexpected error while getting the signature", e);
            return SignatureValidationResultFactory.makeNotFound();
        }
    }

    private static boolean validateSignatureValue(DOMValidateContext validateContext, XMLSignature signature) throws XMLSignatureException {
        boolean signatureValueValid = signature.getSignatureValue().validate(validateContext);
        LOG.debug("signature value cryptographically valid: {}", signatureValueValid);
        return signatureValueValid;
    }

    private static List<String> validateReferences(DOMValidateContext validateContext, XMLSignature signature) throws XMLSignatureException {
        List<String> result = new ArrayList<>();
        for (Object reference : signature.getSignedInfo().getReferences()) {
            Reference ref = (Reference) reference;
            boolean isValid = ref.validate(validateContext);
            LOG.debug("reference {} is valid: {}", ref.getURI(), isValid);

            if (!isValid) {
                result.add(ref.getURI());
            }
        }
        return result;
    }

    private static KeySelector getKeySelector(Function<KeyInfo, Optional<Key>> keyFinder) {
        return new KeySelector() {
            @Override
            public KeySelectorResult select(KeyInfo keyInfo, Purpose purpose, AlgorithmMethod method, XMLCryptoContext context) throws KeySelectorException {
                final Key key = keyFinder.apply(keyInfo)
                        .orElseThrow(() -> new KeySelectorException("cannot find validating key"));

                return () -> key;
            }
        };
    }

}
