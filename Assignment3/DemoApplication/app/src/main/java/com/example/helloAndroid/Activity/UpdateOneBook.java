package com.example.helloAndroid.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.example.helloAndroid.R;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class UpdateOneBook extends AppCompatActivity {
    private Button updateButton;
    private Button cancelUpdateButton;
    private EditText updateTitle;
    private EditText updateYear;
    private EditText updatePrice;
    private EditText updateAuthor;
    private EditText updateImage;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_one_book);

        updateButton = findViewById(R.id.btn_update);
        cancelUpdateButton = findViewById(R.id.btn_cancel_up);

        updateTitle = findViewById(R.id.up_book_title);
        updateYear = findViewById(R.id.up_published_year);
        updatePrice = findViewById(R.id.up_book_price);
        updateAuthor = findViewById(R.id.up_book_authors);
        updateImage = findViewById(R.id.up_book_image);

        updateButton.setOnClickListener(v -> putRequest());
        cancelUpdateButton.setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));
    }

    public void putRequest() {
        MediaType MEDIA_TYPE = MediaType.parse("application/json");
        String url = "http://10.0.2.2:8080/demo/resources/book-store/update-book";

        OkHttpClient client = new OkHttpClient();
        JSONObject putData = new JSONObject();
        JSONObject author = new JSONObject();
        try {
            author.put("authorName",updateAuthor.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONArray authorArray = new JSONArray();
        authorArray.put(author);


        try {
            putData.put("title", updateTitle.getText().toString());
            putData.put("publishedYear", updateYear.getText().toString());
            putData.put("price", Double.parseDouble(updatePrice.getText().toString()));
            putData.put("image", updateImage.getText().toString());
            putData.put("authors", authorArray);
        } catch(JSONException e){
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(MEDIA_TYPE, putData.toString());

        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                UpdateOneBook.this.runOnUiThread(() -> {
                    Intent intent = new Intent(UpdateOneBook.this, MainActivity.class);
                    String message = "Error. Please retry!";
                    intent.putExtra("message",message);
                    startActivity(intent);
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println(putData);
                UpdateOneBook.this.runOnUiThread(() -> {
                    Intent intent = new Intent(UpdateOneBook.this, MainActivity.class);
                    String message = "Book was updated";
                    intent.putExtra("message",message);
                    startActivity(intent);
                });
            }
        });
    }
}
