package com.example.authcrud;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.authcrud.Controller.Adapter;
import com.example.authcrud.Controller.SessionManager;
import com.example.authcrud.Controller.VolleySingleton;
import com.example.authcrud.Model.Book;
import com.example.authcrud.SERVER.URLs;
import com.example.authcrud.UI.AddData;
import com.example.authcrud.UI.LoginActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.authcrud.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    private RecyclerView recyclerView;
    public static Adapter adapter;
    private List<Book> bookList;
    private RequestQueue queue;
    String token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.recycleView);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        // check if the user is logged in, then get the token from the session Manager
        queue = Volley.newRequestQueue(this);
        if (SessionManager.getInstance(this).isLoggedIn()){
            if (SessionManager.getInstance(this).getToken() != null){
                token = SessionManager.getInstance(this).getToken().getToken();
            }
        }else{
            finish();
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }

        // prepare the view and get data from DB
        recyclerView = findViewById(R.id.recycleView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // store the retreived Data into ArrayList
        bookList = new ArrayList<>();
        bookList = getData();

        adapter = new Adapter(this, bookList);
        recyclerView.setAdapter(adapter);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddData.class);
                startActivity(intent);
                finish();
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        // used for the logout button into the 3 dots menu
        if (id == R.id.action_settings) {
            SessionManager.getInstance(this).userLogout();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.recycleView);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public static void notifyAdapter(){
        adapter.notifyDataSetChanged();
    }

    // function to get data from DB
    public List<Book> getData(){

        bookList.clear();
        // show the loader .. when waiting for the DATA from DB
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading ...");
        progressDialog.show();

        // get Data in Json Format
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, URLs.URL_ALL_DATA,null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    JSONArray bookArray = response.getJSONArray("data");

                    // loop into the Json Array data and store each Object using the Book Class
                    for (int i = 0; i < bookArray.length(); i++) {
                        JSONObject bookObj = bookArray.getJSONObject(i);
                        Book book = new Book();
                        book.setId(bookObj.getInt("id"));
                        book.setName(bookObj.getString("name"));
                        book.setAuthor(bookObj.getString("author"));
                        book.setCreated_at(bookObj.getString("created_at"));

                        bookList.add(book);
                        adapter.notifyDataSetChanged();
                        progressDialog.dismiss();
                    }

                } catch (JSONException e) {

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        })
        {
            public Map<String, String> getHeaders(){
                Map<String, String> params = new HashMap<>();
                params.put("Accept","application/json");
                params.put("Authorization","Bearer "+token);

                return params;
            }
        }
                ;
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
        return bookList;
    }
}