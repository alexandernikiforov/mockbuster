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

package ch.alni.mockbuster.web.controller;

import ch.alni.mockbuster.service.MockbusterSsoService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.inject.Inject;

/**
 * Accepts requests from service providers. Implements SAML POST Bindng.
 */
@Controller
public class RedirectBindingController {

    private final MockbusterSsoService mockbusterSsoService;

    @Inject
    public RedirectBindingController(MockbusterSsoService mockbusterSsoService) {
        this.mockbusterSsoService = mockbusterSsoService;
    }

    @RequestMapping(path = "/saml2/sso/post", method = RequestMethod.POST)
    public ResponseEntity<?> authenticate(@RequestBody MultiValueMap<String, String> formParameterMap) {
        final String encodedSamlRequest = formParameterMap.getFirst("SAMLRequest");
        final String relayState = formParameterMap.getFirst("RelayState");

        mockbusterSsoService.authenticate(encodedSamlRequest, null);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(path = "/saml2/sso/logout", method = RequestMethod.POST)
    public ResponseEntity<?> logout(@RequestBody MultiValueMap<String, String> formParameterMap) {
        final String encodedSamlRequest = formParameterMap.getFirst("SAMLRequest");
        final String relayState = formParameterMap.getFirst("RelayState");


        return ResponseEntity.ok().build();
    }


}
