package org.iperp.Controllers;

import org.iperp.Dtos.UserApplicationDto;
import org.iperp.Enums.ApplicationStatus;
import org.iperp.Services.IApplicationService;
import org.iperp.Utilities.NotFoundException;
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

    private final IApplicationService applicationService;

    public ApplicationController(IApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PreAuthorize("hasAuthority('DEVELOPER')")
    @GetMapping("/my-applications")
    public String myApplications(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "id") String sortBy, final Model model) {

        Page<UserApplicationDto> applicationPage = applicationService.findAllForApplicant(page, size, sortBy);
        model.addAttribute("applications", applicationPage.getContent());
        model.addAttribute("currentPage", applicationPage.getNumber());
        model.addAttribute("totalPages", applicationPage.getTotalPages());

        return "application/my-applications";
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
            redirectAttributes.addFlashAttribute("MSG_INFO", "Application withdrawn successfully.");

            return "redirect:/application/manage";
        } catch (NotFoundException e) {
            redirectAttributes.addFlashAttribute("MSG_ERROR", "Application not found.");
            return "redirect:/application/manage";
        }
    }

    @PostMapping("/update-status")
    public String updateApplicationStatus(@RequestParam Long applicationId,
                                          @RequestParam Long postId,
                                          @RequestParam ApplicationStatus status,
                                          RedirectAttributes redirectAttributes) {
        try {
            applicationService.updateApplicationStatus(applicationId, status);
            redirectAttributes.addFlashAttribute("message", "Application " + status.getDisplayName().toLowerCase() + " successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update application status: " + e.getMessage());
        }
        return "redirect:/posts/" + postId + "/applications";
    }
}
