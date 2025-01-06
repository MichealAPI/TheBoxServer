package it.mikeslab.thebox.controller;

import it.mikeslab.thebox.entity.Course;
import it.mikeslab.thebox.entity.User;
import it.mikeslab.thebox.service.CourseService;
import it.mikeslab.thebox.service.SettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class StreamController {

    private final CourseService courseService;
    private final SettingsService settingsService;

    @GetMapping("/stream")
    public String streamPage(@RequestParam String courseId, @RequestParam String ideaId, Model model, User user) {

        Course course = courseService.fetchCourseById(courseId);

        if (course == null) {
            return "redirect:/courses";
        }

        if (user == null) {
            return "redirect:/login";
        }

        model.addAllAttributes(user.toMap());
        model.addAttribute("userInitial", user.getUsername().charAt(0));
        model.addAttribute("course", course);
        model.addAttribute("idea", course.getIdeas().get(ideaId));

        // Add settings
        model.addAttribute(
                "settings",
                user.getParsedSettings()
        );

        return "stream";
    }

}
