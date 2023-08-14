package com.example.authcrud.Controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.authcrud.Model.Book;
import com.example.authcrud.R;
import com.example.authcrud.UI.EditData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder> {

    private Context context;
    private List<Book> bookList;

    public Adapter(Context context, List<Book> bookList) {
        this.context = context;
        this.bookList = bookList;
    }

    @NonNull
    @Override
    public Adapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_content, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        // get data from the Book Class From Model by using getters
        Book book = bookList.get(position);
        holder.name.setText(book.getName());
        holder.author.setText(book.getAuthor());
        holder.timestamp.setText(formatDate(book.getCreated_at()));

        // the button Click listener for deletion
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteData(position, book.getId());
            }
        });

        // the Edit Button Click listener, with the current data to be updated
        // into the update Form ...
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EditData.class);
                intent.putExtra("id", book.getId());
                intent.putExtra("name", book.getName());
                intent.putExtra("author", book.getAuthor());

                context.startActivity(intent);
            }
        });
    }

    // the size of the list items
    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, author, timestamp;
        public ImageView edit, delete;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.nameTextView2);
            author = itemView.findViewById(R.id.authorTextView3);
            timestamp = itemView.findViewById(R.id.timeTextView);
            edit = itemView.findViewById(R.id.editImageView);
            name = itemView.findViewById(R.id.deleteImageView2);
        }
    }


    // convert data Format from DB to be shown on the App
    private String formatDate(String dateStr){

        try {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = fmt.parse(dateStr);
            SimpleDateFormat fmtOut = new SimpleDateFormat("MM dd");

            return fmtOut.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    // when pressed button Delete Run this function
    public void deleteData(int position, int id){

    }
}
