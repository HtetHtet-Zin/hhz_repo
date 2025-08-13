package com.dat.event.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface ImageStorageService {
    void saveImage(MultipartFile file, String eventName);

    Map<String, String> findEventImage(List<String> eventNames);
}
