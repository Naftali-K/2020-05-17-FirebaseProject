package com.hackeru.firebaseproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private final String LOGIN_TRYOUTS_KEY = "login_tryouts";
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    private final DatabaseReference loginTryoutRef = db.getReference().child(LOGIN_TRYOUTS_KEY);
    private final DatabaseReference loginLastPasswordRef = db.getReference().child("login_last_password");
    private final DatabaseReference statusRef = db.getReference().child("status");

    EditText email, password, status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        email = findViewById(R.id.emailEt);
        password = findViewById(R.id.passwordEt);
        status = findViewById(R.id.statusEt);


        findViewById(R.id.loginBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailText = email.getText().toString();
                String passwordText = password.getText().toString();
                if (emailText.isEmpty() || passwordText.isEmpty()) return;
//                loginTryoutRef.push().setValue(new LoginObject(emailText,
//                        password.getText().toString()));
//                loginLastPasswordRef.child(emailText).setValue(
//                        new LoginObject(emailText, password.getText().toString()));
//                statusRef.setValue(status.getText().toString());
                mAuth.createUserWithEmailAndPassword(emailText, passwordText).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            String childRef = "Undefined";
                            if (status.getText().toString().toLowerCase().equals("Teacher".toLowerCase())){
                                childRef = "Teacher";//status.getText().toString();
                            }
                            db.getReference().child(childRef).push().setValue(mAuth.getUid());
                            Intent intent = new Intent(LoginActivity.this, MessagesActivity.class);
                            startActivity(intent);
                            finish();
                        }else{
                            String msg = task.getException().getMessage();
                            Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            }
        });


        //listen to a single value change
        statusRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Toast.makeText(getBaseContext(), dataSnapshot.getValue(String.class), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        //listen to all changes under this reference
        loginLastPasswordRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                LoginObject login = dataSnapshot.getValue(LoginObject.class);
                loginLastPasswordRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Toast.makeText(getBaseContext(), dataSnapshot.getChildrenCount()+"", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                LoginObject login = dataSnapshot.getValue(LoginObject.class);
                ((TextView)findViewById(R.id.displayTv)).setText(login.getPassword());
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //----------------------------------------------------------------------------------------------
    //testing if user already registered, if yes so move streetly to messenger page
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if( user != null) {
            //extract users information
            Intent intent = new Intent(LoginActivity.this, MessagesActivity.class);
            startActivity(intent);
            finish();
        } else {

        }
    }
}
