package com.example.helloAndroid.Activity;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.helloAndroid.R;
import com.example.helloAndroid.Result.AuthorResult;
import com.example.helloAndroid.Result.BookResult;
import com.google.android.material.snackbar.Snackbar;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<BookResult> books = new ArrayList<>();
    private Button addBookBtn;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        intent.getExtras();
        if(intent.hasExtra("message")){
            View lvView = findViewById(R.id.lv_book_list);
            Snackbar.make(lvView, getIntent().getStringExtra("message"), Snackbar.LENGTH_LONG)
                    .setAction("CLOSE", view -> {
                    })
                    .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                    .show();
        }

        addBookBtn = findViewById(R.id.addBtn);
        addBookBtn.setOnClickListener(v -> startActivity(new Intent(this, AddEditOne.class)));

        OkHttpClient client = new OkHttpClient();

        String url = "http://10.0.2.2:8080/demo/resources/book-store/all-books";

        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){
                    try {
                        JSONArray array = new JSONArray(response.body().string());
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            List<AuthorResult> authors = new ArrayList<>();
                            JSONArray jsonArray = object.getJSONArray("authors");
                            for(int a = 0; a< jsonArray.length(); a++){
                                authors.add(new AuthorResult(jsonArray.getJSONObject(a).getString("authorName")));
                            }
                            books.add(new BookResult(object.getString("title"),
                                    object.getString("publishedYear"),
                                    object.getDouble("price"),
                                    object.getString("image"),
                                    authors));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    MainActivity.this.runOnUiThread(() -> {
                        recyclerView = findViewById(R.id.lv_book_list);
                        recyclerView.setHasFixedSize(true);
                        layoutManager = new LinearLayoutManager(MainActivity.this);
                        mAdapter = new RecycleViewAdapter(books, MainActivity.this);
                        recyclerView.setAdapter(mAdapter);
                    });
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.sort_menu,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        switch (item.getItemId()){
            case R.id.menu_aToZ:
                Collections.sort(books, BookResult.BookResultComparatorAZ);
                mAdapter.notifyDataSetChanged();
                return true;
            case R.id.menu_zToA:
                Collections.sort(books, BookResult.BookResultComparatorZA);
                mAdapter.notifyDataSetChanged();
                return true;
            case R.id.menu_lowest:
                Collections.sort(books, BookResult.BookResultComparatorLH);
                mAdapter.notifyDataSetChanged();
                return true;
            case R.id.menu_highest:
                Collections.sort(books, BookResult.BookResultComparatorHL);
                mAdapter.notifyDataSetChanged();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}