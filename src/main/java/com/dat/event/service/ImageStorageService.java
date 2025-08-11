package com.dat.event.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImageStorageService {
    void saveImage(MultipartFile file, String eventName);
}
