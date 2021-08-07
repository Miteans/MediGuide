package com.example.mediguide.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mediguide.R;
import com.example.mediguide.data.MedicationReport;
import com.example.mediguide.data.MedicineInformation;
import com.example.mediguide.forms.MedAddActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class AlarmDisplay extends AppCompatActivity {
    TextView time, day, medicineName, instruction, otherInstruction, dosage;
    ImageView imageView;
    String dayString, timeString, medicineNameString, dosageString, instructionString, otherInstructionString, imageUrl, medicineId;
    RadioGroup confirmMedIntake;
    String userId;
    DatabaseReference reference;
    DatabaseReference medicineDataReference;
    MedicationReport medicationReport = new MedicationReport();
    DatabaseReference addData;
    Date exactDate = new Date();
    ImageView backButton;
    Boolean dataExists = false;
    Date endDate = new Date();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_display);

        medicineNameString = (String) getIntent().getStringExtra("medicineName");
        dosageString = (String) getIntent().getStringExtra("dosage");
        instructionString = (String) getIntent().getStringExtra("instruction");
        otherInstructionString = (String) getIntent().getStringExtra("otherInstruction");
        dayString = getIntent().getStringExtra("day");
        timeString = getIntent().getStringExtra("time");
        imageUrl = getIntent().getStringExtra("image");
        exactDate.setTime(getIntent().getLongExtra("currentDate", -1));
        medicineId = getIntent().getStringExtra("medicineId");
        endDate.setTime(getIntent().getLongExtra("endDate", -1));
        userId = getIntent().getStringExtra("userId");

        time = findViewById(R.id.time);
        day = findViewById(R.id.day);
        medicineName = findViewById(R.id.medicineName);
        instruction = findViewById(R.id.instruction);
        otherInstruction = findViewById(R.id.otherInstruction);
        dosage = findViewById(R.id.dosage);
        imageView = findViewById(R.id.imageView1);

        //Intake confirmation button
        confirmMedIntake = findViewById(R.id.radioGroup);

        //back button
        backButton = findViewById(R.id.backButton);

        medicineName.setText(medicineNameString);
        instruction.setText("Instruction : " + instructionString);
        otherInstruction.setText("Other Instruction : " + otherInstructionString);
        dosage.setText("Dosage : " + dosageString);
        time.setText(timeString);
        day.setText(dayString);

        try {
            Picasso.with(this)
                    .load(imageUrl)
                    /*.placeholder(R.drawable.placeholder) //optional*/
                    .resize(320, 170)         //optional
                    /*.centerCrop()       */                 //optional
                    .into(imageView);
        }
        catch (Exception e) {
            System.out.println(e);
        }

        //Intake confirmation
        confirmMedIntake.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                if(((RadioButton) findViewById(checkedId)).getText().toString().equals("Yes")){
                    addConfirmationToDatabase(true);
                    changeRefillValue();
                }
                else
                    addConfirmationToDatabase(false);

                finish();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (confirmMedIntake.getCheckedRadioButtonId() == -1)
                {
                    // no radio buttons are checked
                    AlertDialog.Builder builder = new AlertDialog.Builder(AlarmDisplay.this);
                    builder.setMessage("Medicine Intake Confirmation");

                    builder.setTitle("Confirm");

                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            addConfirmationToDatabase(true);
                            changeRefillValue();
                            finish();
                        }
                    });

                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            addConfirmationToDatabase(false);
                            finish();
                        }
                    });

                    builder.show();
                }
                else
                {
                    // one of the radio buttons is checked
                    finish();
                }
            }
        });

    }

    private void addConfirmationToDatabase(Boolean flag){
        reference = FirebaseDatabase.getInstance().getReference().child("MedicationReport");

        reference.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long dataCount = 0;
                if(dataSnapshot.exists()){
                    dataExists = false;
                    for(DataSnapshot snapshot : dataSnapshot.getChildren() ){
                        dataCount += 1;
                        System.out.println(dataSnapshot.getChildrenCount());
                        System.out.println(dataCount);
                        //Given medicine reminder detail is already present for the current user
                        if(snapshot.child("medicineId").getValue().toString().equals(medicineId)){
                            System.out.println("Data Exists.......................................");
                            addDataToExistingRecord(snapshot, flag);
                            dataExists = true;
                            break;
                        }
                        //Given medicine reminder detail is not present for the current user
                        if(!dataExists && dataSnapshot.getChildrenCount() == dataCount){
                            System.out.println("Come here.......................");
                            addReportData(flag, userId);
                        }
                    }

                }
                //no medicine reminder details is found for the current user
                else{
                    addReportData(flag, userId);
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        
    }

    private void addReportData(Boolean flag, String userId){
        addData = FirebaseDatabase.getInstance().getReference().child("MedicationReport");

        ArrayList<Map<String, ArrayList<Map<String, Boolean>>>> todayMedReportList = new ArrayList<Map<String, ArrayList<Map<String, Boolean>>>>();
        Map<String, ArrayList<Map<String, Boolean>>> todayMedReport = new HashMap<String, ArrayList<Map<String, Boolean>>>();
        ArrayList<Map<String, Boolean>> todayMedIntake = new ArrayList<Map<String, Boolean>>();
        Map<String, Boolean> intakeStatus = new HashMap<String, Boolean>();

        Calendar cal = Calendar.getInstance();
        cal.setTime(exactDate);
        String dateForm = String.format("%02d-%02d-%d",cal.get(Calendar.DATE),+ (cal.get(Calendar.MONTH) + 1), cal.get(Calendar.YEAR));

        intakeStatus.put(timeString, flag);
        todayMedIntake.add(intakeStatus);
        todayMedReport.put(dateForm, todayMedIntake);
        todayMedReportList.add(todayMedReport);

        medicationReport.setMedicineName(medicineNameString);
        medicationReport.setUserId(userId);
        medicationReport.setMedicineId(medicineId);
        medicationReport.setFlagValues(todayMedReportList);
        System.out.println(medicationReport.getMedicineId());
        System.out.println(medicineId);

        addData.push().setValue(medicationReport);
    }

    private void addDataToExistingRecord(DataSnapshot snapshot, Boolean flag){
        int index = 0;
        GenericTypeIndicator<ArrayList<Map<String, ArrayList<Map<String, Boolean>>>>> genericTypeIndicator = new GenericTypeIndicator<ArrayList<Map<String, ArrayList<Map<String, Boolean>>>>>() {
        };
        ArrayList<Map<String, ArrayList<Map<String, Boolean>>>> todayMedReportList;
        Map<String, ArrayList<Map<String, Boolean>>> todayMedReport = new HashMap<String, ArrayList<Map<String, Boolean>>>();
        ArrayList<Map<String, Boolean>> todayMedIntake;
        Map<String, Boolean> intakeStatus = new HashMap<String, Boolean>();

        Calendar cal = Calendar.getInstance();
        cal.setTime(exactDate);
        String dateForm = String.format("%02d-%02d-%d",cal.get(Calendar.DATE),+ (cal.get(Calendar.MONTH) + 1), cal.get(Calendar.YEAR));

        medicationReport.setFlagValues(snapshot.child("flagValues").getValue(genericTypeIndicator));
        todayMedReportList = medicationReport.getFlagValues();

        for(Map<String, ArrayList<Map<String, Boolean>>> medIntakeReport: todayMedReportList){
            if(medIntakeReport.containsKey(dateForm))
                index = todayMedReportList.indexOf(medIntakeReport);
        }

        todayMedIntake = todayMedReportList.get(index).get(dateForm);
        intakeStatus.put(timeString, flag);
        todayMedIntake.add(intakeStatus);
        todayMedReport.put(dateForm, todayMedIntake);
        todayMedReportList.set(index, todayMedReport);

        reference.child(snapshot.getKey()).child("flagValues").setValue(todayMedReportList);
    }

    private void changeRefillValue(){
        medicineDataReference = FirebaseDatabase.getInstance().getReference().child("MedicineInformation");

        medicineDataReference.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int dosage, refillCount;
                if(dataSnapshot.exists()){
                    for(DataSnapshot snapshot : dataSnapshot.getChildren() ){
                        if(snapshot.child("medicineId").getValue().toString().equals(medicineId)){
                            dosage = Integer.parseInt(snapshot.child("dosage").getValue().toString());
                            refillCount = Integer.parseInt(snapshot.child("refillCount").getValue().toString());
                            checkRefillCount(dosage, refillCount, medicineDataReference, snapshot);
                        }
                    }

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    
    private void checkRefillCount(int dosage, int refillCount, DatabaseReference medReference, DataSnapshot snapshot){
        if(dosage <= refillCount){
            refillCount -= dosage;

            if(refillCount >= 0)
                medReference.child(snapshot.getKey()).child("refillCount").setValue(refillCount);
            else
                medReference.child(snapshot.getKey()).child("refillCount").setValue(0);

            if(refillCount < 5){
                long time_difference = endDate.getTime() - exactDate.getTime();
                // Calculate time difference in days using TimeUnit class
                long days_difference = TimeUnit.MILLISECONDS.toDays(time_difference) % 365;

                if(days_difference * dosage < refillCount && refillCount > 0)
                    getNotification( "You only left with " + String.valueOf(refillCount) + "pills for the medicine "
                            + medicineNameString + "\nPlease make sure to refill the medicine");
            }

        }
        else{
            getNotification("Oops there is no pill left to take this medicine " + medicineNameString + "");
        }
    }

    private void getNotification(String msg){
        int randomId;
        Random random = new Random();
        randomId = random.nextInt();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        Intent intent = new Intent(this, RefillNotification.class);
        intent.putExtra("message", msg);
        intent.putExtra("randomId", randomId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(AlarmDisplay.this, randomId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        long timeMilliSeconds = calendar.getTimeInMillis();
        System.out.println(timeMilliSeconds);

        alarmManager.set(AlarmManager.RTC_WAKEUP, timeMilliSeconds, pendingIntent);

    }

}
