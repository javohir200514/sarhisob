package com.example.dto;

import lombok.Data;

@Data
public class ImageUploadItem {

    private String tileId;    // preview tile ni topib o'chirish uchun (UI ichki ishlatish)
    private String fileName;  // original fayl nomi, masalan: "photo.jpg"
    private byte[] data;
}