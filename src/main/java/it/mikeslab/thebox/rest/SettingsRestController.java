package it.mikeslab.thebox.rest;

import it.mikeslab.thebox.entity.User;
import it.mikeslab.thebox.service.SettingsService;
import lombok.RequiredArgsConstructor;
import org.bson.BsonDocument;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SettingsRestController {

    private final SettingsService settingsService;

    @PostMapping("/settings")
    public ResponseEntity<String> updateSettings(@RequestBody String payload, User user) {
        if (user == null) {
            return ResponseEntity.badRequest().body("User not authenticated");
        }

        // Parse the incoming JSON payload into a map
        BsonDocument document = BsonDocument.parse(payload);

        // Convert document to a map of updated settings
        Map<String, Object> updatedSettings = new HashMap<>();

        document.forEach((key, value) -> {
            // Convert BSON values into Java objects
            updatedSettings.put(key, value.asString()); // todo: handle other types
        });

        // Call the service to update settings for the user
        settingsService.updateSettings(user.getUsername(), updatedSettings);

        return ResponseEntity.ok("Settings successfully updated");
    }

}
