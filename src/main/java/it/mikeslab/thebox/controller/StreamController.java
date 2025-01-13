package it.mikeslab.thebox.controller;

import it.mikeslab.thebox.entity.Course;
import it.mikeslab.thebox.entity.User;
import it.mikeslab.thebox.pojo.Idea;
import it.mikeslab.thebox.service.CourseService;
import it.mikeslab.thebox.service.UserService;
import it.mikeslab.thebox.util.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class StreamController {

    private final CourseService courseService;
    private final UserService userService;

    @GetMapping("/stream")
    public String streamPage(@RequestParam String courseId, @RequestParam String ideaId, Model model, User user) {

        // Validate course and user
        Course course = courseService.fetchCourseById(courseId);
        if (course == null) {
            return "redirect:/courses";
        }

        if (user == null) {
            return "redirect:/login";
        }

        // Fetch updated user instance
        user = userService.getUserByUsername(user.getUsername());
        if (user == null) {
            return "redirect:/login"; // Safety check in case user is null
        }

        // Set model attributes related to user and course
        model.addAllAttributes(user.toMap());
        model.addAttribute("userInitial", user.getUsername().charAt(0));
        model.addAttribute("course", course);
        model.addAttribute("settings", user.getParsedSettings());
        model.addAttribute("user", user);

        // Fetch idea and handle profile pictures
        Idea idea = course.getIdeas().get(ideaId);
        if (idea != null) {
            addProfilePicturesToComments(idea);
            model.addAttribute("idea", idea);
        }

        return "stream";
    }

    private void addProfilePicturesToComments(Idea idea) {
        idea.getComments().forEach(comment -> {
            String author = comment.getAuthor();

            if ("anonymous".equalsIgnoreCase(author)) {
                comment.setProfilePicture(Optional.empty());
            } else {
                User commentOwner = userService.getUserByUsername(author);

                boolean isOwnerAndSettingsNull = commentOwner == null || commentOwner.getSettings() == null;

                comment.setProfilePicture(!isOwnerAndSettingsNull
                        ? Optional.ofNullable(StringUtil.valueOfNullable(commentOwner.getSettings().get("PROFILE_PICTURE")))
                        : Optional.empty());
            }
        });
    }

}
