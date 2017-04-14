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

package ch.alni.mockbuster.service.wbsso;

import org.oasis.saml2.protocol.RequestAbstractType;
import org.oasis.saml2.protocol.StatusResponseType;

import ch.alni.mockbuster.service.ServiceResponse;
import ch.alni.mockbuster.service.events.ServiceEvent;

public class SamlResponsePrepared<T extends RequestAbstractType, R extends StatusResponseType> implements ServiceEvent {

    private final ServiceResponse serviceResponse;
    private final T requestType;
    private final R responseType;

    public SamlResponsePrepared(ServiceResponse serviceResponse, T requestType, R responseType) {
        this.serviceResponse = serviceResponse;
        this.requestType = requestType;
        this.responseType = responseType;
    }

    public ServiceResponse getServiceResponse() {
        return serviceResponse;
    }

    public R getResponseType() {
        return responseType;
    }

    public T getRequestType() {
        return requestType;
    }
}
