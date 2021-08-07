package com.example.mediguide;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mediguide.adapters.RefillAdapter;
import com.example.mediguide.data.MedicineInformation;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class RefillActivity extends AppCompatActivity {
    MaterialToolbar mToolbar;
    RecyclerView recyclerView;
    ArrayList<String> arrayList = new ArrayList<>();
    DatabaseReference reference;
    ArrayList<MedicineInformation> retrieveMedDetails;
    RefillAdapter adapter;
    LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.refill_activity);

        mToolbar = (MaterialToolbar) findViewById(R.id.topAppBar1);
        mToolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RefillActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        reference = FirebaseDatabase.getInstance().getReference().child("MedicineInformation");
        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
        recyclerView = findViewById(R.id.recyclerView);

        reference.orderByChild("userId").equalTo(mFirebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = 0;
                if (dataSnapshot.exists()) {
                    retrieveMedDetails = new ArrayList<MedicineInformation>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        int duration = Integer.parseInt(snapshot.child("duration").getValue().toString());
                        String startDate = snapshot.child("setStartDate").getValue().toString();
                        System.out.println("Hello------------------------------------------------");

                        if(checkActiveMed(duration, startDate)){
                            getData(snapshot);

                            System.out.println("Hello11111111111111------------------------------------------------");
                        }

                        setUpMedicineCards();
                    }
                }

                //No medicine detail is found
                else{
                    //  recyclerView.setVisibility(View.GONE);
                    //noData.setVisibility(View.VISIBLE);
                    //nDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


    }


    private boolean checkActiveMed(int duration, String startDateString){
        Date currentDate = new Date();
        Date medDate = null;
        Date endDate = null;
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        Calendar c = Calendar.getInstance();
        try{
            medDate = format.parse(startDateString);
            c.setTime(medDate);
        }
        catch (Exception e){
        }

        c.add(Calendar.DAY_OF_MONTH, duration);

        try{
            endDate = format.parse(format.format(c.getTime()));
        }
        catch (Exception e){}

        return (medDate.compareTo(currentDate) * currentDate.compareTo(endDate) >= 0);

    }
    private void setUpMedicineCards(){
        //Assign variable
        System.out.println("setUpMedicineCards---------------------");
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setFocusable(false);

        //   recyclerView.setFocusable(false);
        adapter = new RefillAdapter(this);
        recyclerView.setAdapter(adapter);
        adapter.setDataToRefillAdapter(retrieveMedDetails);
        recyclerView.setVisibility(View.VISIBLE);
        /*noData.setVisibility(View.GONE);
        nDialog.dismiss();*/
    }

    private void getData(DataSnapshot snapshot){
        int med_count;
        MedicineInformation dummy = new MedicineInformation();
        dummy.setMedicineId(snapshot.child("medicineId").getValue().toString());
        dummy.setMedicineName(snapshot.child("medicineName").getValue().toString());
        System.out.println(snapshot.child("medicineName").getValue().toString());
        dummy.setRefillCount(Integer.parseInt(snapshot.child("refillCount").getValue().toString()));
        med_count=Integer.parseInt(snapshot.child("refillCount").getValue().toString());
        System.out.println(med_count);

//        if(med_count<=6){
//                changecolor();
//        }
//        dummy.setEverydayMed((boolean) snapshot.child("everydayMed").getValue(Boolean.class));
//        if (!dummy.getEverydayMed()) {
//            dummy.setNoMedIntake(Integer.parseInt(snapshot.child("noMedIntake").getValue().toString()));
//        }
//
//
//        GenericTypeIndicator<List<String>> genericTypeIndicator = new GenericTypeIndicator<List<String>>() {
//        };


        retrieveMedDetails.add(dummy);
    }


    public void  openRefillActivity(){
        Intent intent = new Intent(this, RefillActivity.class);
        startActivity(intent);
        overridePendingTransition(0,0);
    }

//    private void changecolor() {
//        layout = (LinearLayout) findViewById(R.id.layout1);
//        layout.setBackground(@ColorInt);
//    }
}