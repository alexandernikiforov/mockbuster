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

import ch.alni.mockbuster.signature.SignatureConfiguration;
import ch.alni.mockbuster.signature.xpath.NodeFinder;
import ch.alni.mockbuster.signature.xpath.ReferenceUris;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.util.Collections;
import java.util.List;

/**
 * How to make an ENVELOPED signature in the XML dom.
 */
public final class EnvelopedSigner {
    private static XMLSignatureFactory xmlSignatureFactory = XMLSignatureFactory.getInstance("DOM");

    private EnvelopedSigner() {
    }

    /**
     * @param pathToElementToBeSigned xpath to the element in the document that should be signed
     * @param signatureLocation       where the signature should be located in the signed document
     */
    public static void sign(Document document, String pathToElementToBeSigned, SignatureLocation signatureLocation, SignatureConfiguration signatureConfiguration) {
        try {
            DigestMethod digestMethod = xmlSignatureFactory.newDigestMethod(
                    signatureConfiguration.getDigestMethodUri(), null);

            List<Transform> transformList = Collections.singletonList(
                    xmlSignatureFactory.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null)
            );

            Element elementToBeSigned = (Element) NodeFinder.getNode(document, pathToElementToBeSigned);
            String referenceUri = ReferenceUris.getReferenceUri(elementToBeSigned);

            Reference reference = xmlSignatureFactory.newReference(referenceUri,
                    digestMethod, transformList, null, null);

            CanonicalizationMethod canonicalizationMethod = xmlSignatureFactory.newCanonicalizationMethod(
                    CanonicalizationMethod.INCLUSIVE_WITH_COMMENTS,
                    (C14NMethodParameterSpec) null
            );

            SignatureMethod signatureMethod = xmlSignatureFactory.newSignatureMethod(
                    signatureConfiguration.getSignatureMethodUri(),
                    null
            );

            SignedInfo signedInfo = xmlSignatureFactory.newSignedInfo(
                    canonicalizationMethod,
                    signatureMethod,
                    Collections.singletonList(reference)
            );

            KeyInfoFactory keyInfoFactory = xmlSignatureFactory.getKeyInfoFactory();
            X509Data x509Data = keyInfoFactory.newX509Data(signatureConfiguration.getSignatureValidatingCertPath());
            KeyInfo keyInfo = keyInfoFactory.newKeyInfo(Collections.singletonList(x509Data));

            Node parentNode = NodeFinder.getNode(document, signatureLocation.getPathToParentNode());
            PrivateKey signingKey = signatureConfiguration.getSigningKey();

            DOMSignContext signContext = NodeFinder.findNode(document, signatureLocation.getPathToNextSiblingNode())
                    .map(nextSiblingNode -> new DOMSignContext(signingKey, parentNode, nextSiblingNode))
                    .orElseGet(() -> new DOMSignContext(signingKey, parentNode));

            XMLSignature xmlSignature = xmlSignatureFactory.newXMLSignature(signedInfo, keyInfo);
            xmlSignature.sign(signContext);

        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
            throw new IllegalStateException(e);

        } catch (XMLSignatureException | MarshalException e) {
            throw new IllegalStateException("unexpected exception while signing a document", e);
        }

    }
}
