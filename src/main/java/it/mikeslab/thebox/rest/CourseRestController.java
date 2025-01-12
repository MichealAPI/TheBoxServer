package it.mikeslab.thebox.rest;

import it.mikeslab.thebox.entity.Course;
import it.mikeslab.thebox.entity.User;
import it.mikeslab.thebox.service.CourseService;
import it.mikeslab.thebox.service.UserService;
import lombok.RequiredArgsConstructor;
import org.bson.BsonDocument;
import org.bson.conversions.Bson;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/courses")
public class CourseRestController {

    private final CourseService courseService;
    private final UserService userService;

    // Secured endpoint to get all courses a user has joined
    @GetMapping("/joined/{userId}")
    public List<Course> getCoursesByMember(@PathVariable String userId) {
        return courseService.fetchCoursesByMember(userId);
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


        Course course = Course.builder()
                .title(document.getString("title").getValue())
                .description(document.getString("description").getValue())
                .ownerUsername(owner.getUsername())
                .members(List.of(owner.getUsername()))
                .timestamp(System.currentTimeMillis())
                .ideas(Collections.emptyMap())
                .invites(Collections.emptyMap())
                .build();

        Course result = courseService.createCourse(course);

        if (result == null) {
            return ResponseEntity.badRequest().body(
                    "Failed to create course, try again"
            );
        }

        return ResponseEntity.ok().build();
    }




}
