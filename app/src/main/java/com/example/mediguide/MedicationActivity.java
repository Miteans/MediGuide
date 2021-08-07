package com.example.mediguide;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mediguide.adapters.MedicationAdapter;
import com.example.mediguide.data.MedicineInformation;
import com.example.mediguide.forms.MedAddActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MedicationActivity extends AppCompatActivity {
    DatabaseReference reference;
    ArrayList<MedicineInformation> retrieveMedDetails;
    private RecyclerView recyclerView;
    private MedicationAdapter medicationAdapter;
    ProgressDialog nDialog;
    ImageView imageView;
    private LinearLayout noData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.medication_activity);

        MaterialToolbar toolbar = (MaterialToolbar) findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);

        nDialog = new ProgressDialog(MedicationActivity.this);
        nDialog.setMessage("Loading..");
        nDialog.setTitle("Get Data");
        nDialog.setIndeterminate(false);
        nDialog.setCancelable(true);
        nDialog.show();

        recyclerView = findViewById(R.id.recyclerView);
        noData = findViewById(R.id.noData);

        imageView = findViewById(R.id.image_view);
        try {
            Picasso.with(this)
                    .load(R.drawable.nomedfound)
                    /*.placeholder(R.drawable.placeholder) //optional*/
                    .resize(350, 260)         //optional
                    /*.centerCrop()       */                 //optional
                    .into(imageView);
        }
        catch (Exception e){}

        reference = FirebaseDatabase.getInstance().getReference().child("MedicineInformation");
        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMedAddPageOneActivity();
            }
        });

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        openHomeActivity();
                        return true;
                    case R.id.medication:
                        return true;
                    case R.id.connect:
                        openConnectActivity();
                        return true;
                    case R.id.profile:
                        openProfileActivity();
                        return true;
                }
                return false;
            }
        });

        reference.orderByChild("userId").equalTo(mFirebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = 0;
                if (dataSnapshot.exists()) {
                    retrieveMedDetails = new ArrayList<MedicineInformation>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        MedicineInformation dummy = new MedicineInformation();
                        dummy.setMedicineId(snapshot.child("medicineId").getValue().toString());
                        dummy.setMedicineName(snapshot.child("medicineName").getValue().toString());
                        dummy.setFormOfMedicine(snapshot.child("formOfMedicine").getValue().toString());
                        dummy.setDosage(Integer.parseInt(snapshot.child("dosage").getValue().toString()));
                        dummy.setReasonForIntake(snapshot.child("reasonForIntake").getValue().toString());
                        dummy.setEverydayMed((boolean) snapshot.child("everydayMed").getValue(Boolean.class));

                        if (!dummy.getEverydayMed()) {
                            dummy.setNoMedIntake(Integer.parseInt(snapshot.child("noMedIntake").getValue().toString()));
                        }

                        dummy.setFrequencyOfMedIntake(Integer.parseInt(snapshot.child("frequencyOfMedIntake").getValue().toString()));

                        GenericTypeIndicator<List<String>> genericTypeIndicator = new GenericTypeIndicator<List<String>>() {
                        };
                        dummy.setIntakeTimes(snapshot.child("intakeTimes").getValue(genericTypeIndicator));

                        dummy.setImageUrl(snapshot.child("imageUrl").getValue().toString());
                        dummy.setInstruction(snapshot.child("instruction").getValue().toString());
                        dummy.setDuration(Integer.parseInt(snapshot.child("duration").getValue().toString()));
                        dummy.setRefillCount(Integer.parseInt(snapshot.child("refillCount").getValue().toString()));
                        dummy.setSetStartDate(snapshot.child("setStartDate").getValue().toString());
                        dummy.setOtherInstruction(snapshot.child("otherInstruction").getValue().toString());

                        retrieveMedDetails.add(dummy);
                        setUpMedicineCards();
                    }
                }
                //No medicine detail is found
                else{
                    recyclerView.setVisibility(View.GONE);
                    noData.setVisibility(View.VISIBLE);
                    nDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void openMedAddPageOneActivity(){
        Intent intent = new Intent(this, MedAddActivity.class);
        startActivity(intent);
    }

    public void openHomeActivity(){
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        overridePendingTransition(0,0);
    }

    public void openConnectActivity(){
        Intent intent = new Intent(this, DeviceConnectActivity.class);
        startActivity(intent);
        overridePendingTransition(0,0);
    }

    public void openProfileActivity(){
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
        overridePendingTransition(0,0);
    }

    private void setUpMedicineCards(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setFocusable(false);

        medicationAdapter = new MedicationAdapter(this);
        recyclerView.setAdapter(medicationAdapter);
        medicationAdapter.setDataToMedicationAdapter(retrieveMedDetails);
        recyclerView.setVisibility(View.VISIBLE);
        noData.setVisibility(View.GONE);
        nDialog.dismiss();
    }


}
