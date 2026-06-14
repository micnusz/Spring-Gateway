package com.micnusz.roleService.Controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RoleController {

    @GetMapping("/message")
    public String message() {
        return "RoleService up";
    }
}
