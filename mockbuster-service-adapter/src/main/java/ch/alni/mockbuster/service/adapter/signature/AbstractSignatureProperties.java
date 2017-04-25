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

package ch.alni.mockbuster.service.adapter.signature;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Optional;

import ch.alni.mockbuster.signature.SignatureProperties;

import static ch.alni.mockbuster.service.adapter.signature.XPathUtils.findNode;
import static ch.alni.mockbuster.service.adapter.signature.XPathUtils.getNode;

public class AbstractSignatureProperties implements SignatureProperties {
    private final String signedElementPath;
    private final String parentNodePath;
    private final String nextSiblingNodePath;

    public AbstractSignatureProperties(String signedElementPath, String parentNodePath, String nextSiblingNodePath) {
        this.signedElementPath = signedElementPath;
        this.parentNodePath = parentNodePath;
        this.nextSiblingNodePath = nextSiblingNodePath;
    }

    @Override
    public final String getReferenceUri(Document document) {
        final Element element = (Element) getNode(document, signedElementPath);
        final String idValue = element.getAttribute("ID");
        if (StringUtils.isEmpty(idValue)) {
            return "";
        } else {
            // otherwise the ID attribute is not of type Id :-(
            element.setIdAttribute("ID", true);
            return "#" + idValue;
        }
    }

    @Override
    public final Node getParentNode(Document document) {
        return getNode(document, parentNodePath);
    }

    @Override
    public final Optional<Node> findNextSiblingNode(Document document) {
        return findNode(document, nextSiblingNodePath);
    }
}
