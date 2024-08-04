package org.iperp.Controllers;

import org.iperp.Dtos.SkillDto;
import org.iperp.Entities.UserSkill;
import org.iperp.Services.ISkillService;
import org.iperp.Services.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/skills")
@PreAuthorize("hasAuthority('USER')")
public class SkillController {

    @Autowired
    private ISkillService skillService;
    @Autowired
    private IUserService userService;

    @GetMapping("/manage")
    public String manage(Model model) {
        List<UserSkill> userSkills = skillService.getUserSkills();
        List<SkillDto> allSkills = skillService.getAllSkills();

        model.addAttribute("userSkills", userSkills);
        model.addAttribute("allSkills", allSkills);

        return "skills/manage";
    }

    @PostMapping("/add")
    public String addSkill(@RequestParam String skillDescription, @RequestParam int years, RedirectAttributes redirectAttributes) {
        try {
            skillService.addSkillToUser(skillDescription, years);
            redirectAttributes.addFlashAttribute("successMessage", "Skill added successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error adding skill: " + e.getMessage());
        }
        return "redirect:/skills/manage";
    }

    @PostMapping("/remove")
    public String removeSkill(@RequestParam Long skillId, RedirectAttributes redirectAttributes) {
        try {
            skillService.removeSkillFromUser(skillId);
            redirectAttributes.addFlashAttribute("successMessage", "Skill removed successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error removing skill: " + e.getMessage());
        }
        return "redirect:/skills/manage";
    }
}
