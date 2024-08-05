package org.iperp.Controllers;

import org.iperp.Dtos.SkillDto;
import org.iperp.Entities.UserSkill;
import org.iperp.Services.ISkillService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/skills")
@PreAuthorize("hasAuthority('DEVELOPER')")
public class SkillController {

    private final ISkillService skillService;

    public SkillController(ISkillService skillService) {
        this.skillService = skillService;
    }

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
            redirectAttributes.addFlashAttribute("MSG_SUCCESS", "Skill added successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("MSG_ERROR", "Error adding skill: " + e.getMessage());
        }
        return "redirect:/skills/manage";
    }

    @PostMapping("/edit")
    public String editSkill(@RequestParam Long userSkillId,
                            @RequestParam int years,
                            RedirectAttributes redirectAttributes) {
        try {
            skillService.updateUserSkillYears(userSkillId, years);
            redirectAttributes.addFlashAttribute("MSG_SUCCESS", "Skill years updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("MSG_ERROR", "Error updating skill years: " + e.getMessage());
        }
        return "redirect:/skills/manage";
    }

    @PostMapping("/remove")
    public String removeSkill(@RequestParam Long userSkillId, RedirectAttributes redirectAttributes) {
        try {
            skillService.removeSkillFromUser(userSkillId);
            redirectAttributes.addFlashAttribute("MSG_SUCCESS", "Skill removed successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("MSG_ERROR", "Error removing skill: " + e.getMessage());
        }
        return "redirect:/skills/manage";
    }
}
