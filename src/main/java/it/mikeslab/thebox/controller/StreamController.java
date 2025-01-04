package it.mikeslab.thebox.controller;

import it.mikeslab.thebox.entity.Course;
import it.mikeslab.thebox.entity.User;
import it.mikeslab.thebox.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class StreamController {

    private final CourseService courseService;

    @GetMapping("/stream")
    public String streamPage(@RequestParam String courseId, @RequestParam String ideaId, Model model) {

        Course course = courseService.fetchCourseById(courseId);

        if (course == null) {
            return "redirect:/courses";
        }

        // Retrieve user by session
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object objUser = (auth != null) ? auth.getPrincipal() :  null;

        if (objUser == null) {
            return "redirect:/login";
        }

        if (objUser instanceof User user) {
            model.addAllAttributes(user.toMap());
            model.addAttribute("userInitial", user.getUsername().charAt(0));
        }

        model.addAttribute("course", course);
        model.addAttribute("idea", course.getIdeas().get(ideaId));

        return "stream";
    }

}
