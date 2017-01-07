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

package ch.alni.mockbuster.service.authentication;

import ch.alni.mockbuster.service.process.ServiceRequest;
import ch.alni.mockbuster.service.process.ServiceRequestChain;
import ch.alni.mockbuster.service.process.ServiceRequestProcessor;
import ch.alni.mockbuster.service.process.ServiceResponse;

/**
 * Resolves the principal.
 */
public class PrincipalResolver implements ServiceRequestProcessor {

    @Override
    public void process(ServiceRequestChain serviceRequestChain, ServiceResponse serviceResponse, ServiceRequest serviceRequest) {
        serviceRequestChain.doNext(serviceResponse, serviceRequest);
    }
}
