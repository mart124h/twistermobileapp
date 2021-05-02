package com.example.twistermandatoryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static kotlin.jvm.internal.Reflection.function;


public class StartupActivity extends AppCompatActivity {


    private LinearLayout welcomeView;
    private LinearLayout registerView;
    private LinearLayout regConfirmedView;
    private LinearLayout loginForm;
    private Intent loginIntent;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
        registerView = findViewById(R.id.startupRegisterView);
        regConfirmedView = findViewById(R.id.startupRegCompletedForm);
        loginForm = findViewById(R.id.startupLoginView);
        welcomeView = findViewById(R.id.startupWelcomeView);


        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Log.d("Bruger", "Current User:" + currentUser);
        loginIntent = new Intent(this, MainActivity.class);




        if(currentUser!=null)
        {
            startActivity(loginIntent);
        }

        else {
            //""
        }

    }



    public void login (View view)
    {
        EditText emailView = findViewById(R.id.startupEmailView);
        String email = emailView.getText().toString();

        EditText passwordView = findViewById(R.id.startupPasswordView);
        String password = emailView.getText().toString();

        //TextView errorMessage = findViewById(R.id.)

        if("".equals(email))
        {
            emailView.setError("Please Enter Your Email Address");
            return;
        }

        if("".equals(password))
        {
            passwordView.setError("Please Enter Your Password");
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Bruger", "createUserWithEmail:success");
                            String user = mAuth.getCurrentUser().getEmail();

                            loginIntent.putExtra(MainActivity.greetString, "" + user);

                            startActivity(loginIntent);

                        }

                        else {

                            emailView.setText("False");

                        }
                    }
                });
    }



    public void Register (View view)
    {
        EditText emailRegView = findViewById(R.id.startupRegisterEmailView);
        String email = emailRegView.getText().toString();

        EditText passwordView = findViewById(R.id.startupRegisterPasswordView);
        String password = emailRegView.getText().toString();

        if("".equals(email))
        {
            emailRegView.setError("No Email Address Entered");
            return;
        }

        if("".equals(password))
        {
            passwordView.setError("Please Enter a Password");
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Bruger", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            String user2 = mAuth.getCurrentUser().getEmail();

                            loginIntent.putExtra(MainActivity.greetString, "" + user2);

                            OpenRegCompletedForm(view);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Bruger", "createUserWithEmail:failure", task.getException());

                            emailRegView.setError(task.getException().getMessage());
                            passwordView.setError("11");
                        }
                    }
                });
    }

    public void OpenTerms(View view)
    {
//        Intent termsIntent = new Intent(this, TermsConditionsForm.class);
//        startActivity(termsIntent);
    }

    public void OpenLoginForm(View view)
    {
        registerView.setVisibility(View.GONE);
        regConfirmedView.setVisibility(View.GONE);
        welcomeView.setVisibility(View.GONE);
        loginForm.setVisibility(View.VISIBLE);
    }

    public void OpenRegisterForm(View view)
    {
        registerView.setVisibility(View.VISIBLE);
        welcomeView.setVisibility(View.GONE);
        loginForm.setVisibility(View.GONE);
        regConfirmedView.setVisibility(View.GONE);


    }

    public void OpenRegCompletedForm(View view)
    {
        registerView.setVisibility(View.GONE);
        welcomeView.setVisibility(View.GONE);
        loginForm.setVisibility(View.GONE);
        regConfirmedView.setVisibility(View.VISIBLE);

    }

    public void OpenMainActivity(View view)
    {
        startActivity(loginIntent);
    }

    public void logout(View view)
    {
        mAuth.signOut();
    }

}