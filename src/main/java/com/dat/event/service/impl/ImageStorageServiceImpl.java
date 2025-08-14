package com.dat.event.service.impl;

import com.dat.event.service.ImageStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class ImageStorageServiceImpl implements ImageStorageService {

    @Override
    public void saveImage(MultipartFile file, String eventName) {
        try {
            String projectRoot = System.getProperty("user.dir");

            Path imageDir = Paths.get(projectRoot, "photo", "eventPhoto");
            if (!Files.exists(imageDir)) {
                Files.createDirectories(imageDir);
            }

            String originalFilename = file.getOriginalFilename();
            String extension = ".jpg";

//            if (originalFilename != null && originalFilename.contains(".")) {
//                extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
//            } else {
//                log.warn("No file extension found. Defaulting to .jpg");
//                extension = ".jpg";
//            }

            String renamedFileName = eventName.concat(extension);
            Path filePath = imageDir.resolve(renamedFileName);

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            log.info("Photo saved as {}", renamedFileName);

        } catch (IOException exception) {
            log.error("Error saving image: {}", exception.getMessage());
        }
    }

    @Override
    public Map<String, String> findEventImage(List<String> eventNames) {
        Path folder = Paths.get("photo/eventPhoto");
        Map<String, String> result = new HashMap<>();

        try (Stream<Path> files = Files.list(folder)) {
            Map<String, String> allFiles = files.filter(Files::isRegularFile).collect(Collectors.toMap(f -> f.getFileName().toString().substring(0, f.getFileName().toString().lastIndexOf('.')), f -> f.getFileName().toString()));

            for (String name : eventNames) {
                result.put(name, allFiles.getOrDefault(name, "default.jpg"));
            }
        } catch (IOException e) {
            for (String name : eventNames) {
                result.put(name, "default.jpg");
            }
        }

        return result;
    }

    @Override
    public void updateImage(String oldEventName, String newEventName) {
        try {
            String projectRoot = System.getProperty("user.dir");

            Path of = Path.of(projectRoot, "photo", "eventPhoto");
            Path oldName = of.resolve(oldEventName+".jpg");


            Path newName = of.resolve(newEventName+".jpg");

            Files.copy(oldName, newName, StandardCopyOption.REPLACE_EXISTING);


        } catch (IOException exception) {
            log.error("Error saving image: {}", exception.getMessage());
        }
    }


}
