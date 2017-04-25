package ch.alni.mockbuster.web.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.inject.Inject;

import ch.alni.mockbuster.service.MockbusterService;
import ch.alni.mockbuster.service.ServiceRequest;

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

    @RequestMapping(path = "/saml2/sso/post", method = RequestMethod.POST)
    public ResponseEntity<?> authenticate(@RequestBody MultiValueMap<String, String> formParameterMap) {
        final String encodedSamlRequest = formParameterMap.getFirst("SAMLRequest");
        final String relayStateToken = formParameterMap.getFirst("RelayState");

        mockbusterService.authenticate(
                new ServiceRequest(encodedSamlRequest, relayStateToken), null

        );
        return ResponseEntity.ok().build();
    }

    @RequestMapping(path = "/saml2/sso/logout", method = RequestMethod.POST)
    public ResponseEntity<?> logout(@RequestBody MultiValueMap<String, String> formParameterMap) {
        final String encodedSamlRequest = formParameterMap.getFirst("SAMLRequest");
        final String relayStateToken = formParameterMap.getFirst("RelayState");


        return ResponseEntity.ok().build();
    }


}
