package it.mikeslab.thebox.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface ImageUploadService {

    boolean uploadAndSave(String targetId, String field, MultipartFile multipartFile, Optional<String> nestedReferenceId);

}

