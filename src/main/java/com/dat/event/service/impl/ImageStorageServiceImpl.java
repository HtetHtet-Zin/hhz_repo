package com.dat.event.service.impl;

import com.dat.event.service.ImageStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Slf4j
@Service
public class ImageStorageServiceImpl implements ImageStorageService {

    @Override
    public void saveImage(MultipartFile file, String eventName) {
        try {
            String projectRoot = System.getProperty("user.dir");

            Path imageDir = Paths.get(projectRoot, "photo", "eventphoto");
            if (!Files.exists(imageDir)) {
                Files.createDirectories(imageDir);
            }

            String originalFilename = file.getOriginalFilename();
            String extension = "";

            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
            } else {
                log.warn("No file extension found. Defaulting to .jpg");
                extension = ".jpg";
            }

            String renamedFileName = eventName + extension;
            Path filePath = imageDir.resolve(renamedFileName);

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            log.info("Photo saved as {}", renamedFileName);

        } catch (IOException exception) {
            log.error("Error saving image: {}", exception.getMessage());
        }
    }

}
