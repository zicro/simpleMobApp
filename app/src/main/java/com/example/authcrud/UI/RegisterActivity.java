package com.example.authcrud.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.authcrud.Controller.SessionManager;
import com.example.authcrud.Controller.VolleySingleton;
import com.example.authcrud.MainActivity;
import com.example.authcrud.Model.User;
import com.example.authcrud.R;
import com.example.authcrud.SERVER.URLs;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText fullname, email, password;
    private Button buttonRegister;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fullname        = findViewById(R.id.registerNameText);
        email           = findViewById(R.id.registerEmailText);
        password        = findViewById(R.id.registerPasswordText);
        buttonRegister  = findViewById(R.id.buttonRegister);
        progressBar        = findViewById(R.id.progressBarRegister);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        final String myName = fullname.getText().toString().trim();
        final String myEmail = email.getText().toString().trim();
        final String myPassword = password.getText().toString().trim();

        if(TextUtils.isEmpty(myName)){
            fullname.setError("Enter the Fullname");
            fullname.requestFocus();
            return;
        }

        if(TextUtils.isEmpty(myEmail)){
            email.setError("Enter the Email");
            email .requestFocus();
            return;
        }

        if(TextUtils.isEmpty(myPassword)){
            password.setError("Enter the Password");
            password.requestFocus();
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                URLs.URL_REGISTER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressBar.setVisibility(View.VISIBLE);

                try {
                    JSONObject obj = new JSONObject(response);

                    if (obj.getBoolean("success")) {
                        progressBar.setVisibility(View.INVISIBLE);

                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                        JSONObject userJson = obj.getJSONObject("data");
                        User user = new User(userJson.getString("token"));

                        SessionManager.getInstance(getApplicationContext()).userLogin(user);

                        finish();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    } else {
                        Toast.makeText(getApplicationContext(), "Register Failed", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        ){
            @Override
            protected Map<String,String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();

                params.put("Content-Type", "application/json");
                params.put("name", myName);
                params.put("email", myEmail);
                params.put("password", myPassword);
                params.put("c_password", myPassword);

                return params;
            }

        };

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }
}