package it.mikeslab.thebox.controller;

import it.mikeslab.thebox.entity.Course;
import it.mikeslab.thebox.entity.User;
import it.mikeslab.thebox.service.CourseService;
import it.mikeslab.thebox.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class OverviewController {

    private final CourseService courseService;
    private final UserService userService;

    @GetMapping("/overview")
    public String overviewPage(@RequestParam String courseId, Model model, User user) {

        if (user == null) {
            return "redirect:/login";
        }

        // Get updated user instance
        user = userService.getUserByUsername(user.getUsername());

        model.addAllAttributes(user.toMap());
        model.addAttribute("userInitial", user.getUsername().charAt(0));

        Course course = courseService.fetchCourseById(courseId);

        if (course == null) {
            return "redirect:/courses";
        }

        model.addAttribute("course", course);
        model.addAttribute("ideas", courseService.getAllIdeasByCourseId(courseId));

        model.addAttribute("user", user);

        // Add settings
        model.addAttribute(
                "settings",
                user.getParsedSettings()
        );

        return "overview";
    }

}
