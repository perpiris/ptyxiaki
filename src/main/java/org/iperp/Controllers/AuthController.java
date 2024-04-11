package org.iperp.Controllers;

import jakarta.validation.Valid;
import org.iperp.Dtos.AuthenticationRequest;
import org.iperp.Dtos.RegistrationRequest;
import org.iperp.Services.IRegistrationService;
import org.iperp.Utilities.WebUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final IRegistrationService registrationService;

    public AuthController(final IRegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @GetMapping("/login")
    public String login(
            @RequestParam(name = "loginRequired", required = false) final Boolean loginRequired,
            @RequestParam(name = "loginError", required = false) final Boolean loginError,
            @RequestParam(name = "logoutSuccess", required = false) final Boolean logoutSuccess,
            final Model model) {

        model.addAttribute("authentication", new AuthenticationRequest());
        if (loginRequired == Boolean.TRUE) {
            model.addAttribute(WebUtils.MSG_INFO, "Please login to view this page.");
        }
        if (loginError == Boolean.TRUE) {
            model.addAttribute(WebUtils.MSG_ERROR, "An error has occurred.Please try again.");
        }
        if (logoutSuccess == Boolean.TRUE) {
            model.addAttribute(WebUtils.MSG_INFO, "Your logout was successful.");
        }
        return "auth/login";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute @Valid final RegistrationRequest registrationRequest,
                           final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "auth/register";
        }

        registrationService.register(registrationRequest);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, "You have registered successfully.Please login.");
        return "redirect:/login";
    }

    @GetMapping("/register")
    public String register(@ModelAttribute final RegistrationRequest registrationRequest) {
        return "auth/register";
    }

}
