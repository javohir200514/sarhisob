package com.example.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @GetMapping
    public List<Map<String, Object>> getBooks() {
        return List.of(
                Map.of(
                        "id", 1,
                        "title", "O'tkan kunlar",
                        "author", "Abdulla Qodiriy"
                ),
                Map.of(
                        "id", 2,
                        "title", "Mehrobdan chayon",
                        "author", "Abdulla Qodiriy"
                ),
                Map.of(
                        "id", 3,
                        "title", "Atomic Habits",
                        "author", "James Clear"
                ),
                Map.of(
                        "id", 4,
                        "title", "1984",
                        "author", "George Orwell"
                )
        );
    }
}