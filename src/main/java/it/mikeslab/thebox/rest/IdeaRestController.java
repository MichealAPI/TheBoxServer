package it.mikeslab.thebox.rest;

import it.mikeslab.thebox.entity.User;
import it.mikeslab.thebox.helper.ValidationResult;
import it.mikeslab.thebox.pojo.Comment;
import it.mikeslab.thebox.pojo.Idea;
import it.mikeslab.thebox.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.bson.BsonDocument;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/ideas")
@RequiredArgsConstructor
public class IdeaRestController {

    private final CourseService courseService;

    @PostMapping("/create")
    public ResponseEntity<String> createIdea(@RequestBody String payload) {
        // Reuse helper method
        ValidationResult validationResult = ValidationResult.validatePayloadAndUser(
                payload,
                courseService
        );

        if (!validationResult.isSuccess()) {
            return validationResult.getErrorResponse();
        }

        BsonDocument document = validationResult.getBsonDocument();
        User owner = validationResult.getUser();
        String courseId = document.getString("courseId").getValue();

        Idea idea = new Idea(
                document.getString("name").getValue(),
                document.getString("details").getValue(),
                owner.getUsername(),
                new HashMap<>()
        );

        boolean success = courseService.upsertIdea(courseId, idea);
        if (!success) {
            return ResponseEntity.badRequest().body("Failed to create idea, try again");
        }

        return ResponseEntity.ok().build();
    }

    @PostMapping("/comment/create")
    public ResponseEntity<String> commentIdea(@RequestBody String payload) {
        // Reuse helper method
        ValidationResult validationResult = ValidationResult.validatePayloadAndUser(
                payload,
                courseService
        );

        if (!validationResult.isSuccess()) {
            return validationResult.getErrorResponse();
        }

        BsonDocument document = validationResult.getBsonDocument();
        String courseId = document.getString("courseId").getValue();

        // Getting it like this in case of Anonymous user
        String username = document.getString("username").getValue();


        Idea idea = courseService.getIdea(
                document.getString("ideaId").getValue(),
                courseId
        );

        if (idea == null) {
            return ResponseEntity.badRequest().body("Idea not found");
        }

        String comment = document.getString("content").getValue();
        idea.getComments().add(
                Comment.builder()
                        .author(username)
                        .content(comment)
                        .timestamp(System.currentTimeMillis())
                        .build()
        );

        courseService.upsertIdea(courseId, idea);

        return ResponseEntity.ok().build();
    }


    @PostMapping("/vote")
    public ResponseEntity<String> voteIdea(@RequestBody String payload) {
        // Reuse helper method
        ValidationResult validationResult = ValidationResult.validatePayloadAndUser(
                payload,
                courseService
        );

        if (!validationResult.isSuccess()) {
            return validationResult.getErrorResponse();
        }

        BsonDocument document = validationResult.getBsonDocument();
        User user = validationResult.getUser();
        String courseId = document.getString("courseId").getValue();

        Idea idea = courseService.getIdea(
                document.getString("ideaId").getValue(),
                courseId
        );

        if (idea == null) {
            return ResponseEntity.badRequest().body("Idea not found");
        }

        int vote = Objects.equals(document.getString("vote").getValue(), "up") ? 1 : -1;
        Idea.Vote requestedVote = Idea.Vote.fromInt(vote);

        // Check if the user has already voted and if the vote is the same as the requested one
        Map<String, Idea.Vote> votes = idea.getVotes();

        if (votes.containsKey(user.getUsername())) {

            Idea.Vote currentVote = votes.get(user.getUsername());

            if(currentVote.name().equals(requestedVote.name())) {
                return ResponseEntity.badRequest().body("You have already voted this way");
            }

            votes.remove(user.getUsername());

        }


        votes.put(
                user.getUsername(),
                requestedVote
        );

        courseService.upsertIdea(courseId, idea);

        // Returns actual vote count
        return ResponseEntity.ok().body(
                String.valueOf(idea.calculateVotes())
        );
    }

}
