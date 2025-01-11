package it.mikeslab.thebox.service;

import it.mikeslab.thebox.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserImageUploadService implements ImageUploadService {

    private final ImageService imageService;
    private final UserService userService;

    @Override
    public boolean uploadAndSave(String targetId, String field, MultipartFile multipartFile, Optional<String> nestedReferenceId) {

        try {
            String url = imageService.uploadImage(multipartFile, "users/" + targetId + "/" + field);
            
            if (url == null) {
                return false;
            }
            
            // Save the URL to the database
            User user = userService.getUserByUsername(targetId);
            
            if (user == null) {
                return false;
            }

            // Destroy the old image if it exists
            if (user.getSettings().containsKey(field)) {
                imageService.destroy(
                        (String) user.getSettings().get(field),
                        null
                );
            }

            user.getSettings().put(field, url);
            userService.updateUser(user);

            return true;
            
        } catch (IOException e) {
            return false;
        }
    }
}
