package com.example.helloAndroid.Activity;

import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.helloAndroid.R;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class AddEditOne extends AppCompatActivity {

    private Button okButton;
    private Button cancelButton;
    private EditText inputTitle;
    private EditText inputYear;
    private EditText inputPrice;
    private EditText inputAuthor;
    private EditText inputImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_one);

        okButton = findViewById(R.id.btn_ok);
        cancelButton = findViewById(R.id.btn_cancel);

        inputTitle = findViewById(R.id.et_book_title);
        inputYear = findViewById(R.id.et_published_year);
        inputPrice = findViewById(R.id.et_book_price);
        inputAuthor = findViewById(R.id.et_book_authors);
        inputImage = findViewById(R.id.et_book_image);

        okButton.setOnClickListener(v -> postRequest());
        cancelButton.setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));
    }

        public void postRequest(){

            MediaType MEDIA_TYPE = MediaType.parse("application/json");
            String url = "http://10.0.2.2:8080/demo/resources/book-store/addNewBook";

            OkHttpClient client = new OkHttpClient();
            JSONObject postData = new JSONObject();
            JSONObject author = new JSONObject();
            try {
                author.put("authorName",inputAuthor.getText().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JSONArray authorArray = new JSONArray();
            authorArray.put(author);


            try {
                postData.put("title", inputTitle.getText().toString());
                postData.put("publishedYear", inputYear.getText().toString());
                postData.put("price", Double.parseDouble(inputPrice.getText().toString()));
                postData.put("image", inputImage.getText().toString());
                postData.put("authors", authorArray);
            } catch(JSONException e){
                e.printStackTrace();
            }

            RequestBody body = RequestBody.create(MEDIA_TYPE, postData.toString());

            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    AddEditOne.this.runOnUiThread(() -> {
                        Intent intent = new Intent(AddEditOne.this, MainActivity.class);
                        String message = "Error. Please retry!";
                        intent.putExtra("message",message);
                        startActivity(intent);
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    System.out.println(postData);
                    AddEditOne.this.runOnUiThread(() -> {
                        Intent intent = new Intent(AddEditOne.this, MainActivity.class);
                        String message = "Book was added";
                        intent.putExtra("message",message);
                        startActivity(intent);
                    });
                }
            });
        }
}