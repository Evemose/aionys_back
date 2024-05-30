package org.aionys.notes.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class UserController {

    @PostMapping("/login")
    public void login(Principal principal) {

    }
}
