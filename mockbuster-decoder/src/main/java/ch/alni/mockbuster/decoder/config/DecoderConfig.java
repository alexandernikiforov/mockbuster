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

package ch.alni.mockbuster.decoder.config;

import org.oasis.saml2.protocol.AuthnRequestType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import ch.alni.mockbuster.decoder.Saml2Serializer;
import ch.alni.mockbuster.decoder.SamlRequestDecoder;
import ch.alni.mockbuster.decoder.SamlResponseEncoder;

@Configuration
@ComponentScan(basePackageClasses = {SamlRequestDecoder.class, SamlResponseEncoder.class})
public class DecoderConfig {

    public JAXBContext jaxbContext() {
        try {
            return JAXBContext.newInstance(
                    AuthnRequestType.class
            );
        } catch (JAXBException e) {
            throw new IllegalStateException("cannot create JAXB context", e);
        }
    }

    public Schema schema() {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        schemaFactory.setResourceResolver(new Saml2CoreResourceResolver());

        try {
            return schemaFactory.newSchema(new Source[]{
                            new StreamSource(getClass().getResourceAsStream("/saml2/saml-schema-protocol-2.0.xsd"))
                    }
            );
        } catch (SAXException e) {
            throw new IllegalStateException("cannot create schema for SAML2 validation", e);
        }
    }

    @Bean
    public Saml2Serializer saml2Serializer() {
        return new Saml2Serializer(jaxbContext(), schema());
    }
}
