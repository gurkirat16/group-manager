package com.techeducation.groupmanager;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddUsersActivity extends AppCompatActivity {

    EditText eTxtName,eTxtEmail;
    Button btnInvite;

    void initViews(){
        eTxtName = (EditText)findViewById(R.id.nameInvite);
        eTxtEmail = (EditText)findViewById(R.id.emailInvite);
        btnInvite = (Button)findViewById(R.id.btnInvite);
        btnInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMail();
            }
        });


    }

    void sendMail(){
        if(eTxtName.getText().toString().trim().equals(""))
            eTxtName.setError("Enter valid name");
        else if(eTxtEmail.getText().toString().trim().equals(""))
            eTxtEmail.setError("Enter valid name");
        else if(!isValidEmail(eTxtEmail.getText().toString().trim()))
            eTxtEmail.setError("Invalid Email");
        else{
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("message/rfc822");
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{eTxtEmail.getText().toString().trim()});
            intent.putExtra(Intent.EXTRA_SUBJECT, "Invite");
            intent.putExtra(Intent.EXTRA_TEXT,"I want to invite you to the Group. Download the app and join the group.");
            try{
                startActivity(Intent.createChooser(intent,"Send mail..."));
            }catch(ActivityNotFoundException ex){
                Toast.makeText(AddUsersActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_users);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("Invite User");

        initViews();
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home: {
                finish();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }



}
