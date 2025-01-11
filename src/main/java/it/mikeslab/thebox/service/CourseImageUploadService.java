package it.mikeslab.thebox.service;

import it.mikeslab.thebox.entity.Course;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CourseImageUploadService implements ImageUploadService {

    private final ImageService imageService;
    private final CourseService courseService;

    @Override
    public boolean uploadAndSave(String targetId, String field, MultipartFile multipartFile, Optional<String> nestedReferenceId) {

        try {
            String url = imageService.uploadImage(multipartFile, "courses/" + targetId + "/" + field);
            
            if (url == null) {
                return false;
            }
            
            // Save the URL to the database
            Course course = courseService.fetchCourseById(targetId);
            
            if (course == null) {
                return false;
            }

            // Destroy the old image if it exists
            if (course.getSettings().containsKey(field)) {
                imageService.destroy(
                        course.getSettings().get(field),
                        null
                );
            }

            course.getSettings().put(field, url);
            courseService.saveCourse(course);
            return true;
            
        } catch (IOException e) {
            return false;
        }
    }
}
