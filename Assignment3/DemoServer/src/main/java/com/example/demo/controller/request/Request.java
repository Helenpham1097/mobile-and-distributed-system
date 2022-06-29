package com.example.demo.controller.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Request {
    private String title;
    private String publishedYear;
    private double price;
    private String image;
    private List<AuthorRequest> authors;
    private String authorName;


}
