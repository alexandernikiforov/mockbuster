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

package ch.alni.mockbuster.saml2;

import org.oasis.saml2.assertion.AttributeStatementType;
import org.oasis.saml2.assertion.NameIDType;
import org.oasis.saml2.protocol.AuthnRequestType;

public class ResponseTypeParams {

    private final String identityProviderId;
    private final long deliveryValidityInSeconds;
    private final Long sessionTimeoutInSeconds;
    private final String sessionIndex;
    private final String assertionConsumerServiceUrl;
    private final AttributeStatementType attributeStatementType;
    private final NameIDType subjectIdentityId;
    private final AuthnRequestType authnRequestType;

    private ResponseTypeParams(Builder builder) {
        this.identityProviderId = builder.identityProviderId;
        this.deliveryValidityInSeconds = builder.deliveryValidityInSeconds;
        this.sessionTimeoutInSeconds = builder.sessionTimeoutInSeconds;
        this.assertionConsumerServiceUrl = builder.assertionConsumerServiceUrl;
        this.attributeStatementType = builder.attributeStatementType;
        this.subjectIdentityId = builder.subjectIdentityId;
        this.authnRequestType = builder.authnRequestType;
        sessionIndex = builder.sessionIndex;
    }

    public String getIdentityProviderId() {
        return identityProviderId;
    }

    public long getDeliveryValidityInSeconds() {
        return deliveryValidityInSeconds;
    }

    public Long getSessionTimeoutInSeconds() {
        return sessionTimeoutInSeconds;
    }

    public String getAssertionConsumerServiceUrl() {
        return assertionConsumerServiceUrl;
    }

    public AttributeStatementType getAttributeStatementType() {
        return attributeStatementType;
    }

    public NameIDType getSubjectIdentityId() {
        return subjectIdentityId;
    }

    public AuthnRequestType getAuthnRequestType() {
        return authnRequestType;
    }

    public String getSessionIndex() {
        return sessionIndex;
    }

    public static class Builder {
        private String identityProviderId;
        private long deliveryValidityInSeconds;
        private Long sessionTimeoutInSeconds;
        private String sessionIndex;
        private String assertionConsumerServiceUrl;
        private AttributeStatementType attributeStatementType;
        private NameIDType subjectIdentityId;
        private AuthnRequestType authnRequestType;

        public Builder setIdentityProviderId(String identityProviderId) {
            this.identityProviderId = identityProviderId;
            return this;
        }

        public Builder setDeliveryValidityInSeconds(long deliveryValidityInSeconds) {
            this.deliveryValidityInSeconds = deliveryValidityInSeconds;
            return this;
        }

        public Builder setSessionTimeoutInSeconds(Long sessionTimeoutInSeconds) {
            this.sessionTimeoutInSeconds = sessionTimeoutInSeconds;
            return this;
        }

        public Builder setAssertionConsumerServiceUrl(String assertionConsumerServiceUrl) {
            this.assertionConsumerServiceUrl = assertionConsumerServiceUrl;
            return this;
        }

        public Builder setAttributeStatementType(AttributeStatementType attributeStatementType) {
            this.attributeStatementType = attributeStatementType;
            return this;
        }

        public Builder setSubjectIdentityId(NameIDType subjectIdentityId) {
            this.subjectIdentityId = subjectIdentityId;
            return this;
        }

        public Builder setAuthnRequestType(AuthnRequestType authnRequestType) {
            this.authnRequestType = authnRequestType;
            return this;
        }

        public Builder setSessionIndex(String sessionIndex) {
            this.sessionIndex = sessionIndex;
            return this;
        }

        public ResponseTypeParams build() {
            return new ResponseTypeParams(this);
        }
    }
}
