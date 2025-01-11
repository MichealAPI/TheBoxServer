package it.mikeslab.thebox.rest;

import it.mikeslab.thebox.service.CourseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class ImageRestController {

    private final CourseService courseService;

    public ImageRestController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping("/upload/{targetCategory}")
    public ResponseEntity<String> upload(@PathVariable String targetCategory, @RequestParam String targetId, @RequestParam String field, @RequestParam String binary, @RequestParam(required = false) Optional<String> nestedReferenceId) {

        if (targetCategory == null || field == null || binary == null) {
            return ResponseEntity.badRequest().build();
        }

        // Upload the file

        // Sanitize target
        targetCategory = targetCategory.toLowerCase().trim();

        switch (targetCategory) {
            case "course":
                courseService.uploadFile(
                        targetId,
                        field,
                        binary
                );
                break;
            case "idea":

                if(nestedReferenceId.isEmpty()) {
                    return ResponseEntity.badRequest().build();
                }

                courseService.uploadIdeaFile(
                        targetId,
                        nestedReferenceId.get(),
                        field,
                        binary
                );

                break;
            default:
                return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().body(
                "File uploaded"
        );


    }


    @GetMapping("/download")
    public ResponseEntity<String> download(@RequestParam String targetCategory, @RequestParam String targetId, @RequestParam String field, @RequestParam(required = false) Optional<String> nestedReferenceId) {

        if (targetCategory == null || field == null) {
            return ResponseEntity.badRequest().build();
        }

        // Sanitize target
        targetCategory = targetCategory.toLowerCase().trim();
        String response;

        switch (targetCategory) {
            case "course":
                response = courseService.fetchCourseById(targetId)
                        .getSettings()
                        .getOrDefault(field, null);
                break;
            case "idea":

                if (nestedReferenceId.isEmpty()) {
                    return ResponseEntity.badRequest().build();
                }

                response = courseService.fetchCourseById(targetId)
                        .getIdeas()
                        .get(nestedReferenceId.get())
                        .getSettings()
                        .getOrDefault(field, null);

                break;
            default:
                return ResponseEntity.badRequest().build();
        }

        if (response == null) {
            return ResponseEntity.badRequest().build();
        }

        System.out.println(response);

        return ResponseEntity.ok().body(response);
    }


}
