package com.quartet.car_rental.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class CarImageController {

    private static final String IMAGE_DIR = "images/cars/";

    @GetMapping("/images/cars/{filename:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) throws Exception {
        Path path = Paths.get(IMAGE_DIR + filename);
        Resource resource = new UrlResource(path.toUri());
        return ResponseEntity.ok().body(resource);
    }
}

