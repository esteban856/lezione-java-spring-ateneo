package com.generation.ateneo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import ch.qos.logback.core.model.Model;
import lombok.extern.slf4j.Slf4j;


@Controller
@Slf4j
public class PasswordController {

    @GetMapping("/cambia-password")
    public String cambiaPassword(Model model){
        log.info("redirect to cambia-password");
        // model.addAttribute("passwordForm", new PasswordForm());
        return "cambia-password";
    }

}
