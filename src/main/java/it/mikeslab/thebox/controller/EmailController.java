package it.mikeslab.thebox.controller;

import it.mikeslab.thebox.entity.Course;
import it.mikeslab.thebox.service.CourseService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api")
@RequiredArgsConstructor
public class EmailController {

    private final CourseService courseService;

    @GetMapping("/courses/invite/confirm/{inviteId}")
    public String confirmInvite(@PathVariable String inviteId, @RequestParam String courseId, HttpSession httpSession) {

        // Check if the course exists
        Course course = courseService.fetchCourseById(courseId);

        if (course == null) {
            return "redirect:/login";
        }

        // Check if invite exists
        if (!course.getInvites().containsKey(inviteId)) {
            return "redirect:/login";
        }

        // Check if user is already a member
        if (course.getMembers().contains(course.getInvites().get(inviteId))) {
            return "redirect:/login";
        }

        String username = course.getInvites().get(inviteId);

        // Add user to course
        courseService.addMember(course, username);

        // Remove invite
        courseService.removeInvite(course, inviteId);

        // Check if user is logged, if not redirect to log in
        // otherwise redirects to course overview
        if (httpSession.getAttribute("user") == null) {
            System.out.println("User not logged in");
            return "redirect:/login";
        }

        return "redirect:/overview?courseId=" + courseId;
    }


}
