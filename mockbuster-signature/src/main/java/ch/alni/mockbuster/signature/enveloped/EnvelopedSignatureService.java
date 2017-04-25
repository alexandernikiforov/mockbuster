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

import org.apache.commons.lang.UnhandledException;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;

import ch.alni.mockbuster.signature.SignatureProperties;
import ch.alni.mockbuster.signature.SignatureService;

/**
 * How to make an ENVELOPED signature in the XML dom.
 */
@Component
public class EnvelopedSignatureService implements SignatureService {
    private static XMLSignatureFactory xmlSignatureFactory = XMLSignatureFactory.getInstance("DOM");

    private final SignatureConfiguration signatureConfiguration;

    @Inject
    public EnvelopedSignatureService(SignatureConfiguration signatureConfiguration) {
        this.signatureConfiguration = signatureConfiguration;
    }

    @Override
    public void sign(Document document, SignatureProperties signatureProperties) {
        try {
            DigestMethod digestMethod = xmlSignatureFactory.newDigestMethod(
                    signatureConfiguration.getDigestMethodUri(), null);

            List<Transform> transformList = Collections.singletonList(
                    xmlSignatureFactory.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null)
            );

            Reference reference = xmlSignatureFactory.newReference(signatureProperties.getReferenceUri(document),
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

            DOMSignContext signContext = signatureProperties.findNextSiblingNode(document)
                    .map(nextSiblingNode -> new DOMSignContext(
                            signatureConfiguration.getSigningKey(),
                            signatureProperties.getParentNode(document),
                            nextSiblingNode))

                    .orElse(new DOMSignContext(
                            signatureConfiguration.getSigningKey(),
                            signatureProperties.getParentNode(document)));

            XMLSignature xmlSignature = xmlSignatureFactory.newXMLSignature(signedInfo, keyInfo);
            xmlSignature.sign(signContext);

        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
            throw new IllegalStateException(e);

        } catch (XMLSignatureException | MarshalException e) {
            throw new UnhandledException("unexpecting exception while signining a dom", e);
        }

    }
}
