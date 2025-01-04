package it.mikeslab.thebox.rest;

import it.mikeslab.thebox.entity.User;
import it.mikeslab.thebox.service.UserService;
import it.mikeslab.thebox.util.AuthUtil;
import org.bson.BsonDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class UserRestController {

    private final UserService userService;

    @Autowired
    public UserRestController(UserService userService) {
        this.userService = userService;
    }



    @PostMapping("/api/register")
    public ResponseEntity<String> register(@RequestBody String payload) {

        if (payload == null) {
            return ResponseEntity.badRequest().build();
        }

        // Register the user

        // Converting to BSON for MongoDB
        Optional<BsonDocument> optDocument = Optional.ofNullable(BsonDocument.parse(payload));

        if(optDocument.isEmpty()) {
            return ResponseEntity.badRequest().body(
                    "Invalid payload"
            );
        }

        BsonDocument document = optDocument.get();

        // Checking if the document contains all required fields
        if(!AuthUtil.containsFields(document, User.getFields())) {
            return ResponseEntity.badRequest().build();
        }

        // Create the user
        User user = new User(document);

        // Save the user
        try {
            // Check if the user already exists, if it doesn't, registers the user
            userService.checkAndSave(user);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }

        return ResponseEntity.ok().build();
    }

}
