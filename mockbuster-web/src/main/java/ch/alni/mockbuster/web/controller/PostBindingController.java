package ch.alni.mockbuster.web.controller;

import ch.alni.mockbuster.service.MockbusterLogoutService;
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
public class PostBindingController {

    private final MockbusterSsoService mockbusterSsoService;
    private final MockbusterLogoutService localLogoutService;

    @Inject
    public PostBindingController(MockbusterSsoService mockbusterSsoService, MockbusterLogoutService localLogoutService) {
        this.mockbusterSsoService = mockbusterSsoService;
        this.localLogoutService = localLogoutService;
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
