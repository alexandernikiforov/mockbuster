package ch.alni.mockbuster.web.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.inject.Inject;

import ch.alni.mockbuster.service.MockbusterService;

/**
 * Accepts requests from service providers. Implements SAML POST Bindng.
 */
@Controller
public class PostBindingController {

    private final MockbusterService mockbusterService;

    @Inject
    public PostBindingController(MockbusterService mockbusterService) {
        this.mockbusterService = mockbusterService;
    }

    @RequestMapping(path = "/SAML2/SSO/POST", method = RequestMethod.POST)
    public ResponseEntity<?> authenticate(@RequestBody MultiValueMap<String, String> formParameterMap) {
        final String encodedSamlRequest = formParameterMap.getFirst("SAMLRequest");
        final String relayStateToken = formParameterMap.getFirst("RelayState");


        return ResponseEntity.ok().build();
    }

}
