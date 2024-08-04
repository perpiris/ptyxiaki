package org.iperp.Controllers;

import jakarta.validation.Valid;
import org.iperp.Dtos.PostDto;
import org.iperp.Enums.JobLocation;
import org.iperp.Enums.JobType;
import org.iperp.Services.IApplicationService;
import org.iperp.Services.IPostService;
import org.iperp.Services.ISkillService;
import org.iperp.Services.IUserService;
import org.iperp.Utilities.NotFoundException;
import org.iperp.Utilities.UnauthorizedException;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/posts")
public class PostController {

    private final IPostService postService;
    private final ISkillService skillService;
    private final IUserService userService;
    private final IApplicationService applicationService;

    public PostController(IPostService postService, ISkillService skillService, IUserService userService, IApplicationService applicationService) {
        this.postService = postService;
        this.skillService = skillService;
        this.userService = userService;
        this.applicationService = applicationService;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("jobTypeValues", JobType.values());
        model.addAttribute("jobLocationValues", JobLocation.values());
    }

    @GetMapping("/list")
    public String list(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "id") String sortBy, @RequestParam(required = false) JobType jobType, @RequestParam(required = false) JobLocation jobLocation, final Model model, Principal principal) {

        model.addAttribute("selectedJobType", null);
        model.addAttribute("selectedJobLocation", null);

        Page<PostDto> postPage;
        if (jobType != null && jobLocation != null) {
            postPage = postService.findByJobTypeAndJobLocation(jobType, jobLocation, page, size, sortBy);
        } else if (jobType != null) {
            postPage = postService.findByJobType(jobType, page, size, sortBy);
        } else if (jobLocation != null) {
            postPage = postService.findByJobLocation(jobLocation, page, size, sortBy);
        } else {
            postPage = postService.findAll(page, size, sortBy);
        }

        if (jobType != null) {
            model.addAttribute("selectedJobType", jobType);
        }
        if (jobLocation != null) {
            model.addAttribute("selectedJobLocation", jobLocation);
        }

        if (principal != null) {
            var userSkills = userService.getUserSkills(principal.getName());
            model.addAttribute("userSkills", userSkills);
        } else {
            model.addAttribute("userSkills", java.util.Collections.emptySet());
        }

        model.addAttribute("posts", postPage.getContent());
        model.addAttribute("currentPage", postPage.getNumber());
        model.addAttribute("totalPages", postPage.getTotalPages());

        return "post/list";
    }

    @GetMapping("/details/{id}")
    public String details(@PathVariable("id") long postId, Model model, Principal principal) {
        PostDto post = postService.get(postId);
        model.addAttribute("post", post);
        model.addAttribute("userHasApplied", applicationService.hasUserAppliedToPost(postId));

        if (principal != null) {
            var userSkills = userService.getUserSkills(principal.getName());
            model.addAttribute("userSkills", userSkills);
        } else {
            model.addAttribute("userSkills", java.util.Collections.emptySet());
        }

        return "post/details";
    }

    @PreAuthorize("hasAuthority('RECRUITER')")
    @GetMapping("/create")
    public String create(@ModelAttribute("post") PostDto postDto) {

        return "post/create";
    }

    @PreAuthorize("hasAuthority('RECRUITER')")
    @PostMapping("/create")
    public String create(@ModelAttribute("post") @Valid PostDto postDto, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("skills", postDto.getSkills());
            return "post/create";
        }

        var newPostId = postService.create(postDto);
        redirectAttributes.addFlashAttribute("MSG_SUCCESS", "Post created successfully.");

        return "redirect:/posts/details/" + newPostId;
    }

    @PreAuthorize("hasAuthority('RECRUITER')")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") Long postId, Model model, RedirectAttributes redirectAttributes) {
        var isOwner = postService.isOwner(postId);
        if (!isOwner) {
            redirectAttributes.addFlashAttribute("MSG_ERROR", "Unauthorized: You are not authorized to edit this post.");
            return "redirect:/posts/manage";
        }

        PostDto postDto = postService.get(postId);
        model.addAttribute("post", postDto);
        model.addAttribute("skills", postDto.getSkills()); // Add this line
        model.addAttribute("allSkills", skillService.getAllSkills());
        return "post/edit";
    }

    @PreAuthorize("hasAuthority('RECRUITER')")
    @PostMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") Long postId, @ModelAttribute("post") @Valid PostDto postDto, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

        if (bindingResult.hasErrors()) {
            // Add the skills back to the model
            model.addAttribute("skills", postDto.getSkills());
            model.addAttribute("allSkills", skillService.getAllSkills());
            return "post/edit";
        }

        try {
            postService.edit(postId, postDto);
            redirectAttributes.addFlashAttribute("MSG_SUCCESS", "Post updated successfully.");

            return "redirect:/posts/manage";
        } catch (NotFoundException e) {
            redirectAttributes.addFlashAttribute("MSG_ERROR", "Post not found.");
            return "redirect:/posts/manage";
        } catch (UnauthorizedException e) {
            redirectAttributes.addFlashAttribute("MSG_ERROR", "Unauthorized: You are not authorized to edit this post.");
            return "redirect:/posts/manage";
        }
    }

    @PreAuthorize("hasAuthority('RECRUITER')")
    @PostMapping("/toggle-applications/{id}")
    public String toggleAcceptingApplications(@PathVariable(name = "id") Long postId, RedirectAttributes redirectAttributes) {
        try {
            postService.toggleAcceptingApplications(postId);
            redirectAttributes.addFlashAttribute("MSG_SUCCESS", "Post application status updated successfully.");
        } catch (NotFoundException e) {
            redirectAttributes.addFlashAttribute("MSG_ERROR", "Post not found.");
        } catch (UnauthorizedException e) {
            redirectAttributes.addFlashAttribute("MSG_ERROR", "Unauthorized: You are not authorized to modify this post.");
        }
        return "redirect:/posts/manage";
    }

    @PreAuthorize("hasAuthority('RECRUITER')")
    @PostMapping("/toggle-archive/{id}")
    public String toggleArchive(@PathVariable(name = "id") Long postId, RedirectAttributes redirectAttributes) {
        try {
            postService.toggleArchive(postId);
            redirectAttributes.addFlashAttribute("MSG_SUCCESS", "Post archive status updated successfully.");
        } catch (NotFoundException e) {
            redirectAttributes.addFlashAttribute("MSG_ERROR", "Post not found.");
        } catch (UnauthorizedException e) {
            redirectAttributes.addFlashAttribute("MSG_ERROR", "Unauthorized: You are not authorized to modify this post.");
        }
        return "redirect:/posts/manage";
    }

    @PreAuthorize("hasAuthority('RECRUITER')")
    @GetMapping("/manage")
    public String manage(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "id") String sortBy, final Model model) {

        Page<PostDto> postPage = postService.findAllForManager(page, size, sortBy);
        model.addAttribute("posts", postPage.getContent());
        model.addAttribute("currentPage", postPage.getNumber());
        model.addAttribute("totalPages", postPage.getTotalPages());

        return "post/manage";
    }
}
