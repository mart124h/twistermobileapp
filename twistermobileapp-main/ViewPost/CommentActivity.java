package com.example.twistermandatoryapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.twistermandatoryapp.REST.ApiUtils;
import com.example.twistermandatoryapp.REST.Comment;
import com.example.twistermandatoryapp.REST.Message;
import com.example.twistermandatoryapp.REST.RecyclerViewSimpleAdapter;
import com.example.twistermandatoryapp.REST.RecyclerViewSimpleCommentsAdapter;
import com.example.twistermandatoryapp.REST.RestService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentActivity extends AppCompatActivity {
    public static final String MESSAGEID = "529";
    public static final String MESSAGECONTENT = "fefewag";
    public static final String USERLABEL = "DRE";
    public String currentUser = GetCurrentUser().getEmail().trim();


    private static final String LOG_TAG = "Posts";
    public int messageID;

    public Message thePost;
    public TextView userLabelView;
    public TextView messageView;
    public TextView messageContent;
    public TextView messageIDText;


    //public ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        userLabelView = findViewById(R.id.list_item_user);

        messageView = findViewById(R.id.commentMessageView);
        messageIDText = findViewById(R.id.commentSelected_message_label);
        messageContent = findViewById(R.id.list_item_content);

        Intent intent = getIntent();
        userLabelView.setText("\uD83D\uDCCC " + intent.getSerializableExtra(USERLABEL).toString());

        Intent intent1 = getIntent();
        messageContent.setText(intent1.getSerializableExtra(MESSAGECONTENT).toString());

        Intent intent2 = getIntent();
        messageIDText.setText(intent2.getSerializableExtra(MESSAGEID).toString());


        Log.d(LOG_TAG, "putExtra: " + MESSAGEID + " MESSAGEID");
        messageID = Integer.parseInt(messageIDText.getText().toString());
        messageIDText.setText("Twist #" + messageID);
        Log.d(LOG_TAG, "putExtra  " + messageID + "messageID");
    }

    @Override
    protected void onStart()
    {
        super.onStart();



        getAndShowAllComments();


    }

    public void CreateComment(View view)
    {
        EditText commentField = findViewById(R.id.messageCommentEditField);
        String commentString = commentField.getText().toString();


        RestService restService = ApiUtils.getRestService();

        Comment newComment = new Comment(GetCurrentUser().getEmail(), commentString, messageID);

        Call<Comment> commentPost = restService.postComment(messageID, newComment);
        commentPost.enqueue(new Callback<Comment>() {
            @Override
            public void onResponse(Call<Comment> call, Response<Comment> response) {
                if (response.isSuccessful()) {
                    Comment theNewComment = response.body();
                    Log.d(LOG_TAG, theNewComment.toString());
                    getAndShowAllComments();

                } else {
                    String problem = "Problem: " + response.code() + " " + response.message();
                    Log.e(LOG_TAG, problem);
                    messageView.setText(problem);
                    //Toast.makeText(AddBookActivity.this, problem, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Comment> call, Throwable t) {
                messageView.setText(t.getMessage());

            }
        });
    }


    public void getAndShowAllComments()
    {

        RestService restServ = ApiUtils.getRestService();
        //Call<List<Message>> getAllMessagesCall = restServ.getAllMessages();
        Call<List<Comment>> getComments = restServ.getCommentsByMessageId(messageID);
        messageView.setText("");
        //progressBar.setVisibility(View.VISIBLE);

        getComments.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                //ResponseBody body = response.raw();
                Log.d(LOG_TAG, response.raw().toString());
                /*try {
                    Thread.sleep(5000);
                    // sleep a little to get a chance to see the progressbar in action
                    // don't do this at home
                } catch (InterruptedException e) {
                }*/
                //progressBar.setVisibility(View.INVISIBLE);
                if (response.isSuccessful()) {
                    List<Comment> allComments = response.body();
                    Log.d(LOG_TAG, allComments.toString());
                    populateRecyclerView(allComments);

                } else {
                    String message = "Problem " + response.code() + " " + response.message();
                    Log.d(LOG_TAG, message);
                    messageView.setText(message);
                }
            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {
                //progressBar.setVisibility(View.INVISIBLE);
                Log.e(LOG_TAG, t.getMessage());
                messageView.setText(t.getMessage());
            }
        });
    }

    private void populateRecyclerView(List<Comment> allComments) {
        RecyclerView recyclerView = findViewById(R.id.messageCommentRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        RecyclerViewSimpleCommentsAdapter adapter = new RecyclerViewSimpleCommentsAdapter(allComments);
        //RecyclerViewSimpleAdapter<Message> adapter = new RecyclerViewSimpleAdapter<>(allMessages);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener((view, position, item) -> {
            Comment comment = (Comment) item;
            item.setUser(item.getUser());
            int itemId = item.getId();
            Log.d(LOG_TAG, item.toString());
            Log.d(LOG_TAG, "putExtra " + comment.toString());
            Log.d(LOG_TAG, "currentuser");
            Log.d(LOG_TAG, "commentID: " + itemId);


            if(item.getUser().equals(GetCurrentUser().getEmail()))
            {
                Log.d(LOG_TAG, "commentUser = true " + comment.getUser() + " | " + currentUser);
                view.setVisibility(View.VISIBLE);

                RestService restService = ApiUtils.getRestService();
                Call<Comment> deleteCommentCall = restService.deleteComment(item.getMessageId(), itemId);
                messageView.setText("");

                deleteCommentCall.enqueue(new Callback<Comment>() {
                    @Override
                    public void onResponse(Call<Comment> call, Response<Comment> response) {
                        if (response.isSuccessful()) {
                            //Snackbar.make(view, "Book deleted, id: " + originalBook.getId(), Snackbar.LENGTH_LONG).show();
                            String message = "Comment Deleted";
                            Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
                            getAndShowAllComments();
                        } else {
                            //Snackbar.make(view, "Problem: " + response.code() + " " + response.message(), Snackbar.LENGTH_LONG).show();
                            String problem = call.request().url() + "\n" + response.code() + " " + response.message();
                            messageView.setText(problem);
                            //Toast.makeText(getBaseContext(), problem, Toast.LENGTH_SHORT).show();
                            Log.e(LOG_TAG, problem + ", " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<Comment> call, Throwable t) {
                        //Snackbar.make(view, "Problem: " + t.getMessage(), Snackbar.LENGTH_LONG).show();
                        Log.e(LOG_TAG, "Problem: " + t.getMessage());
                    }
                });
            }

            else
            {
                Log.d(LOG_TAG, "commentUser = false " + item.getUser() + " | " + currentUser);
                view.setAlpha(0.5f);
                String message = "You can only delete your own comments";
                Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
            }
//
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
        if (id == R.id.viewProfileToolbar) {
            return true;
        }

        if (id == R.id.signOffToolbar) {

        }


        return super.onOptionsItemSelected(item);
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


    public void DeletePost(View view)
    {

    }

}