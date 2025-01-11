package it.mikeslab.thebox.service;

import it.mikeslab.thebox.entity.Course;
import it.mikeslab.thebox.pojo.Idea;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IdeaImageUploadService implements ImageUploadService {

    private final ImageService imageService;
    private final CourseService courseService;

    @Override
    public boolean uploadAndSave(String targetId, String field, MultipartFile multipartFile, Optional<String> nestedReferenceId) {

        if(nestedReferenceId.isEmpty()) {
            return false;
        }

        try {

            String publicId = imageService.uploadImage(
                    multipartFile,
                    "courses/" + targetId + "/" + nestedReferenceId.get() + "/" + field
            );
            
            if (publicId == null) {
                return false;
            }
            
            // Save the URL to the database
            Course course = courseService.fetchCourseById(targetId);
            
            if (course == null) {
                return false;
            }

            Idea idea = course.getIdeas().getOrDefault(
                    nestedReferenceId.get(),
                    null
            );

            if (idea == null) {
                return false;
            }

            // Destroy the old image if it exists
            if (idea.getSettings().containsKey(field)) {
                imageService.destroy(
                        idea.getSettings().get(field),
                        null
                );
            }

            idea.getSettings().put(field, publicId);
            courseService.upsertIdea(course.getId(), idea);
            return true;
            
        } catch (IOException e) {
            return false;
        }
    }
}
