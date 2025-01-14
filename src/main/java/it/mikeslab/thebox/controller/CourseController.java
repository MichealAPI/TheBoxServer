package it.mikeslab.thebox.controller;

import it.mikeslab.thebox.entity.Course;
import it.mikeslab.thebox.entity.User;
import it.mikeslab.thebox.service.CourseService;
import it.mikeslab.thebox.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;
    private final UserService userService;

    @GetMapping("/courses")
    public String coursesPage(Model model, User user) {

        if (user == null) {
            return "redirect:/login";
        }

        // Get updated user instance
        user = userService.getUserByUsername(user.getUsername());

        List<Course> courses = courseService.fetchCoursesByMember(user.getUsername());
        model.addAttribute("courses", courses);
        model.addAttribute("userInitial", user.getUsername().charAt(0));

        model.addAllAttributes(user.toMap());

        model.addAttribute("user", user);

        // Add settings
        model.addAttribute(
                "settings",
                user.getParsedSettings()
        );

        return "courses";
    }

}
