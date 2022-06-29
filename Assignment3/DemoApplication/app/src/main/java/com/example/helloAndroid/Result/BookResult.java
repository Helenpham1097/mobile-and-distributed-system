package com.example.helloAndroid.Result;

import java.util.Comparator;
import java.util.List;

public class BookResult {
    private String title;
    private String publishedYear;
    private double price;
    private String image;
    private List<AuthorResult> authors;

    public BookResult(String title, String publishedYear, double price, String image, List<AuthorResult> authors) {
        this.title = title;
        this.publishedYear = publishedYear;
        this.price = price;
        this.authors = authors;
        this.image = image;

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublishedYear() {
        return publishedYear;
    }

    public void setPublishedYear(String publishedYear) {
        this.publishedYear = publishedYear;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public List<AuthorResult> getAuthors() {
        return authors;
    }

    public void setAuthors(List<AuthorResult> authors) {
        this.authors = authors;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAuthorsName(){
        String authorName = "";
        for(AuthorResult a: authors){
            authorName += authors.toString()+ " ";
        }
        return authorName;
    }

    public static Comparator<BookResult> BookResultComparatorAZ = new Comparator<BookResult>() {
        @Override
        public int compare(BookResult b1, BookResult b2) {
            return b1.getTitle().compareTo(b2.getTitle());
        }
    };

    public static Comparator<BookResult> BookResultComparatorZA = new Comparator<BookResult>() {
        @Override
        public int compare(BookResult b1, BookResult b2) {
            return b2.getTitle().compareTo(b1.getTitle());
        }
    };

    public static Comparator<BookResult> BookResultComparatorLH = new Comparator<BookResult>() {
        @Override
        public int compare(BookResult b1, BookResult b2) {
            return Double.compare(b1.getPrice(), b2.getPrice());
        }
    };

    public static Comparator<BookResult> BookResultComparatorHL = new Comparator<BookResult>() {
        @Override
        public int compare(BookResult b1, BookResult b2) {
            return Double.compare(b2.getPrice(), b1.getPrice());
        }
    };

    @Override
    public String toString() {
        String authorsResult = "";
        for(AuthorResult author : authors){
            authorsResult += author.toString() + ", ";
        }
        return "BookResult{" +
                "title='" + title + '\'' +
                ", publishedYear='" + publishedYear + '\'' +
                ", price=" + price +
                ", image='" + image + '\'' +
                ", authors=" + authorsResult +
                '}';
    }
}
