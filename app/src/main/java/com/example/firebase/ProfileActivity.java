package com.example.firebase;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {
    private EditText nameEditText, phoneEditText;
    private ImageView profileImageView;
    private Button editButton, saveButton;
    private FirebaseFirestore firestore;
    private DocumentReference userDocument;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        nameEditText = findViewById(R.id.nameEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        profileImageView = findViewById(R.id.profileImageView);
        editButton = findViewById(R.id.editButton);
        saveButton = findViewById(R.id.saveButton);

        firestore = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userDocument = firestore.collection("users").document(userId);

        // Load user data from Firestore and populate the UI fields
        userDocument.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String name = documentSnapshot.getString("name");
                    String phone = documentSnapshot.getString("phone");

                    nameEditText.setText(name);
                    phoneEditText.setText(phone);
                }
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableEditMode();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfileData();
            }
        });
    }

    private void enableEditMode() {
        // Enable editing of the profile data
        nameEditText.setEnabled(true);
        phoneEditText.setEnabled(true);
        saveButton.setVisibility(View.VISIBLE);
    }

    private void saveProfileData() {
        String name = nameEditText.getText().toString();
        String phone = phoneEditText.getText().toString();

        // Update the user data in Firestore
        userDocument.update("name", name, "phone", phone)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ProfileActivity.this, "Profile saved successfully",
                                Toast.LENGTH_SHORT).show();
                        disableEditMode();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProfileActivity.this, "Failed to save profile",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void disableEditMode() {
        // Disable editing of the profile data
        nameEditText.setEnabled(false);
        phoneEditText.setEnabled(false);
        saveButton.setVisibility(View.GONE);
    }
}

