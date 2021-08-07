package com.example.mediguide;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mediguide.adapters.HomeAdapter;
import com.example.mediguide.data.MedicineInformation;
import com.example.mediguide.forms.MedAddActivity;
import com.github.jhonnyx2012.horizontalpicker.DatePickerListener;
import com.github.jhonnyx2012.horizontalpicker.HorizontalPicker;
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

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    DatabaseReference reference;
    ArrayList<MedicineInformation> retrieveMedDetails;
    private RecyclerView recyclerView;
    private HomeAdapter homeAdapter;
    private LinearLayout noData, noTodayMed;
    private ImageView imageView,imageView1;
    ProgressDialog nDialog;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        MaterialToolbar toolbar = (MaterialToolbar) findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("MediGuide");

        noTodayMed = findViewById(R.id.noTodayMed);
        noData = findViewById(R.id.noData);
        recyclerView = findViewById(R.id.recyclerView);

        // find the picker
        HorizontalPicker picker = (HorizontalPicker) findViewById(R.id.datePicker);
        nDialog = new ProgressDialog(HomeActivity.this);
        nDialog.setMessage("Loading..");
        nDialog.setTitle("Get Data");
        nDialog.setIndeterminate(false);
        nDialog.setCancelable(true);
        nDialog.show();

        DatePickerListener listener = new DatePickerListener() {
            @Override
            public void onDateSelected(DateTime dateSelected) {
                getDataFromDatabase(dateSelected.toDate());
            }
        };

        // initialize it and attach a listener
        picker
                .setListener(listener)
                .setDays(20)
                .setOffset(10)
                .setDateSelectedColor(Color.DKGRAY)
                .setDateSelectedTextColor(Color.WHITE)
                .setMonthAndYearTextColor(Color.DKGRAY)
                .setTodayButtonTextColor(getColor(R.color.colorPrimary))
                .setTodayDateTextColor(getColor(R.color.colorPrimary))
                .setTodayDateBackgroundColor(Color.GRAY)
                .setUnselectedDayTextColor(Color.DKGRAY)
                .setDayOfWeekTextColor(Color.DKGRAY)
                .setUnselectedDayTextColor(getColor(R.color.primary))
                .showTodayButton(false)
                .init();

        picker.setDate(new DateTime());
        getDataFromDatabase(new Date());

        imageView = findViewById(R.id.image_view);
        try {
            Picasso.with(this)
                    .load(R.drawable.calendar)
                    /*.placeholder(R.drawable.placeholder) //optional*/
                    .resize(350, 260)         //optional
                    /*.centerCrop()       */                 //optional
                    .into(imageView);
        }
        catch (Exception e){}


        imageView1 = findViewById(R.id.image_view1);
        try {
            Picasso.with(this)
                    .load(R.drawable.calendar)
                    /*.placeholder(R.drawable.placeholder) //optional*/
                    .resize(350, 260)         //optional
                    /*.centerCrop()       */                 //optional
                    .into(imageView1);
        }
        catch (Exception e){}


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
                        return true;
                    case R.id.medication:
                        openMedicationActivity();
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
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_top_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.appointment:
                //add the function to perform here
                openAppointmentActivity();
                return true;
            case R.id.refill:
                //add the function to perform here
                openRefillActivity();
                return true;
            case R.id.report:
                //add the function to perform here
                openReportActivity();
                return true;
        }
        return false;
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

    public void openProfileActivity(){
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
        overridePendingTransition(0,0);
    }

    public void openMedAddPageOneActivity(){
        Intent intent = new Intent(this, MedAddActivity.class);
        startActivity(intent);
        overridePendingTransition(0,0);
    }

    public void openAppointmentActivity(){
        Intent intent = new Intent(this, AppointmentActivity.class);
        startActivity(intent);
        overridePendingTransition(0,0);
    }

    public void openReportActivity(){
        Intent intent = new Intent(this, ReportActivity.class);
        startActivity(intent);
        overridePendingTransition(0,0);
    }

    public void openRefillActivity(){
        Intent intent = new Intent(this, RefillActivity.class);
        startActivity(intent);
        overridePendingTransition(0,0);
    }

    private void setUpMedicineCards() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setFocusable(false);

        homeAdapter = new HomeAdapter(this);
        recyclerView.setAdapter(homeAdapter);

        homeAdapter.setDataToMedicationAdapter(retrieveMedDetails);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean checkTheDate(String medDateString, String medDuration, Date currentDate){
        Date medDate = currentDate;
        Date endDate = currentDate;
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        Calendar c = Calendar.getInstance();
        try{
            medDate = format.parse(medDateString);
            c.setTime(medDate);
        }
        catch (Exception e){
        }

        c.add(Calendar.DAY_OF_MONTH, Integer.parseInt(medDuration));

        try{
            endDate = format.parse(format.format(c.getTime()));
        }
        catch (Exception e){}

        return (medDate.compareTo(currentDate) * currentDate.compareTo(endDate) >= 0);
    }

    private boolean checkNoDate(DataSnapshot snapshot, Date currentDate){
        Date medDate = currentDate;
        Date endDate = currentDate;
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        Calendar c = Calendar.getInstance();

        try {
            medDate = format.parse(snapshot.child("setStartDate").getValue().toString());
            c.setTime(medDate);
            c.add(Calendar.DAY_OF_MONTH, Integer.parseInt(snapshot.child("duration").getValue().toString()));
            endDate = format.parse(format.format(c.getTime()));

            c = Calendar.getInstance();
            c.setTime(medDate);

            while ((format.parse(format.format(c.getTime())).after(medDate) ||
                    format.parse(format.format(c.getTime())).equals(medDate))
                    && (format.parse(format.format(c.getTime())).before(endDate) ||
                    format.parse(format.format(c.getTime())).equals(endDate))){

                if(format.parse(format.format(c.getTime())).equals(currentDate)){
                    return true;
                }

                else if(format.parse(format.format(c.getTime())).after(currentDate)){
                    return false;
                }

                c.add(Calendar.DAY_OF_MONTH, Integer.parseInt(snapshot.child("noMedIntake").getValue().toString()));
            }
        }
        catch (Exception e){ }
        return false;
    }

    private void retrieveDataFromDatabase(DataSnapshot snapshot){
        MedicineInformation dummy = new MedicineInformation();
        dummy.setMedicineName(snapshot.child("medicineName").getValue().toString());
        dummy.setDosage(Integer.parseInt(snapshot.child("dosage").getValue().toString()));
        dummy.setFrequencyOfMedIntake(Integer.parseInt(snapshot.child("frequencyOfMedIntake").getValue().toString()));

        GenericTypeIndicator<List<String>> genericTypeIndicator = new GenericTypeIndicator<List<String>>() {};
        dummy.setIntakeTimes(snapshot.child("intakeTimes").getValue(genericTypeIndicator));

        dummy.setImageUrl(snapshot.child("imageUrl").getValue().toString());

        retrieveMedDetails.add(dummy);
    }

    public void getDataFromDatabase(Date currentDate){
        reference = FirebaseDatabase.getInstance().getReference().child("MedicineInformation");
        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser=mFirebaseAuth.getCurrentUser();

        reference.orderByChild("userId").equalTo(mFirebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = 0;
                if(dataSnapshot.exists()){
                    retrieveMedDetails = new ArrayList<MedicineInformation>();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren() ){
                        //Daily Medication
                        if(snapshot.child("modeOfNotification").getValue().toString().equals("Smart Phone")) {
                            if (snapshot.child("everydayMed").getValue(Boolean.class)) {
                                if (checkTheDate(snapshot.child("setStartDate").getValue().toString(), snapshot.child("duration").getValue().toString(), currentDate)) {
                                    retrieveDataFromDatabase(snapshot);
                                }
                            }
                            //Not a daily Medication
                            else {
                                if (checkNoDate(snapshot, currentDate)) {
                                    retrieveDataFromDatabase(snapshot);
                                }
                            }
                        }
                    }
                    //No medication to take today
                    if(retrieveMedDetails.size() == 0){
                        nDialog.dismiss();
                        noTodayMed.setVisibility(View.VISIBLE);
                        noData.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                    }
                    //Today's medicine
                    else{
                        nDialog.dismiss();
                        noTodayMed.setVisibility(View.GONE);
                        noData.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        setUpMedicineCards();
                    }
                }
                //No medicine detail is found
                else{
                    nDialog.dismiss();
                    noData.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    noTodayMed.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
