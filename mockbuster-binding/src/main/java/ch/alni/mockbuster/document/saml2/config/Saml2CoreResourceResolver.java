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

package ch.alni.mockbuster.document.saml2.config;

import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

import java.util.HashMap;
import java.util.Map;

public class Saml2CoreResourceResolver implements LSResourceResolver {
    private final static String SAML2_ASSERTION_NAMESPACE_URI = "urn:oasis:names:tc:SAML:2.0:assertion";
    private final static String SAML2_METADATA_NAMESPACE_URI = "urn:oasis:names:tc:SAML:2.0:metadata";
    private final static String SAML2_PROTOCOL_NAMESPACE_URI = "urn:oasis:names:tc:SAML:2.0:protocol";
    private final static String XENC_NAMESPACE_URI = "http://www.w3.org/2001/04/xmlenc#";
    private final static String XMLSIG_CORE_NAMESPACE_URI = "http://www.w3.org/2000/09/xmldsig#";

    private final Map<String, String> schemaTable = new HashMap<>();

    Saml2CoreResourceResolver() {
        schemaTable.put(SAML2_ASSERTION_NAMESPACE_URI, "saml-schema-assertion-2.0.xsd");
        schemaTable.put(SAML2_METADATA_NAMESPACE_URI, "saml-schema-metadata-2.0.xsd");
        schemaTable.put(SAML2_PROTOCOL_NAMESPACE_URI, "saml-schema-protocol-2.0.xsd");
        schemaTable.put(XENC_NAMESPACE_URI, "xenc-schema.xsd");
        schemaTable.put(XMLSIG_CORE_NAMESPACE_URI, "xmldsig-core-schema.xsd");
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
