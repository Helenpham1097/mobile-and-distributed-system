package com.example.helloAndroid.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.helloAndroid.R;
import okhttp3.*;

import java.io.IOException;

public class BookDetailActivity extends AppCompatActivity {
    private String bookTitleToDelete;

    private Button updateBookButton;
    private Button deleteBookButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_detail);
        getIncomingIntent();

        updateBookButton = findViewById(R.id.update_button);
        updateBookButton.setOnClickListener(v -> {
            Intent intent = new Intent(BookDetailActivity.this, UpdateOneBook.class);
            startActivity(intent);
        });
        deleteBookButton = findViewById(R.id.delete_button);
        deleteBookButton.setOnClickListener(v -> deleteBook());
    }

    private void deleteBook(){
        String url = "http://10.0.2.2:8080/demo/resources/book-store/"+bookTitleToDelete;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .delete()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                BookDetailActivity.this.runOnUiThread(() -> {
                    Intent intent = new Intent(BookDetailActivity.this, MainActivity.class);
                    String message = "Error. PLease retry!";
                    intent.putExtra("message",message);
                    startActivity(intent);
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                BookDetailActivity.this.runOnUiThread(() -> {
                    Intent intent = new Intent(BookDetailActivity.this, MainActivity.class);
                    String message = "Book was deleted";
                    intent.putExtra("message",message);
                    startActivity(intent);
                });
            }
        });
    }

    private void getIncomingIntent(){

        if(getIntent().hasExtra("book_cover")
                && getIntent().hasExtra("book_title")
                && getIntent().hasExtra("book_year")
                && getIntent().hasExtra("book_price")
                && getIntent().hasExtra("book_authors")){

            String bookCover = getIntent().getStringExtra("book_cover");
            String bookTitle = getIntent().getStringExtra("book_title");
            bookTitleToDelete = bookTitle;
            String bookYear = getIntent().getStringExtra("book_year");
            String bookPrice = getIntent().getStringExtra("book_price");
            String bookAuthors = getIntent().getStringExtra("book_authors");

            setImage(bookCover, bookTitle, bookYear, bookPrice, bookAuthors);
        }
    }


    private void setImage(String bookCover, String bookTitle, String bookYear, String bookPrice, String bookAuthors){
        TextView title = findViewById(R.id.detail_book_title);
        TextView year = findViewById(R.id.detail_book_year);
        TextView price= findViewById(R.id.detail_book_price);
        TextView authors = findViewById(R.id.detail_book_author);
        ImageView cover = findViewById(R.id.detail_book_cover);

        String uriImage = "@drawable/"+bookCover;
        int imageResource = this.getResources().getIdentifier(uriImage, null, this.getPackageName());

        title.setText(bookTitle);
        year.setText(bookYear);
        price.setText(bookPrice);
        authors.setText(bookAuthors);
        Glide.with(this)
                .asBitmap()
                .load(imageResource)
                .into(cover);
    }
}
