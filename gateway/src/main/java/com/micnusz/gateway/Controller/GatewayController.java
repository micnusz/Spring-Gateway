package com.micnusz.gateway.Controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GatewayController {

    @GetMapping("/message")
    public String message() {
        return "GatewayController message";
    }
}
