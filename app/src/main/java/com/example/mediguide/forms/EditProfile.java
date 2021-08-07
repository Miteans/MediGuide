package com.example.mediguide.forms;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mediguide.ProfileActivity;
import com.example.mediguide.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditProfile extends AppCompatActivity {
    ImageView cancel;
    DatabaseReference reference;
    Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);


        //Close Button
        cancel =  findViewById(R.id.cancel_button1);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditProfile.this, ProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Intent intent = getIntent();
        String name= intent.getStringExtra("NAME");
        String mail= intent.getStringExtra("EMAIL");
        String phnum= intent.getStringExtra("PHONE");

        TextInputEditText fullname= findViewById(R.id.fname);
        TextInputEditText email = findViewById(R.id.email);
        TextInputEditText phone = findViewById(R.id.phone);

        fullname.setText(name);
        email.setText(mail);
        phone.setText(phnum);


        saveButton = findViewById(R.id.btnSave);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData(fullname.getText().toString(), phone.getText().toString());
            }
        });


    }

    private void saveData(String name,String phoneNumber){
        reference = FirebaseDatabase.getInstance().getReference().child("User");
        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser=mFirebaseAuth.getCurrentUser();

        reference.orderByChild("userId").equalTo(mFirebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                        reference.child(snapshot.getKey()).child("name").setValue(name);
                        reference.child(snapshot.getKey()).child("phoneNumber").setValue(phoneNumber);
                        Toast.makeText(EditProfile.this, "Profile Updated Successfully", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(EditProfile.this, ProfileActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(EditProfile.this, "Profile update is not possible please try again....", Toast.LENGTH_LONG).show();
            }
        });

    }
}


