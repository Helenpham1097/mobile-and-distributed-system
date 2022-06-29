package com.example.helloAndroid.Activity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.helloAndroid.R;
import com.example.helloAndroid.Result.BookResult;

import java.util.List;

public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.MyViewHolder> {

    private List<BookResult> books;
    private Context context;

    public RecycleViewAdapter(List<BookResult> books, Context context) {
        this.books = books;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_line_book, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String imageName = books.get(position).getImage();
        String uriImage = "@drawable/"+imageName;
        int imageResource = this.context.getResources().getIdentifier(uriImage, null, this.context.getPackageName());
        Glide.with(this.context).load(imageResource).into(holder.bookCover);

        holder.bookTitle.setText(books.get(position).getTitle());
        holder.bookPrice.setText(String.valueOf(books.get(position).getPrice()));
        holder.bookAuthors.setText(books.get(position).getAuthorsName());
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, BookDetailActivity.class);
            intent.putExtra("book_cover",books.get(position).getImage());
            intent.putExtra("book_title", books.get(position).getTitle());
            intent.putExtra("book_year", books.get(position).getPublishedYear());
            intent.putExtra("book_price", String.valueOf(books.get(position).getPrice()));
            intent.putExtra("book_authors", books.get(position).getAuthors().toString());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView bookCover;
        private TextView bookTitle;
        private TextView bookPrice;
        private TextView bookAuthors;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            bookCover = itemView.findViewById(R.id.iv_book_cover);
            bookTitle = itemView.findViewById(R.id.tv_book_title);
            bookPrice = itemView.findViewById(R.id.tv_book_price);
            bookAuthors = itemView.findViewById(R.id.tv_authors);
        }
    }
}
