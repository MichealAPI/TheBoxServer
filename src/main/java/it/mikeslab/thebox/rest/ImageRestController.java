package it.mikeslab.thebox.rest;

import com.cloudinary.Cloudinary;
import it.mikeslab.thebox.service.CourseImageUploadService;
import it.mikeslab.thebox.service.CourseService;
import it.mikeslab.thebox.service.IdeaImageUploadService;
import it.mikeslab.thebox.service.UserImageUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class ImageRestController {

    private final CourseService courseService;
    private final CourseImageUploadService courseImageUploadService;
    private final IdeaImageUploadService ideaImageUploadService;
    private final UserImageUploadService userImageUploadService;

    @PostMapping("/upload/{targetCategory}")
    public ResponseEntity<String> upload(@PathVariable String targetCategory, @RequestParam String targetId, @RequestParam String field, @RequestParam MultipartFile file, @RequestParam(required = false) Optional<String> nestedReferenceId) {

        if (targetCategory == null || field == null || file == null) {
            return ResponseEntity.badRequest().build();
        }

        // Upload the file

        // Sanitize target
        targetCategory = targetCategory.toLowerCase().trim();

        switch (targetCategory) {
            case "course":
                courseImageUploadService.uploadAndSave(
                        targetId,
                        field,
                        file,
                        Optional.empty()
                );
                break;
            case "idea":

                if(nestedReferenceId.isEmpty()) {
                    return ResponseEntity.badRequest().build();
                }

                ideaImageUploadService.uploadAndSave(
                        targetId,
                        field,
                        file,
                        nestedReferenceId
                );

                break;
            case "user":

                userImageUploadService.uploadAndSave(
                        targetId,
                        field,
                        file,
                        Optional.empty()
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

        return ResponseEntity.ok().body(response);
    }


}
