package it.mikeslab.thebox.rest;

import it.mikeslab.thebox.service.CourseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class UploadRestController {

    private final CourseService courseService;

    public UploadRestController(CourseService courseService) {
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


}
