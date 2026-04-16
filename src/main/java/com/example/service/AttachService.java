package com.example.service;


import com.example.dto.AttachDTO;
import com.example.dto.ImageUploadItem;
import com.example.entity.AttachEntity;
import com.example.exseption.AppBadException;
import com.example.repository.AttachRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class AttachService {

    @Autowired
    private AttachRepository attachRepository;
    @Value("${attache.folder}")
    private String attacheFolder;

    @Value("${server.url}")
    private String attachUrl;

    public AttachDTO upload(MultipartFile file) {
        if (file.isEmpty()) {
            throw new AppBadException("File not found");
        }

        try {
            return saveToStorage(
                    file.getBytes(),
                    Objects.requireNonNull(file.getOriginalFilename()),
                    file.getSize()
            );
        } catch (IOException e) {
            throw new AppBadException("Upload went wrong");
        }
    }

    public AttachDTO uploadBytes(byte[] data, String originalFileName) {
        if (data == null || data.length == 0) throw new AppBadException("File is empty");
        return saveToStorage(data, originalFileName, data.length);
    }

    private AttachDTO saveToStorage(byte[] data, String originalFileName, long size) {
        try {
            String pathFolder = getYmDString(); // 2025/11/19
            String key = UUID.randomUUID().toString(); // dasdasd-dasdasda-asdasda-asdasd
            String extension = getExtension(Objects.requireNonNull(originalFileName)); // .jpg, .png, .mp4

            // create folder if not exists
            File folder = new File(attacheFolder + "/" + pathFolder); // attaches/2025/11/19
            if (!folder.exists()) {
                boolean t = folder.mkdirs();
            }

            Path path = Paths.get(attacheFolder + "/" + pathFolder + "/" + key + "." + extension);
            // attaches/2025/11/19/dasdasd-dasdasda-asdasda-asdasd.jpeg
            Files.write(path, data);

            // save to db
            AttachEntity entity = new AttachEntity();
            entity.setId(key + "." + extension); // dasdasd-dasdasda-asdasda-asdasd.jpeg
            entity.setPath(pathFolder);
            entity.setSize(size);
            entity.setOriginalName(originalFileName);
            entity.setExtension(extension);
            entity.setVisible(true);
            attachRepository.save(entity);

            return toDTO(entity);
        } catch (IOException e) {
            throw new AppBadException("Upload went wrong");
        }
    }

    public List<AttachDTO> uploadAll(List<ImageUploadItem> items) {
        List<AttachDTO> dtoList = new ArrayList<>();
        for (ImageUploadItem item : items) {
            AttachDTO dto = uploadBytes(item.getData(), item.getFileName());
            dtoList.add(dto);
        }
        return dtoList;
    }

    public ResponseEntity<Resource> open(String id) {
        try {
            AttachEntity entity = getEntity(id);

            Path filePath = Paths.get(attacheFolder, entity.getPath(), entity.getId()).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);

        } catch (AppBadException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<Resource> download(String filename) {
        AttachEntity entity = getEntity(filename);
        try {
            Path file = Paths.get(attacheFolder + "/" + entity.getPath() + "/" + entity.getId()).normalize();
            Resource resource = new UrlResource(file.toUri());

            if (!resource.exists()) {
                throw new AppBadException("File not found");
            }
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"" + entity.getOriginalName() + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (Exception e) {
            throw new AppBadException("Something went wrong");
        }
    }

    public Boolean delete(String fileId) {
        AttachEntity entity = getEntity(fileId);

        try {
            Path file = Paths.get(attacheFolder + "/" + entity.getPath() + "/" + entity.getId()).normalize();

            boolean deleted = Files.deleteIfExists(file);

            if (!deleted) {

               return false;
            }

            attachRepository.delete(entity);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            throw new AppBadException("Something went wrong");
        }
    }


    private String getYmDString() {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
        int day = Calendar.getInstance().get(Calendar.DATE);
        return year + "/" + month + "/" + day;
    }

    private String getExtension(String fileName) {
        int lastIndex = fileName.lastIndexOf(".");
        return fileName.substring(lastIndex + 1);
    }

    private AttachDTO toDTO(AttachEntity entity) {
        AttachDTO attachDTO = new AttachDTO();
        attachDTO.setId(entity.getId());
        attachDTO.setOriginalName(entity.getOriginalName());
        attachDTO.setSize(entity.getSize());
        attachDTO.setExtension(entity.getExtension());
        attachDTO.setCreatedData(entity.getCreatedDate());
        attachDTO.setPath(openURL(entity.getId()));
        return attachDTO;
    }

    public AttachEntity getEntity(String id) {
        return attachRepository.findById(id).orElseThrow(() -> new AppBadException("File not found"));
    }

    public String openURL(String fileName) {
        return attachUrl + "/api/v1/attach/open/" + fileName;
    }


}
