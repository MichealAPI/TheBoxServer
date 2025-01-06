package it.mikeslab.thebox.controller;

import it.mikeslab.thebox.entity.Course;
import it.mikeslab.thebox.entity.User;
import it.mikeslab.thebox.service.CourseService;
import it.mikeslab.thebox.service.SettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;
    private final SettingsService settingsService;

    @GetMapping("/courses")
    public String coursesPage(Model model, User user) {

        if (user == null) {
            return "redirect:/login";
        }

        System.out.println(user.toString());
        System.out.println("User: " + user.getUsername());

        List<Course> courses = courseService.getCoursesByMember(user.getUsername());
        model.addAttribute("courses", courses);
        model.addAttribute("userInitial", user.getUsername().charAt(0));

        model.addAllAttributes(user.toMap());

        // Add settings
        model.addAttribute(
                "settings",
                user.getParsedSettings()
        );

        return "courses";
    }

}
