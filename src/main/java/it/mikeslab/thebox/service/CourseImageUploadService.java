package it.mikeslab.thebox.service;

import it.mikeslab.thebox.entity.Course;
import it.mikeslab.thebox.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CourseImageUploadService implements ImageUploadService {

    private final ImageService imageService;
    private final CourseService courseService;
    private final UserService userService;

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

            // Checking if who is calling this method is authorized to do so
            User user = userService.getUserByUsername(
                    userService.getAuthenticatedUser().getUsername()
            );

            if (!Objects.equals(course.getOwnerUsername(), user.getUsername())) {
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
