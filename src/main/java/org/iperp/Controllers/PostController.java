package org.iperp.Controllers;

import jakarta.validation.Valid;
import org.iperp.Dtos.PostDto;
import org.iperp.Enums.JobLocation;
import org.iperp.Enums.JobType;
import org.iperp.Services.IPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private IPostService postService;

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("jobTypeValues", JobType.values());
        model.addAttribute("jobLocationValues", JobLocation.values());
    }

    @GetMapping("/list")
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       @RequestParam(defaultValue = "id") String sortBy,
                       @RequestParam(required = false) JobType jobType,
                       @RequestParam(required = false) JobLocation jobLocation,
                       final Model model) {

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

        model.addAttribute("posts", postPage.getContent());
        model.addAttribute("currentPage", postPage.getNumber());
        model.addAttribute("totalPages", postPage.getTotalPages());

        return "post/list";
    }

    @GetMapping("/details/{id}")
    public String details(@PathVariable("id") long id, Model model) {

        model.addAttribute("post", postService.get(id));
        return "post/details";
    }

    @GetMapping("/create")
    public String create(@ModelAttribute("post") PostDto postDto) {

        return "post/create";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute("post") @Valid PostDto postDto, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "post/create";
        }

        postService.create(postDto);
        redirectAttributes.addFlashAttribute("MSG_SUCCESS", "Post created successfully.");

        return "redirect:/posts/list";
    }

    @GetMapping("/update/{id}")
    public String update(@PathVariable(name = "id") Long id, Model model) {

        model.addAttribute("post", postService.get(id));
        return "post/update";
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable(name = "id") Long id, @ModelAttribute("post") @Valid PostDto postDto, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "post/update";
        }

        postService.update(id, postDto);
        redirectAttributes.addFlashAttribute("MSG_SUCCESS", "Post updated successfully.");

        return "redirect:/posts/manage";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable(name = "id") Long id, RedirectAttributes redirectAttributes) {

        postService.delete(id);
        redirectAttributes.addFlashAttribute("MSG_INFO", "Post deleted successfully.");

        return "redirect:/manage";
    }

    @GetMapping("/manage")
    public String manage(@RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "10") int size,
                         @RequestParam(defaultValue = "id") String sortBy,
                         final Model model) {

        Page<PostDto> postPage = postService.findAllForManager(page, size, sortBy);
        model.addAttribute("posts", postPage.getContent());
        model.addAttribute("currentPage", postPage.getNumber());
        model.addAttribute("totalPages", postPage.getTotalPages());

        return "post/manage";
    }
}
