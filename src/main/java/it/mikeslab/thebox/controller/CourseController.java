package it.mikeslab.thebox.controller;

import it.mikeslab.thebox.entity.Course;
import it.mikeslab.thebox.entity.User;
import it.mikeslab.thebox.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class CourseController {

    private final CourseService courseService;

    @Autowired
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping("/courses")
    public String coursesPage(Model model) {

        // Retrieve user by session
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object objUser = (auth != null) ? auth.getPrincipal() :  null;


        if (objUser instanceof User user) {
            List<Course> courses = courseService.getCoursesByMember(user.getId());
            model.addAttribute("courses", courses);

            model.addAttribute("userInitial", user.getFirstName().charAt(0));

            model.addAllAttributes(user.toMap());

        } else {
            return "redirect:/login";
        }

        return "courses";
    }

}
