package org.iperp.Controllers;

import org.iperp.Dtos.ApplicationDto;
import org.iperp.Services.IApplicationService;
import org.iperp.Utilities.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
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

        Page<ApplicationDto> applicationPage = applicationService.findAllForApplicant(page, size, sortBy);
        model.addAttribute("applications", applicationPage.getContent());
        model.addAttribute("currentPage", applicationPage.getNumber());
        model.addAttribute("totalPages", applicationPage.getTotalPages());

        return "application/manage";
    }

    @PostMapping("/apply")
    public String applyToPost(@RequestParam Long postId, RedirectAttributes redirectAttributes) {
        try {
            applicationService.applyToPost(postId);
            redirectAttributes.addFlashAttribute("MSG_SUCCESS", "You have applied successfully.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("MSG_INFO", e.getMessage());
        }

        return "redirect:/posts/details/" + postId;
    }

    @PostMapping("/cancel/{id}")
    public String cancel(@PathVariable(name = "id") Long applicationId, RedirectAttributes redirectAttributes) {

        try {
            applicationService.cancelApplication(applicationId);
            redirectAttributes.addFlashAttribute("MSG_INFO", "Post deleted successfully.");

            return "redirect:/application/manage";
        } catch (NotFoundException e) {
            redirectAttributes.addFlashAttribute("MSG_ERROR", "Application not found.");
            return "redirect:/application/manage";
        }
    }
}
