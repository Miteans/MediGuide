package com.example.mediguide;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mediguide.adapters.MedicationAdapter;
import com.example.mediguide.adapters.ReportAdapter;
import com.example.mediguide.data.MedicationReport;
import com.example.mediguide.data.ReportData;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ReportActivity extends AppCompatActivity {
    private DatabaseReference reference;
    private ArrayList<MedicationReport> medicationReport = new ArrayList<MedicationReport>();
    private ArrayList<ReportData> reportData = new ArrayList<ReportData>();
    private RecyclerView recyclerView;
    private ReportAdapter reportAdapter;
    private MaterialToolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_activity);

        reference = FirebaseDatabase.getInstance().getReference().child("MedicationReport");
        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser =  mFirebaseAuth.getCurrentUser();

        recyclerView = findViewById(R.id.recyclerView);
        GenericTypeIndicator<ArrayList<Map<String, ArrayList<Map<String, Boolean>>>>> genericTypeIndicator = new GenericTypeIndicator<ArrayList<Map<String, ArrayList<Map<String, Boolean>>>>>() {
        };

        reference.orderByChild("userId").equalTo(mFirebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    int dataCount = 0;
                    for(DataSnapshot snapshot : dataSnapshot.getChildren() ){

                        MedicationReport report = new MedicationReport();
                        report.setMedicineName(snapshot.child("medicineName").getValue().toString());
                        report.setFlagValues(snapshot.child("flagValues").getValue(genericTypeIndicator));
                        medicationReport.add(report);

                        dataCount += 1;

                        if(dataCount == dataSnapshot.getChildrenCount()){
                            reArrangeData();
                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mToolbar = (MaterialToolbar) findViewById(R.id.topAppBar);
        mToolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ReportActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void reArrangeData(){
        for(MedicationReport report : medicationReport){
            ArrayList<Map<String, ArrayList<Map<String, Boolean>>>> MedReportList;
            Map<String, ArrayList<Map<String, Boolean>>> todayMedReport = new HashMap<String, ArrayList<Map<String, Boolean>>>();
            ArrayList<Map<String, Boolean>> MedIntake;

            MedReportList = report.getFlagValues();

            for(Map<String, ArrayList<Map<String, Boolean>>> medIntakeReport: MedReportList){
                for(Map.Entry<String, ArrayList<Map<String, Boolean>>> pair: medIntakeReport.entrySet()){
                    MedIntake = pair.getValue();
                    for(Map<String,Boolean> intakeStatus: MedIntake){
                        for(Map.Entry<String, Boolean> intake: intakeStatus.entrySet()){
                            ReportData data = new ReportData();
                            data.setMedicineName(report.getMedicineName());
                            data.setIntakeDate(pair.getKey());
                            data.setIntakeTiming(intake.getKey());
                            data.setIntakeStatus(intake.getValue());

                            reportData.add(data);
                        }
                    }
                }
            }
        }

        setUpReportTable();
    }

    private void setUpReportTable(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setFocusable(false);

        reportAdapter = new ReportAdapter(ReportActivity.this);
        recyclerView.setAdapter(reportAdapter);
        reportAdapter.setDataToReportAdapter(reportData);
        recyclerView.setVisibility(View.VISIBLE);
        /*noData.setVisibility(View.GONE);
        nDialog.dismiss();*/
    }
}
