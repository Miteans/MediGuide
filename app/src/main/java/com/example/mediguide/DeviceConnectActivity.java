package com.example.mediguide;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mediguide.adapters.DeviceConnectAdapter;
import com.example.mediguide.data.MedicineInformation;
import com.example.mediguide.device_interface.DeviceActivity;
import com.example.mediguide.forms.MedAddActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DeviceConnectActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<String> arrayList = new ArrayList<>();
    DatabaseReference reference;
    Toolbar toolbar;
    ArrayList<MedicineInformation> retrieveMedDetails;
    ArrayList<MedicineInformation> selectedActiveMeds = new ArrayList<MedicineInformation>();
    private TextView txtToolbar;
    private ImageButton btnBack;
    private int counter = 0;
    public boolean isActionMode = false;
    DeviceConnectAdapter adapter;
    public int position = -1;
    AlertDialog alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.device_connect_activity);
        super.onCreate(savedInstanceState);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        txtToolbar = findViewById(R.id.textToolbar);
        txtToolbar.setVisibility(View.VISIBLE);
        btnBack = findViewById(R.id.backButton);
        btnBack.setVisibility(View.GONE);

        btnBack.setOnClickListener(v -> {
            clearActionMode();
            System.out.println(retrieveMedDetails);
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
                        if(snapshot.child("modeOfNotification").getValue().toString().equals("Smart Watch") && checkActiveMed(duration, startDate))
                            getData(snapshot);

                        setUpMedicineCards();
                    }
                }

                //No medicine detail is found
                else{
                    recyclerView.setVisibility(View.GONE);
                    //noData.setVisibility(View.VISIBLE);
                    //nDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
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
                        openMedicationActivity();
                        return true;
                    case R.id.connect:
                        return true;
                    case R.id.profile:
                        openProfileActivity();
                        return true;
                }
                return false;
            }
        });
    }

    public void openHomeActivity(){
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        overridePendingTransition(0,0);
    }

    public void openMedicationActivity(){
        Intent intent = new Intent(this, MedicationActivity.class);
        startActivity(intent);
        overridePendingTransition(0,0);
    }

    public void openProfileActivity() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    private void setUpMedicineCards(){
        //Assign variable

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setFocusable(false);
        adapter = new DeviceConnectAdapter(this);
        recyclerView.setAdapter(adapter);
        adapter.setDataToDeviceConnectAdapter(retrieveMedDetails);
        recyclerView.setVisibility(View.VISIBLE);
        /*noData.setVisibility(View.GONE);
        nDialog.dismiss();*/
    }

    private void getData(DataSnapshot snapshot){
        MedicineInformation dummy = new MedicineInformation();
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
        dummy.setOtherInstruction(snapshot.child("otherInstruction").getValue().toString());

        retrieveMedDetails.add(dummy);
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


    public void startSelection(int index) {

        if(!isActionMode){
            isActionMode = true;
            selectedActiveMeds.add(retrieveMedDetails.get(index));
            counter++;
            updateToolbarText(counter);
            txtToolbar.setVisibility(View.VISIBLE);
            btnBack.setVisibility(View.VISIBLE);
            toolbar.inflateMenu(R.menu.device_menu_bar);
            position = index;
            adapter.notifyDataSetChanged();
        }
    }

    public void check(View v, int index){
        if(((CheckBox)v).isChecked()){
            System.out.println(index);
            selectedActiveMeds.add(retrieveMedDetails.get(index));
            counter++;
            updateToolbarText(counter);
        }
        else{
            selectedActiveMeds.remove(retrieveMedDetails.get(index));
            counter--;
            updateToolbarText(counter);
        }
    }

    private void updateToolbarText(int counter){
        if(counter == 0){
            txtToolbar.setText("0 items selected");
        }

        else if(counter == 1){
            txtToolbar.setText("1 item selected");
        }

        else{
            txtToolbar.setText(counter + " items selected");
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){

        if(item.getItemId() == R.id.menu_share && selectedActiveMeds.size() > 0){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setCancelable(false);

            builder.setMessage("Share " + selectedActiveMeds.size() + " items ?");
            builder.setTitle("Confirm");

            builder.setPositiveButton("Share", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            alert = builder.create();
            alert = builder.show();

            alert.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    for(MedicineInformation med: selectedActiveMeds){

                        int[] hrs = new int[3];
                        hrs[0] = 4;
                        int[] mnts = new int[3];
                        mnts[0] = 00;
                        Intent intent = new Intent(DeviceConnectActivity.this, DeviceActivity.class);
                        intent.putExtra("medicineName", med.getMedicineName());
                        intent.putExtra("dosage", med.getDosage());
                        intent.putExtra("instruction", med.getInstruction());
                        intent.putExtra("intakeTimes",1);
                        intent.putExtra("hrs",hrs);
                        intent.putExtra("mnts", mnts);
                        startActivity(intent);
                        alert.dismiss();
                    }

                }
            });
        }


        else if(item.getItemId() == R.id.menu_select_all){
            if(item.getIcon().getConstantState() == (getResources().getDrawable(R.drawable.ic_baseline_check_box_outline_blank_24)).getConstantState()){
                item.setIcon(R.drawable.ic_baseline_check_box_24);
                selectedActiveMeds = retrieveMedDetails;
                counter = retrieveMedDetails.size();
                adapter.setTheCheckBox();
            }
            else if(item.getIcon().getConstantState() == getResources().getDrawable(R.drawable.ic_baseline_check_box_24).getConstantState()){
                item.setIcon(R.drawable.ic_baseline_check_box_outline_blank_24);
                selectedActiveMeds.clear();
                counter = 0;
                adapter.setUncheckBox();
            }
            updateToolbarText(counter);
        }

        return super.onOptionsItemSelected(item);

    }

    private void clearActionMode(){
        isActionMode = false;
        txtToolbar.setVisibility(View.VISIBLE);
        txtToolbar.setText("Connect to Device");
        btnBack.setVisibility(View.GONE);
        counter = 0;
        selectedActiveMeds.clear();
        toolbar.getMenu().clear();
        adapter.notifyDataSetChanged();
    }
}