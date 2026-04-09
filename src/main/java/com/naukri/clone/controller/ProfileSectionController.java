package com.naukri.clone.controller;

import com.naukri.clone.model.*;
import com.naukri.clone.repository.*;
import com.naukri.clone.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile")
public class ProfileSectionController {

    @Autowired private UserService userService;
    @Autowired private EmploymentRepository employmentRepo;
    @Autowired private EducationRepository educationRepo;
    @Autowired private ProjectRepository projectRepo;
    @Autowired private ItSkillRepository itSkillRepo;

    //EMPLOYMENT....
    @PostMapping("/employment/add")
    public String addEmployment(@ModelAttribute Employment emp, Authentication auth, RedirectAttributes ra) {
        User user = userService.findByEmail(auth.getName()).orElseThrow();
        emp.setUser(user);
        employmentRepo.save(emp);
        ra.addFlashAttribute("sectionMsg", "Employment added!");
        return "redirect:/profile/view";
    }

    @PostMapping("/employment/delete/{id}")
    public String deleteEmployment(@PathVariable Long id, RedirectAttributes ra) {
        employmentRepo.deleteById(id);
        ra.addFlashAttribute("sectionMsg", "Employment removed.");
        return "redirect:/profile/view";
    }

    //EDUCATION...
    @PostMapping("/education/add")
    public String addEducation(@ModelAttribute Education edu, Authentication auth, RedirectAttributes ra) {
        User user = userService.findByEmail(auth.getName()).orElseThrow();
        edu.setUser(user);
        educationRepo.save(edu);
        ra.addFlashAttribute("sectionMsg", "Education added!");
        return "redirect:/profile/view";
    }

    @PostMapping("/education/delete/{id}")
    public String deleteEducation(@PathVariable Long id, RedirectAttributes ra) {
        educationRepo.deleteById(id);
        ra.addFlashAttribute("sectionMsg", "Education removed.");
        return "redirect:/profile/view";
    }

    //IT SKILLS
    @PostMapping("/itskill/add")
    public String addItSkill(@ModelAttribute ItSkill skill, Authentication auth, RedirectAttributes ra) {
        User user = userService.findByEmail(auth.getName()).orElseThrow();
        skill.setUser(user);
        itSkillRepo.save(skill);
        ra.addFlashAttribute("sectionMsg", "IT Skill added!");
        return "redirect:/profile/view";
    }

    @PostMapping("/itskill/delete/{id}")
    public String deleteItSkill(@PathVariable Long id, RedirectAttributes ra) {
        itSkillRepo.deleteById(id);
        return "redirect:/profile/view";
    }

    //PROJECTS
    @PostMapping("/project/add")
    public String addProject(@ModelAttribute Project project, Authentication auth, RedirectAttributes ra) {
        User user = userService.findByEmail(auth.getName()).orElseThrow();
        project.setUser(user);
        projectRepo.save(project);
        ra.addFlashAttribute("sectionMsg", "Project added!");
        return "redirect:/profile/view";
    }

    @PostMapping("/project/delete/{id}")
    public String deleteProject(@PathVariable Long id, RedirectAttributes ra) {
        projectRepo.deleteById(id);
        return "redirect:/profile/view";
    }
}
