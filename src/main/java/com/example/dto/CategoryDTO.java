package com.example.dto;

public class CategoryDTO {
    private String name;
    private String icon;

    public CategoryDTO(String name, String iconUrl) {
        this.name = name;
        this.icon = iconUrl;
    }

    public String getName() { return name; }
    public String getIcon() { return icon; }
}
