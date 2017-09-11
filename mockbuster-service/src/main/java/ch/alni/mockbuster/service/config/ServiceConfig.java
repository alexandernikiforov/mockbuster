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

package ch.alni.mockbuster.service.config;

import ch.alni.mockbuster.service.ServiceConfiguration;
import ch.alni.mockbuster.service.events.EventBus;
import ch.alni.mockbuster.service.events.ServiceEventPublisher;
import ch.alni.mockbuster.service.events.SpringBasedEventBus;
import ch.alni.mockbuster.signature.SignatureConfiguration;
import ch.alni.mockbuster.signature.enveloped.EnvelopedSignatureValidator;
import ch.alni.mockbuster.signature.enveloped.EnvelopedSigner;
import ch.alni.mockbuster.signature.pkix.X509CertListBasedKeyFinder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.xml.crypto.dsig.DigestMethod;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.List;

/**
 * Configuration for the Spring container.
 */
@Configuration
@ComponentScan("ch.alni.mockbuster.service")
public class ServiceConfig {

    @Value("${mockbuster.config.service_id}")
    private String serviceId;

    @Value("${mockbuster.config.delivery_validity_in_seconds:60}")
    private long deliveryValidityInSeconds;

    @Value("${mockbuster.config.session_not_on_or_after_in_seconds:-1}")
    private long sessionNotOnOrAfterInSeconds;

    @Bean
    public EventBus eventBus() {
        SpringBasedEventBus springBasedEventBus = new SpringBasedEventBus();
        ServiceEventPublisher.connect(springBasedEventBus);

        return springBasedEventBus;
    }

    @Bean
    public ServiceConfiguration serviceConfiguration() {
        return new ServiceConfiguration(serviceId, deliveryValidityInSeconds, sessionNotOnOrAfterInSeconds);
    }

    @Bean
    public EnvelopedSigner envelopedSigner() {
        return new EnvelopedSigner(new SignatureConfiguration() {

            @Override
            public List<X509Certificate> getSignatureValidatingCertPath() {
                return null;
            }

            @Override
            public PrivateKey getSigningKey() {
                return null;
            }

            @Override
            public String getSignatureMethodUri() {
                return "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256";
            }

            @Override
            public String getDigestMethodUri() {
                return DigestMethod.SHA256;
            }
        });
    }

    @Bean
    public EnvelopedSignatureValidator envelopedSignatureValidator() {
        return new EnvelopedSignatureValidator(new X509CertListBasedKeyFinder(
                Collections::emptyList
        ));
    }
}
