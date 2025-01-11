package it.mikeslab.thebox.service;

import com.cloudinary.Cloudinary;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class ImageService {

    private final Cloudinary cloudinary;

    public ImageService(Environment environment) {
        this.cloudinary = new Cloudinary(environment.getProperty("cloudinary.url"));
    }

    /**
     * Upload an image to the server
     * @param multipartFile the image file to upload as a {@link MultipartFile}
     * @return the PublicId of the uploaded image
     */
    public String uploadImage(MultipartFile multipartFile, String folderName) throws IOException, IllegalStateException {

        if (multipartFile == null) {
            return null;
        }

        // Upload the file to Cloudinary

        Map uploadedFile = cloudinary
                .uploader()
                .upload(multipartFile.getBytes(), Map.of("folder", folderName));

        return (String) uploadedFile.get("public_id");

    }


    public void destroy(String publicId, Map options) throws IOException {
        cloudinary.uploader().destroy(publicId, options);
    }
}
