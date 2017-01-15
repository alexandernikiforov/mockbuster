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

package ch.alni.mockbuster.service.binding;

import org.w3c.dom.Document;

/**
 * Interface to deflate and encode request and response objects.
 */
public interface MessageTransport {

    /**
     * Returns SAML dom (DOM) from the request.
     *
     * @param request request string containing encoded SAML 2.0 request
     */
    Document processRequest(String request);

    /**
     * Encodes the given SAML response dom as string that will be transferred to the caller.
     *
     * @param response response dom
     */
    String encodeResponse(Document response);
}
