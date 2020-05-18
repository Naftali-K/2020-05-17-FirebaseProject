package com.hackeru.firebaseproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class MessagesActivity extends AppCompatActivity {


    FirebaseDatabase db = FirebaseDatabase.getInstance();
    FirebaseAuth mAUth = FirebaseAuth.getInstance();


    RecyclerView msgList;
    EditText msgEt;
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        msgList = findViewById(R.id.msgList);
        msgEt = findViewById(R.id.messageEt);
        btn = findViewById(R.id.sendBtn);


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.getReference().child("messages").child(mAUth.getUid()).push().setValue(msgEt.getText().toString());
            }
        });
    }


    //override the activity back press and implement the logout
    @Override
    public void onBackPressed() {
        mAUth.signOut();
        Intent intent = new Intent(MessagesActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
