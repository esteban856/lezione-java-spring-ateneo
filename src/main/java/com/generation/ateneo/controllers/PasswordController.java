package com.generation.ateneo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.generation.ateneo.DTO.UserAccountCambiaPasswordDTO;
import com.generation.ateneo.services.UserAccountService;

import ch.qos.logback.core.model.Model;
import lombok.extern.slf4j.Slf4j;



@Controller
@Slf4j
public class PasswordController {

    private UserAccountService userAccountService;

    @GetMapping("/cambia-password")
    public String cambiaPassword(Model model){
        log.info("redirect to cambia-password");
        
        return "/cambia-password";
    }

    @PostMapping("/{id}/cambia-password")
    public String cambiaPassword(@PathVariable Long id, @ModelAttribute("userAccountCambiaPassword") UserAccountCambiaPasswordDTO userAccountCambiaPassword) {
        
        
        userAccountService.updatePassword(id, userAccountCambiaPassword.getNuovaPassword(), userAccountCambiaPassword.getConfermaPassword());
        
        return "redirect:/";
    }
    

}
