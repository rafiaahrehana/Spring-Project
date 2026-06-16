package com.StartupSAAS.serviceImpl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ImageService {

    @Value("${image.upload.dir}")
    private String uploadDir;

    public String upload(MultipartFile file, String folder, String name) {
        try {
            Path path = Paths.get(uploadDir, folder);
            if (!Files.exists(path)) Files.createDirectories(path);

            String original = file.getOriginalFilename();
            String ext = (original != null && original.contains("."))
                    ? original.substring(original.lastIndexOf("."))
                    : "";

            String fileName = name.trim().replaceAll("\\s+", "_") + "_" + UUID.randomUUID() + ext;
            Files.copy(file.getInputStream(), path.resolve(fileName));
            return fileName;

        } catch (Exception e) {
            throw new RuntimeException("Image upload failed");
        }
    }
}
