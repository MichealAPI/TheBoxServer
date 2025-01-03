package it.mikeslab.thebox.rest;

import it.mikeslab.thebox.pojo.Idea;
import it.mikeslab.thebox.entity.User;
import it.mikeslab.thebox.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.bson.BsonDocument;
import org.bson.conversions.Bson;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("/api/ideas")
@RequiredArgsConstructor
public class IdeaRestController {

    private final CourseService courseService;

    @PostMapping("/create")
    public ResponseEntity<String> createIdea(@RequestBody String payload) {

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

        if (courseService.getCourse(document.getString("courseId").getValue()) == null) {
            return ResponseEntity.badRequest().body(
                    "Course not found"
            );
        }

        Idea idea = new Idea(
                document.getString("name").getValue(),
                document.getString("details").getValue(),
                owner.getUsername(),
                new HashMap<>()
        );

        String courseId = document.getString("courseId").getValue();
        Idea result = courseService.addIdeaToCourse(
                courseId,
                idea
        );

        if (result == null) {
            return ResponseEntity.badRequest().body(
                    "Failed to create course, try again"
            );
        }

        return ResponseEntity.ok().build();
    }


}
