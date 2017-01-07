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

package ch.alni.mockbuster.service.process;

import java.util.Iterator;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * Implements a process of responsibility for the service requests.
 */
public class ServiceRequestChain {

    private final Iterator<ServiceRequestProcessor> serviceRequestProcessorListIterator;

    public ServiceRequestChain(List<ServiceRequestProcessor> serviceRequestProcessorList) {
        serviceRequestProcessorListIterator = serviceRequestProcessorList.iterator();
    }

    public ServiceRequestChain(ServiceRequestProcessor... serviceRequestProcessorList) {
        serviceRequestProcessorListIterator = asList(serviceRequestProcessorList).iterator();
    }

    public void doNext(ServiceResponse response, ServiceRequest request) {
        if (serviceRequestProcessorListIterator.hasNext()) {
            serviceRequestProcessorListIterator.next().process(this, response, request);
        }
    }
}
