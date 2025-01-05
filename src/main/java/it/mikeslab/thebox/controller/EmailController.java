package it.mikeslab.thebox.controller;

import it.mikeslab.thebox.entity.Course;
import it.mikeslab.thebox.service.CourseService;
import it.mikeslab.thebox.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class EmailController {

    private final CourseService courseService;
    private final UserService userService;

    @GetMapping("/registration/verify/sent")
    public String verificationSent(Model model, @RequestParam String email) {

        model.addAttribute("email", email);

        return "email_verification";
    }

    @GetMapping("/api/courses/invite/confirm/{inviteId}")
    public String confirmInvite(@PathVariable String inviteId, @RequestParam String courseId, HttpSession httpSession) {

        // Check if the course exists
        Course course = courseService.fetchCourseById(courseId);

        if (course == null) {
            return "redirect:/login?join=failed";
        }

        // Check if invite exists
        if (!course.getInvites().containsKey(inviteId)) {
            return "redirect:/login?join=failed";
        }

        // Check if user is already a member
        if (course.getMembers().contains(course.getInvites().get(inviteId))) {
            return "redirect:/login?join=failed";
        }

        String username = course.getInvites().get(inviteId);

        // Add user to course
        courseService.addMember(course, username);

        // Remove invite
        courseService.removeInvite(course, inviteId);

        // Check if user is logged, if not redirect to log in
        // otherwise redirects to course overview
        if (httpSession.getAttribute("user") == null) {
            return "redirect:/login?join=success";
        }

        return "redirect:/overview?join=success&courseId=" + courseId;
    }


    @GetMapping("/registration/verify")
    public String verifyUser(@RequestParam String token, @RequestParam String username) {
        // Check if token exists

        if (!userService.verifyUser(username, token)) {
            return "redirect:/login?verification=failed";
        }

        return "redirect:/login?verification=success";
    }


}
