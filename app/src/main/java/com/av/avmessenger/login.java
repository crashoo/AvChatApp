package com.av.avmessenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class login extends AppCompatActivity {
    TextView logsignup;
    Button button;
    EditText email, password;
    FirebaseAuth auth;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    ProgressDialog progressDialog; // Use android.app.ProgressDialog instead of import android.app.ProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
        getSupportActionBar().hide();
        auth = FirebaseAuth.getInstance();
        button = findViewById(R.id.logbutton);
        email = findViewById(R.id.editTexLogEmail);
        password = findViewById(R.id.editTextLogPassword);
        logsignup = findViewById(R.id.logsignup);

        logsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(login.this, registration.class);
                startActivity(intent);
                finish();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email = email.getText().toString().trim(); // Trim the email to remove leading and trailing whitespaces
                String pass = password.getText().toString().trim(); // Trim the password

                if (TextUtils.isEmpty(Email)) {
                    Toast.makeText(login.this, "Enter The Email", Toast.LENGTH_SHORT).show();
                    return; // Return to avoid executing the rest of the code
                } else if (TextUtils.isEmpty(pass)) {
                    Toast.makeText(login.this, "Enter The Password", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!Email.matches(emailPattern)) {
                    email.setError("Give Proper Email Address");
                    return;
                } else if (pass.length() < 6) {
                    password.setError("More Than Six Characters");
                    Toast.makeText(login.this, "Password Needs To Be Longer Than Six Characters", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressDialog.show(); // Move progressDialog.show() here to show it before the authentication starts

                auth.signInWithEmailAndPassword(Email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss(); // Dismiss the progressDialog after the task completes

                        if (task.isSuccessful()) {
                            try {
                                Intent intent = new Intent(login.this, MainActivityMessage.class);
                                startActivity(intent);
                                finish();
                            } catch (Exception e) {
                                Toast.makeText(login.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(login.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
