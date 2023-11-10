package com.av.avmessenger;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final int CONTACT_LIST_REQUEST_CODE = 123;
    FirebaseAuth auth;
    RecyclerView mainUserRecyclerView;
    UserAdpter adapter;
    FirebaseDatabase database;
    ArrayList<Users> selectedUsersList = new ArrayList<>();  // Updated list for selected users
    ImageView imglogout;
    ImageView cumbut, setbut;

    private void startContactListsActivity() {
        Intent intent = new Intent(MainActivity.this, ContactListsActivity.class);
        startActivityForResult(intent, CONTACT_LIST_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CONTACT_LIST_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null && data.hasExtra("selectedUserId")) {
                String selectedUserId = data.getStringExtra("selectedUserId");

                // Fetch user details and add to the selected users list
                fetchUserDetails(selectedUserId);
            }
        }
    }

    private void fetchUserDetails(String userId) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("user").child(userId);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Users selectedUser = snapshot.getValue(Users.class);

                    if (selectedUser != null) {
                        // Add the selected user to the selected users list
                        selectedUsersList.add(selectedUser);

                        // Update the adapter with the selected users list
                        adapter.setUsersList(selectedUsersList);
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error if fetching data fails
            }
        });
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        cumbut = findViewById(R.id.camBut);
        setbut = findViewById(R.id.settingBut);

        DatabaseReference reference = database.getReference().child("user");

        mainUserRecyclerView = findViewById(R.id.mainUserRecyclerView);
        mainUserRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UserAdpter(MainActivity.this, selectedUsersList);
        mainUserRecyclerView.setAdapter(adapter);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Users users = dataSnapshot.getValue(Users.class);
                    // Assuming you want to display only selected users
                    // Modify the condition based on your criteria
                    if (users != null && isUserSelected(users.getUserId())) {
                        selectedUsersList.add(users);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
            private boolean isUserSelected(String userId) {
                // Implement your logic to determine if the user is selected
                // For example, you can check if the user is in the selectedUsersList
                for (Users selectedUser : selectedUsersList) {
                    if (selectedUser.getUserId().equals(userId)) {
                        return true;
                    }
                }
                return false;
            }
        });


        imglogout = findViewById(R.id.logoutimg);

        imglogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(MainActivity.this, R.style.dialoge);
                dialog.setContentView(R.layout.dialog_layout);
                Button no, yes;
                yes = dialog.findViewById(R.id.yesbnt);
                no = dialog.findViewById(R.id.nobnt);
                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseAuth.getInstance().signOut();
                        Intent inten = new Intent(MainActivity.this, login.class);
                        startActivity(inten);
                        finish();
                    }
                });
                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        setbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startContactListsActivity();
            }
        });

        cumbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 10);
            }
        });

        if (auth.getCurrentUser() == null) {
            Intent intent = new Intent(MainActivity.this, login.class);
            startActivity(intent);
        }
    }
}
