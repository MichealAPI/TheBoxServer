package it.mikeslab.thebox.rest;

import it.mikeslab.thebox.entity.Course;
import it.mikeslab.thebox.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    @Autowired
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    // Secured endpoint to get all courses a user has joined
    @GetMapping("/joined/{userId}")
    public List<Course> getCoursesByMember(@PathVariable String userId) {
        return courseService.getCoursesByMember(userId);
    }


}
