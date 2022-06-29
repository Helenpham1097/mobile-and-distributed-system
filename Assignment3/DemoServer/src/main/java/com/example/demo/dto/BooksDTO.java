package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class BooksDTO {
    private String title;
    private String publishedYear;
    private double price;
    private String image;
    private List<AuthorsDTO> authors;

    public BooksDTO(String title, String publishedYear, double price, String image){
        this.title = title;
        this.publishedYear = publishedYear;
        this.price = price;
    }
}
