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

package ch.alni.mockbuster.service.saml2;

import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

import java.util.HashMap;
import java.util.Map;

public class Saml2CoreResourceResolver implements LSResourceResolver {

    private final Map<String, String> schemaTable = new HashMap<>();

    public Saml2CoreResourceResolver() {
        schemaTable.put(Saml2NamespaceUri.SAML2_ASSERTION_NAMESPACE_URI, "saml-schema-assertion-2.0.xsd");
        schemaTable.put(Saml2NamespaceUri.SAML2_METADATA_NAMESPACE_URI, "saml-schema-metadata-2.0.xsd");
        schemaTable.put(Saml2NamespaceUri.SAML2_PROTOCOL_NAMESPACE_URI, "saml-schema-protocol-2.0.xsd");
        schemaTable.put(Saml2NamespaceUri.XENC_NAMESPACE_URI, "xenc-schema.xsd");
        schemaTable.put(Saml2NamespaceUri.XMLSIG_CORE_NAMESPACE_URI, "xmldsig-core-schema.xsd");
    }

    @Override
    public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {
        final String resourceName = schemaTable.get(namespaceURI);

        if (null != resourceName) {
            return new Saml2CoreLsInput(resourceName, systemId, baseURI, publicId);
        } else {
            return null;
        }
    }
}
