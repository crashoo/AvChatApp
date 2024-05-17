package com.av.avmessenger;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ContactListsActivity extends AppCompatActivity {

    private SearchView searchView;
    private RelativeLayout newUserDisplay;
    private CardView parentImageView;
    private ImageView profilePicImageView;
    private Button addContactBtn;
    private TextView userName, userMail;

    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private ArrayList<User> searchedUsers = new ArrayList<>();
    private void handleUserSelection(User selectedUser) {
        // Get the UID of the selected user
        String selectedUserId = selectedUser.getUserId();


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_lists);

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        searchView = findViewById(R.id.search_view);
        newUserDisplay = findViewById(R.id.new_user_display);
        parentImageView = findViewById(R.id.parent_image_view);
        profilePicImageView = findViewById(R.id.profile_pic_imageview);
        addContactBtn = findViewById(R.id.add_contact_btn);
        userName = findViewById(R.id.user_name);
        userMail = findViewById(R.id.usermail);

        newUserDisplay.setVisibility(View.GONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                searchedUsers.clear();

                firebaseDatabase.getReference("user").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            User user = dataSnapshot.getValue(User.class);
                            if (user != null && !user.getUserId().equals(firebaseAuth.getCurrentUser().getUid())
                                    && user.getEmail().equals(query.trim())) {
                                searchedUsers.add(user);
                                displaySearchedUser(user);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        searchedUsers.clear();
                    }
                });

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Handle search query text change
                return false;
            }
        });
    }

    private void displaySearchedUser(User user) {
        userName.setText(user.getUserName());
        userMail.setText(user.getEmail());
        Picasso.get().load(user.getProfilepic())
                .fit()
                .centerCrop()
                .error(R.drawable.user)
                .placeholder(R.drawable.user)
                .into(profilePicImageView);
        newUserDisplay.setVisibility(View.VISIBLE);

        addContactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!searchedUsers.isEmpty()) {
                    // Assuming you have the selected user ID
                    User selectedUser = searchedUsers.get(0);

                    // Get the UID of the selected user
                    String selectedUserId = selectedUser.getUserId();

                    // Get the UID of the current logged-in user
                    String currentUserId = firebaseAuth.getCurrentUser().getUid();

                    // Store the contact in the database under the current user's contacts
                    firebaseDatabase.getReference("user_contactsss").child(currentUserId).child(selectedUserId).setValue(true);

                    // Provide feedback to the user
                    Toast.makeText(ContactListsActivity.this, "Contact added successfully", Toast.LENGTH_SHORT).show();

                    // Send the selected user ID back to MainActivity

                }
                else {
                    // Handle the case where there are no searched users
                    Toast.makeText(ContactListsActivity.this, "No users found", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}