package com.example.twistermandatoryapp;

import android.content.Intent;
import android.os.Bundle;

import com.example.twistermandatoryapp.REST.ApiUtils;
import com.example.twistermandatoryapp.REST.Message;
import com.example.twistermandatoryapp.REST.RestService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateActivity extends AppCompatActivity {

    private TextView messageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        messageView = findViewById(R.id.createpostMessageView);


    }


    public void CreatePost(View view)
    {
        EditText postField = findViewById(R.id.createactivityPostText);
        String postString = postField.getText().toString();

        RestService restService = ApiUtils.getRestService();

        Message post = new Message(postString, GetCurrentUser().getEmail());

        Call<Message> saveMessageCall = restService.saveMessageBody(post);
        saveMessageCall.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                if (response.isSuccessful()) {
                    Message theNewPost = response.body();
                    String message = "Post Created";
                    Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
                    Intent mainPageIntent = new Intent(CreateActivity.this, MainActivity.class);
                    startActivity(mainPageIntent);

                } else {
                    String problem = "Problem: " + response.code() + " " + response.message();
                    messageView.setText(problem);
                    //Toast.makeText(AddBookActivity.this, problem, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                messageView.setText(t.getMessage());

            }
        });
    }

    public void OpenTerms(View view)
    {
//        Intent termsIntent = new Intent(this, TermsConditionsForm.class);
//        startActivity(termsIntent);
    }

    public FirebaseUser GetCurrentUser()
    {
        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        currentUser.toString();
        Log.d("Bruger", "Current User:" + currentUser);
        return currentUser;

    }
}