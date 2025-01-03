package it.mikeslab.thebox.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserRestController {

    @PostMapping("/api/register")
    public ResponseEntity<String> register(@RequestBody String payload) {
        System.out.println(payload);
        return ResponseEntity.ok("{\"status\": \"Registered\"}");
    }

}
