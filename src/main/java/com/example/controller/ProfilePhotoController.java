package com.example.controller;


import com.example.service.AttachService;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProfilePhotoController {
    private final AttachService attachService;
    public ProfilePhotoController(AttachService attachService) { this.attachService = attachService; }

    @GetMapping("/profiles/{id}")
    public ResponseEntity<Resource> open(@PathVariable String id) {
        return attachService.open(id);
    }
}