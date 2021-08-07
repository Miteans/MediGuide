package com.example.mediguide;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mediguide.data.User;
import com.example.mediguide.forms.AppointmentForm;
import com.example.mediguide.forms.EditProfile;
import com.example.mediguide.forms.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;

public class ProfileActivity extends AppCompatActivity {
    Button edit_profile;
    TextView fullname, email, phone,profileName;
    DatabaseReference reference;

    TextInputEditText old_pass, newpass1, newpass2;
    boolean isOldValid, isNew1Valid, isNew2Valid;

    AlertDialog alert;
    FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        fullname = (TextView) findViewById(R.id.PersonName2);
        email = (TextView) findViewById(R.id.email2);
        phone = (TextView) findViewById(R.id.phone2);
        profileName = (TextView) findViewById(R.id.topText3);

        MaterialToolbar toolbar = (MaterialToolbar) findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });


        reference = FirebaseDatabase.getInstance().getReference().child("User");
        mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();

        reference.orderByChild("userId").equalTo(mFirebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User retrieveProfileDetails = new User();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        retrieveProfileDetails.setName(snapshot.child("name").getValue().toString());
                        retrieveProfileDetails.setEmail(snapshot.child("email").getValue().toString());
                        retrieveProfileDetails.setPhoneNumber(Long.parseLong(snapshot.child("phoneNumber").getValue().toString()));
                        fullname.setText(retrieveProfileDetails.getName());
                        System.out.println(retrieveProfileDetails.getName());
                        email.setText(retrieveProfileDetails.getEmail());
                        phone.setText(String.valueOf(retrieveProfileDetails.getPhoneNumber()));
                        profileName.setText(retrieveProfileDetails.getName());
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

        edit_profile = (Button) findViewById(R.id.edit_profile);
        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String fname = fullname.getText().toString();
                String mail = email.getText().toString();
                String phnum = phone.getText().toString();

                Intent intent = new Intent(ProfileActivity.this, EditProfile.class);
                intent.putExtra("NAME", fname);
                intent.putExtra("EMAIL", mail);
                intent.putExtra("PHONE", phnum);

                startActivity(intent);
            }
        });

        /*BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        openHomeActivity();
                        return true;
                    case R.id.medication:
                        openMedicationActivity();
                        return true;
                    case R.id.connect:
                        openConnectActivity();
                        return true;
                    case R.id.profile:
                        return true;
                }
                return false;
            }
        });*/


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.change_password:
                //add the function to perform here
                changePassword();
                return true;
            case R.id.logout:
                //add the function to perform here
                logout();
                return true;
        }
        return false;
    }

    public void logout(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure?")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mFirebaseAuth.getInstance().signOut();
                        System.out.println(mFirebaseAuth.getCurrentUser());
                        openLoginActivity();
                        finish();
                    }
                })
                .setNegativeButton("Cancel", null);

        alert = builder.create();
        alert.show();
    }

    public void changePassword(){

        View view = LayoutInflater.from(this).inflate(R.layout.change_password_activity, null);

        old_pass = (TextInputEditText) view.findViewById(R.id.oldpass);
        newpass1 = (TextInputEditText) view.findViewById(R.id.newpass);
        newpass2 = (TextInputEditText) view.findViewById(R.id.confirmpass);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);

        builder.setMessage("Change password")
                .setView(view)
                .setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Dismiss dialog and close activity if appropriated, do not use this (cancel) button at all.
                dialog.dismiss();
            }
        });

        alert = builder.create();
        alert.show();

        alert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validatePassword()) {
                    //Reset password
                    reset(old_pass.getText().toString(), newpass1.getText().toString(), newpass2.getText().toString());
                    alert.dismiss();
                }
            }
        });

    }

    public void openLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void reset(String old_pass, String newpass1, String newpass2) {
        reference = FirebaseDatabase.getInstance().getReference().child("User");
        FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        //Re authenticate user
        AuthCredential credential = EmailAuthProvider.getCredential(mFirebaseUser.getEmail(), old_pass);
        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                user.updatePassword(newpass2).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(ProfileActivity.this, "Password Updated Successfully", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
            }
        });

    }

    public boolean validatePassword(){
        if (old_pass.getText().toString().isEmpty()) {
            Toast.makeText(ProfileActivity.this, "Old password is required", Toast.LENGTH_LONG).show();
            isOldValid = false;
        } else {
            isOldValid = true;
        }

        // Check for a new valid password.
        if (newpass1.getText().toString().isEmpty()) {
            Toast.makeText(ProfileActivity.this, "New password is required", Toast.LENGTH_LONG).show();
            isNew1Valid = false;
        } else if (newpass1.getText().length() < 6) {
            Toast.makeText(ProfileActivity.this, "Please enter a password of minimum 6 characters", Toast.LENGTH_LONG).show();
            isNew1Valid = false;
        } else {
            isNew1Valid = true;
        }

        // Check for a confirmation of password.
        if (newpass2.getText().toString().isEmpty()) {
            Toast.makeText(ProfileActivity.this, "Confirm password is required", Toast.LENGTH_LONG).show();
            isNew2Valid = false;
        } else if (newpass2.getText().toString().equals(newpass1.getText().toString())) {
            isNew2Valid = true;
        } else {
            Toast.makeText(ProfileActivity.this, "Please enter new password correctly", Toast.LENGTH_LONG).show();
            isNew2Valid = false;
        }

        if (isOldValid && isNew1Valid && isNew2Valid) {
            return true;

        } else {
           return false;
        }
    }

    public void openMedicationActivity(){
        Intent intent = new Intent(this, MedicationActivity.class);
        startActivity(intent);
        overridePendingTransition(0,0);
    }

    public void openConnectActivity(){
        Intent intent = new Intent(this, DeviceConnectActivity.class);
        startActivity(intent);
        overridePendingTransition(0,0);
    }

    public void openHomeActivity(){
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        overridePendingTransition(0,0);
    }

}
