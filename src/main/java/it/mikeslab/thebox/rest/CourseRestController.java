package it.mikeslab.thebox.rest;

import it.mikeslab.thebox.entity.Course;
import it.mikeslab.thebox.entity.User;
import it.mikeslab.thebox.service.CourseService;
import org.bson.BsonDocument;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseRestController {

    private final CourseService courseService;

    @Autowired
    public CourseRestController(CourseService courseService) {
        this.courseService = courseService;
    }

    // Secured endpoint to get all courses a user has joined
    @GetMapping("/joined/{userId}")
    public List<Course> getCoursesByMember(@PathVariable String userId) {
        return courseService.getCoursesByMember(userId);
    }

    @PostMapping("/create")
    public ResponseEntity<String> createCourse(@RequestBody String payload) {

        if (payload == null) {
            return ResponseEntity.badRequest().body(
                    "Payload is required"
            );
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object objUser = (auth != null) ? auth.getPrincipal() :  null;

        if (objUser == null) {
            return ResponseEntity.badRequest().body(
                    "User not found, refresh your session"
            );
        }

        Bson bson = BsonDocument.parse(payload);
        BsonDocument document = bson.toBsonDocument(BsonDocument.class, null);

        User owner = (User) objUser;

        Course course = new Course(
                null,
                document.getString("title").getValue(),
                document.getString("description").getValue(),
                owner.getUsername(),
                List.of(owner.getId()),
                System.currentTimeMillis(),
                List.of()
        );

        Course result = courseService.createCourse(course);

        if (result == null) {
            return ResponseEntity.badRequest().body(
                    "Failed to create course, try again"
            );
        }

        return ResponseEntity.ok().build();
    }


}
