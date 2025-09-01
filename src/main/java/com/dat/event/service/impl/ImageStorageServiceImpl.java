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
    public void saveImage(MultipartFile file, String name, boolean isEvent) {
        try {
            String projectRoot = System.getProperty("user.dir");

            Path imageDir = Paths.get(projectRoot, "photo", isEvent ? "eventPhoto" : "signature");
            if (!Files.exists(imageDir)) {
                Files.createDirectories(imageDir);
            }

            String extension = ".jpg";

            String renamedFileName = name.concat(extension);
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
            Path oldNamePath = of.resolve(oldEventName+".jpg");


            Path newNamePath = of.resolve(newEventName+".jpg");

            Files.move(oldNamePath, newNamePath, StandardCopyOption.REPLACE_EXISTING);
            Files.deleteIfExists(oldNamePath);

        } catch (IOException exception) {
            log.error("Error saving image: {}", exception.getMessage());
        }
    }


}
