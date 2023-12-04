package com.file.storage.api.controllers;

import com.file.storage.api.config.FileStorageProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/api/files")
public class FileStorageController {

    private final Path fileStorageLocation;

    public FileStorageController(FileStorageProperties fileStorageProperties){
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file")MultipartFile file){
        String fileName = StringUtils.cleanPath(file.getName());
        try{
            Path targetLocation = fileStorageLocation.resolve(fileName);
            file.transferTo(targetLocation);
            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/files/download/").path(fileName).toUriString();
            return ResponseEntity.ok("Upload completed! Download link: " + fileDownloadUri);
        }catch (IOException ex){
            return ResponseEntity.badRequest().build();
        }
    }
}