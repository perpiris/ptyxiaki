package org.iperp.Controllers;

import org.iperp.Dtos.ApplicationDto;
import org.iperp.Services.IApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/applications")
@PreAuthorize("hasAuthority('USER')")
public class ApplicationController {

    @Autowired
    private IApplicationService applicationService;

    @GetMapping("/manage")
    public String manage(@RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "10") int size,
                         @RequestParam(defaultValue = "id") String sortBy,
                         final Model model) {

        Page<ApplicationDto> postPage = applicationService.findAllForApplicant(page, size, sortBy);
        model.addAttribute("applications", postPage.getContent());
        model.addAttribute("currentPage", postPage.getNumber());
        model.addAttribute("totalPages", postPage.getTotalPages());

        return "application/manage";
    }

    @PostMapping("/apply")
    public String applyToPost(@RequestParam Long postId, RedirectAttributes redirectAttributes) {
        try {
            applicationService.applyToPost(postId);
            redirectAttributes.addFlashAttribute("MSG_SUCCESS", "You have applied successfully.");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("MSG_INFO", e.getMessage());
        }

        return "redirect:/applications/manage";
    }
}
