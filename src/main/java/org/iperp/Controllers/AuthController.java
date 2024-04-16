package org.iperp.Controllers;

import jakarta.validation.Valid;
import org.iperp.Dtos.LoginDto;
import org.iperp.Dtos.RegisterDto;
import org.iperp.Enums.UserRole;
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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class AuthController {

    private final IRegistrationService registrationService;

    public AuthController(final IRegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {

        UserRole[] allRoles = UserRole.values();
        List<UserRole> filteredRoles = Arrays.stream(allRoles)
                .filter(role -> role != UserRole.USER && role != UserRole.ADMIN)
                .collect(Collectors.toList());

        model.addAttribute("roleValues", filteredRoles);
    }

    @GetMapping("/login")
    public String login(
            @RequestParam(name = "loginRequired", required = false) final Boolean loginRequired,
            @RequestParam(name = "loginError", required = false) final Boolean loginError,
            @RequestParam(name = "logoutSuccess", required = false) final Boolean logoutSuccess,
            final Model model) {

        model.addAttribute("authentication", new LoginDto());
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

    @GetMapping("/register")
    public String register(@ModelAttribute final RegisterDto registerDto) {
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute @Valid final RegisterDto registerDto,
                           final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "auth/register";
        }

        registrationService.register(registerDto);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, "You have registered successfully.Please login.");
        return "redirect:/login";
    }

}
