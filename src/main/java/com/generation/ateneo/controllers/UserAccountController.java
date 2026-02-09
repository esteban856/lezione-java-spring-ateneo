package com.generation.ateneo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.Authentication;

import com.generation.ateneo.entities.Persona;
import com.generation.ateneo.entities.UserAccount;
import com.generation.ateneo.services.UserAccountService;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;



@Controller
@RequestMapping("/user")
public class UserAccountController {

    @Autowired
    UserAccountService accountService;

    
    @GetMapping("/change-psw")
    public String getMethodName(Model model, Authentication authentication) {
        if (authentication != null) {
            UserAccount user = (UserAccount)authentication.getPrincipal();
            Persona p = user.getPersona();
            model.addAttribute("idPersona", p.getId());
            model.addAttribute("persona", p);
            return "user/user-details";
            
        }
        return "redirect:/login";
    }
    
    
    @PostMapping("/{id}/change-psw")
    public String userChangePassword(
        @PathVariable Long id,
        String username,
        String oldPsw,
        String newPsw,
        Model model,
        Authentication authentication,
        RedirectAttributes ra
    ) {

        if(authentication != null) {
            UserAccount user = accountService.changePasswordUser(username, oldPsw, newPsw);
    
            if(user == null) {
                ra.addFlashAttribute("error", "Credenziali errate");
                return "redirect:/user/change-psw";
            }

            ra.addFlashAttribute("success", "Password cambiata, riesegui il login");

            return "redirect:/logout";
        }

        return "redirect:/login";

    }
    
    
}