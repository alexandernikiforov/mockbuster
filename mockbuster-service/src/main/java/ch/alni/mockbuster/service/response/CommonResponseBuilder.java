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

package ch.alni.mockbuster.service.response;

import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;

import org.oasis.saml2.protocol.RequestAbstractType;
import org.oasis.saml2.protocol.ResponseType;

/**
 * Fills out common information in a SAML 2.0 response.
 */
public class CommonResponseBuilder {

    public static final String SAML_VERSION = "2.0";

    public ResponseType build(RequestAbstractType request) {
        String responseId = request.getID();

        final ResponseType response = ResponseType.builder()
                .withIssueInstant(new XMLGregorianCalendarImpl())
                .withInResponseTo(responseId)
                .withVersion(SAML_VERSION)
                .build();

        return response;
    }
}
