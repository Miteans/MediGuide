package com.example.mediguide.forms;

import android.Manifest;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.mediguide.MedicationActivity;
import com.example.mediguide.R;
import com.example.mediguide.data.MedicineInformation;
import com.example.mediguide.notification.AlarmReceiver;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class MedAddActivity extends AppCompatActivity {
    MaterialToolbar mToolbar;
    View view1, view2, view3;
    LinearLayout fragmentOne, fragmentTwo, fragmentThree;
    int intakeIndex;
    int randomId;

    Button backToFirstFragment, goToSecondFragment, goToThirdFragment, backToSecondFragment, camera, saveMedInfo;
    ImageView imageView;
    DatePickerDialog picker;

    TextInputEditText medicineName, reasonForIntake, dosage, noMedIntakeText;
    TextInputEditText frequencyOfMedIntake, intakeTimes[], setStartDate, duration, otherInstruction,  refillCount;
    AutoCompleteTextView formOfMedicine, instruction;
    RadioGroup isEverydayMedGrp, notificationGroup;

    boolean isEverydayMedValue;
    String selectedFormOfMedicine, selectedInstruction, imageId, notificationModeValue = "Smart Phone";

    MedicineInformation medicineInformation;
    DatabaseReference reference;
    StorageReference storageReference;
    StorageTask upload;
    ProgressDialog nDialog;

    Bitmap capturedImage;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.med_add_activity);

        initiaseActivity();
        connectToFirstFragment();

        medicineInformation = new MedicineInformation();
        reference = FirebaseDatabase.getInstance().getReference().child("MedicineInformation");
        storageReference = FirebaseStorage.getInstance().getReference("MedicineImage");

        //Form of Medicine dropdown
        String[] options = {"Pill","Insulin","Solution","Powder","Drops","Other"};
        formOfMedicine.setAdapter(new ArrayAdapter<String>(MedAddActivity.this,
                R.layout.option_items, options));

        //Setting up Instruction Dropdown
        String[] ins_options = {"Before eating","After eating","While eating","Doesn't matter"};
        instruction.setAdapter(new ArrayAdapter<String>(MedAddActivity.this,
                R.layout.instrucion_options, ins_options));

        //Image Part
        if (ContextCompat.checkSelfPermission(MedAddActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MedAddActivity.this,
                    new String[] {
                            Manifest.permission.CAMERA
                    },
                    100);
        }

        camera.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 100);
            }
        });

        //Setting up the helper Text for the dosage
        TextInputLayout dosageLayout = (TextInputLayout) findViewById(R.id.dosage);
        formOfMedicine.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedFormOfMedicine = (String) adapterView.getItemAtPosition(i);
                if(selectedFormOfMedicine.equals("Pill")){
                    dosageLayout.setHelperText("Enter number of pills");
                }
                else if(selectedFormOfMedicine.equals("Drops")){
                    dosageLayout.setHelperText("Enter number of drops");
                }
            }
        });

        //get the selected Instruction
        instruction.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedInstruction = (String) adapterView.getItemAtPosition(i);
            }
        });

        notificationGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                RadioButton checkedRadioButton = (RadioButton)group.findViewById(checkedId);
                boolean isChecked = checkedRadioButton.isChecked();
                // If the radiobutton that has changed in check state is now checked...
                if (isChecked)
                {
                    notificationModeValue = checkedRadioButton.getText().toString();
                }
            }
        });


        //Dynamic Input
        TextWatcher watcher= new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if (!(frequencyOfMedIntake.getText().toString().equals(""))) {
                    try {
                        int value = Integer.parseInt(frequencyOfMedIntake.getText().toString());
                        //setting up intakeTimes array
                        intakeTimes = new TextInputEditText[value];
                        intakeIndex = 0;
                        addFormFields(value);
                    }
                    catch (Exception e){
                        System.out.println(e);
                    }
                }

            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Do something or nothing.
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Do something or nothing
            }
        };
        frequencyOfMedIntake.addTextChangedListener(watcher);

        //Setting up the Date picker for Date
        setStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(MedAddActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                setStartDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                picker.show();
            }
        });

        //Navigations
        view1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectToFirstFragment();
            }
        });

        view2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectToSecondFragment();
            }
        });

        view3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectToThirdFragment();
            }
        });

        goToSecondFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            connectToSecondFragment();
            }
        });

        goToThirdFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectToThirdFragment();
            }
        });

        backToFirstFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectToFirstFragment();
            }
        });

        backToSecondFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectToSecondFragment();
            }
        });

        //Adding the medicine detail to Database
        saveMedInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if the form validation is true
                if(setValidation()){

                    nDialog = new ProgressDialog(MedAddActivity.this);
                    nDialog.setMessage("Loading..");
                    nDialog.setTitle("Saving Data");
                    nDialog.setIndeterminate(false);
                    nDialog.setCancelable(true);
                    nDialog.show();

                    medicineInformation.setMedicineId(medicineName.getText().toString() + " " + String.valueOf((new Date()).getTime()));
                    medicineInformation.setMedicineName(medicineName.getText().toString());
                    medicineInformation.setFormOfMedicine(selectedFormOfMedicine);
                    medicineInformation.setDosage(Integer.parseInt(dosage.getText().toString()));
                    medicineInformation.setReasonForIntake(reasonForIntake.getText().toString());

                    //Get the radio button value
                    int selectedId = isEverydayMedGrp.getCheckedRadioButtonId();
                    if(((RadioButton) findViewById(selectedId)).getText().toString().equals("Yes"))
                        isEverydayMedValue = true;
                    else{
                        isEverydayMedValue = false;
                        medicineInformation.setNoMedIntake(Integer.parseInt(noMedIntakeText.getText().toString()));
                    }
                    medicineInformation.setEverydayMed(isEverydayMedValue);
                    medicineInformation.setModeOfNotification(notificationModeValue);

                    medicineInformation.setFrequencyOfMedIntake(Integer.parseInt(frequencyOfMedIntake.getText().toString()));

                    //setting the intake times
                    List<String> intakes = new ArrayList<>();
                    for(int i = 0;i < intakeTimes.length;i++){
                        intakes.add(intakeTimes[i].getText().toString());
                    }
                    medicineInformation.setIntakeTimes(intakes);

                    medicineInformation.setSetStartDate(setStartDate.getText().toString());
                    medicineInformation.setDuration(Integer.parseInt(duration.getText().toString()));

                    if(!refillCount.getText().toString().isEmpty())
                        medicineInformation.setRefillCount(Integer.parseInt(refillCount.getText().toString()));
                    else
                        medicineInformation.setRefillCount(-1);

                    medicineInformation.setInstruction(selectedInstruction);
                    medicineInformation.setOtherInstruction(otherInstruction.getText().toString());

                    //User Id set up
                    FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
                    String userId = currentFirebaseUser.getUid();
                    medicineInformation.setUserId(userId);

                    //Adding Image to the database
                    imageUploader();
                }
            }
        });
    }


    private void imageUploader(){
        final boolean[] successfulUpload = new boolean[1];
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        capturedImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);

        imageId = medicineInformation.getMedicineName() + " " + System.currentTimeMillis() + "." + "jpeg";
        StorageReference Ref = storageReference.child(imageId);

        byte[] b = stream.toByteArray();
        upload = Ref.putBytes(b)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @Override
                            public void onSuccess(Uri uri) {
                                String url = uri.toString();
                                medicineInformation.setImageUrl(url);

                                //Adding data to database
                                reference.push().setValue(medicineInformation);
                                if(medicineInformation.getModeOfNotification().equals("Smart Phone"))
                                    setReminder();

                                nDialog.dismiss();
                                //Confirmation and clearing the form
                                Toast.makeText(MedAddActivity.this, "Saved successfully", Toast.LENGTH_LONG).show();
                                clearForm();
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MedAddActivity.this, "Image Uploading is not possible please try again....", Toast.LENGTH_LONG).show();
                        connectToThirdFragment();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            capturedImage = (Bitmap) data.getExtras().get("data");
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageBitmap(capturedImage);
        }
    }
    
    public void connectToFirstFragment(){
        view1.setBackgroundResource(R.drawable.ic_baseline_lens_24);
        view2.setBackgroundResource(R.drawable.ic_baseline_radio_button_unchecked_24);
        view3.setBackgroundResource(R.drawable.ic_baseline_radio_button_unchecked_24);
        fragmentOne.setVisibility(View.VISIBLE);
        fragmentTwo.setVisibility(View.GONE);
        fragmentThree.setVisibility(View.GONE);
    }

    public void connectToSecondFragment(){
        view2.setBackgroundResource(R.drawable.ic_baseline_lens_24);
        view1.setBackgroundResource(R.drawable.ic_baseline_radio_button_unchecked_24);
        view3.setBackgroundResource(R.drawable.ic_baseline_radio_button_unchecked_24);
        fragmentOne.setVisibility(View.GONE);
        fragmentTwo.setVisibility(View.VISIBLE);
        fragmentThree.setVisibility(View.GONE);

        int selectedId = isEverydayMedGrp.getCheckedRadioButtonId();
        TextInputLayout noMedIntake = findViewById(R.id.no_med_intake);

        if(((RadioButton) findViewById(selectedId)).getText().toString().equals("Yes"))
            noMedIntake.setVisibility(View.GONE);
        else
            noMedIntake.setVisibility(View.VISIBLE);
    }

    public void connectToThirdFragment(){
        view3.setBackgroundResource(R.drawable.ic_baseline_lens_24);
        view1.setBackgroundResource(R.drawable.ic_baseline_radio_button_unchecked_24);
        view2.setBackgroundResource(R.drawable.ic_baseline_radio_button_unchecked_24);
        fragmentOne.setVisibility(View.GONE);
        fragmentTwo.setVisibility(View.GONE);
        fragmentThree.setVisibility(View.VISIBLE);
    }

    public void initiaseActivity(){
        view1 = findViewById(R.id.circle_one);
        view2 = findViewById(R.id.circle_two);
        view3 = findViewById(R.id.circle_three);

        fragmentOne = (LinearLayout) findViewById(R.id.fragment_1);
        fragmentTwo = (LinearLayout) findViewById(R.id.fragment_2);
        fragmentThree = (LinearLayout) findViewById(R.id.fragment_3);

        backToFirstFragment = (Button) findViewById(R.id.backTofirst_fragment);
        goToSecondFragment = (Button) findViewById(R.id.goToSecond_fragment);
        goToThirdFragment = (Button) findViewById(R.id.goTothird_fragment);
        backToSecondFragment = (Button) findViewById(R.id.backToSecond_fragment);

        //first Form
        medicineName = (TextInputEditText) findViewById(R.id.medNameText);
        formOfMedicine = (AutoCompleteTextView) findViewById(R.id.form_of_med_dropdown);
        dosage = (TextInputEditText) findViewById(R.id.dosageText);
        reasonForIntake = (TextInputEditText) findViewById(R.id.reasonText);
        isEverydayMedGrp = (RadioGroup) findViewById(R.id.radioGroup);
        imageView = (ImageView) findViewById(R.id.image_view);
        camera =  (Button) findViewById(R.id.open_camera);

        //second Form
        noMedIntakeText = (TextInputEditText) findViewById(R.id.no_med_intake_text);
        frequencyOfMedIntake = (TextInputEditText) findViewById(R.id.freq_intake);

        //Third Form
        setStartDate = (TextInputEditText) findViewById(R.id.set_date);
        duration = (TextInputEditText) findViewById(R.id.setDuration);
        refillCount = (TextInputEditText) findViewById(R.id.refillCount);
        instruction = (AutoCompleteTextView) findViewById(R.id.set_instruction);
        otherInstruction = (TextInputEditText) findViewById(R.id.otherInstruction);
        notificationGroup = (RadioGroup) findViewById(R.id.notif_radioGroup);
        saveMedInfo = (Button) findViewById(R.id.save_btn);

        //Setting the int type
        dosage.setInputType(InputType.TYPE_CLASS_NUMBER);
        frequencyOfMedIntake.setInputType(InputType.TYPE_CLASS_NUMBER);
        duration.setInputType(InputType.TYPE_CLASS_NUMBER);
        refillCount.setInputType(InputType.TYPE_CLASS_NUMBER);
        noMedIntakeText.setInputType(InputType.TYPE_CLASS_NUMBER);

        selectedFormOfMedicine = "";
        selectedInstruction = "";

        //Back Button(To Medications)
        mToolbar = (MaterialToolbar) findViewById(R.id.topAppBar);
        mToolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MedAddActivity.this, MedicationActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public boolean setValidation(){

        boolean isNameValid, isFormOfMedicineValid, isDosageValid, isFreqValid, isDateValid;
        boolean isDurationValid, isTimeValid = true, isImageValid, isNoMedIntake = true, isNotificationEmpty;

        if (medicineName.getText().toString().isEmpty()) {
            medicineName.setError(getResources().getString(R.string.required));
            isNameValid = false;
        } else  {
            isNameValid = true;
        }

        if (selectedFormOfMedicine.isEmpty()) {
            formOfMedicine.setError(getResources().getString(R.string.required));
            isFormOfMedicineValid = false;
        } else  {
            isFormOfMedicineValid = true;
        }

        if (dosage.getText().toString().isEmpty()) {
            dosage.setError(getResources().getString(R.string.required));
            isDosageValid = false;
        } else  {
            isDosageValid = true;
        }

        int selectedId = isEverydayMedGrp.getCheckedRadioButtonId();

        if(((RadioButton) findViewById(selectedId)).getText().toString().equals("No")){
            if(noMedIntakeText.getText().toString().isEmpty()){
                noMedIntakeText.setError(getResources().getString(R.string.required));
                isNoMedIntake = false;
            }
        }

        if (selectedInstruction.isEmpty())
            selectedInstruction = "Doesn't matter";

        if (reasonForIntake.getText().toString().isEmpty())
            reasonForIntake.setText("");

        if (otherInstruction.getText().toString().isEmpty())
            otherInstruction.setText("");

        if (frequencyOfMedIntake.getText().toString().isEmpty()) {
            frequencyOfMedIntake.setError(getResources().getString(R.string.required));
            isFreqValid = false;
            isTimeValid = false;
        } else  {
            isFreqValid = true;
            isTimeValid = true;
            for(int i = 0;i < Integer.parseInt(frequencyOfMedIntake.getText().toString());i++){
                if(intakeTimes[i].getText().toString().isEmpty()){
                    intakeTimes[i].setError(getResources().getString(R.string.required));
                    isTimeValid = false;
                }
            }
        }

        if(setStartDate.getText().toString().isEmpty()){
            setStartDate.setError(getResources().getString(R.string.required));
            isDateValid = false;
        } else{
            isDateValid = true;
        }

        if(duration.getText().toString().isEmpty()){
            duration.setError(getResources().getString(R.string.required));
            isDurationValid = false;
        } else{
            isDurationValid = true;
        }

        Drawable drawable = imageView.getDrawable();
        boolean hasImage = (drawable != null);

        if (hasImage && (drawable instanceof BitmapDrawable)) {
            isImageValid = true;
        }
        else{
            Toast.makeText(MedAddActivity.this, "Please upload medicine image", Toast.LENGTH_LONG).show();
            isImageValid = false;
            connectToFirstFragment();
            return false;
        }

        if (isNameValid && isDateValid && isDosageValid && isFormOfMedicineValid &&
        isFreqValid  && isDurationValid && isTimeValid && isImageValid && isNoMedIntake) {
            return true;
        }

        Toast.makeText(MedAddActivity.this, "Please fill all the required Fields", Toast.LENGTH_LONG).show();
        return false;
        
    }

    public void addFormFields(int number) {
        LinearLayout time_addView = (LinearLayout) findViewById(R.id.time_add);
        time_addView.setVisibility(View.VISIBLE);

        if (time_addView.getChildCount() > 0) {
            time_addView.removeAllViews();
        }

        for (int i = 1; i <= number; i++) {
            TextInputLayout timeTextInputLayout = new TextInputLayout(MedAddActivity.this);
            timeTextInputLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            if (i == 1)
                timeTextInputLayout.setHint("Set your 1st dose timing");
            else if (i == 2)
                timeTextInputLayout.setHint("Set your 2nd dose timing");
            else if (i == 3)
                timeTextInputLayout.setHint("Set your 3rd dose timing");
            else
                timeTextInputLayout.setHint("Set your " + i + "th dose timing");

            timeTextInputLayout.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE);
            timeTextInputLayout.setBoxStrokeColor(getResources().getColor(R.color.primary));
            timeTextInputLayout.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.primary)));
            timeTextInputLayout.setBoxBackgroundColor(getResources().getColor(R.color.transparent));
            timeTextInputLayout.setBoxCornerRadii(10, 10, 10, 10);
            timeTextInputLayout.setPadding(10, 0, 10, 30);
            TextInputEditText edtTime = new TextInputEditText(timeTextInputLayout.getContext());
            edtTime.setPadding(40, 40, 40, 40);
            edtTime.setId(View.generateViewId());
            timeTextInputLayout.addView(edtTime);
            time_addView.addView(timeTextInputLayout);

            edtTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    Calendar mcurrentTime = Calendar.getInstance();
                    int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                    int minute = mcurrentTime.get(Calendar.MINUTE);
                    TimePickerDialog mTimePicker;
                    mTimePicker = new TimePickerDialog(MedAddActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            edtTime.setText(String.format("%02d:%02d", selectedHour, selectedMinute));
                        }
                    }, hour, minute, false);//Yes 24 hour time
                    mTimePicker.setTitle("Select Time");
                    mTimePicker.show();
                }
            });

            //Adding the time editText to the array
            intakeTimes[intakeIndex] = edtTime;
            intakeIndex += 1;

        }
    }

    public void clearForm(){
        finish();
        Intent intent = new Intent(MedAddActivity.this, MedAddActivity.class);
        startActivity(intent);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean checkTheDate(String medDateString, int medDuration){
        Date currentDate = new Date();
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

        c.add(Calendar.DAY_OF_MONTH, medDuration);

        try{
            endDate = format.parse(format.format(c.getTime()));
        }
        catch (Exception e){}

        return (medDate.compareTo(currentDate) * currentDate.compareTo(endDate) >= 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setReminder() {
        Date setDate = null;
        Date currentDate = new Date();
        Date endDate = null;
        Calendar calendar = Calendar.getInstance();
        Calendar getDuration = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        String userId = currentFirebaseUser.getUid();

        try{
            setDate = format.parse(medicineInformation.getSetStartDate());
        }
        catch (Exception e){}

        calendar.setTime(setDate);
        getDuration.setTime(setDate);

        getDuration.add(Calendar.DAY_OF_MONTH, medicineInformation.getDuration());

        try{
            endDate = format.parse(format.format(getDuration.getTime()));
        }
        catch (Exception e){}


        Random random = new Random();

        for(String time:medicineInformation.getIntakeTimes()){
            String[] times = time.split(":");
            randomId = random.nextInt();
            System.out.println(randomId);

            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(String.valueOf(times[0])));
            calendar.set(Calendar.MINUTE, Integer.parseInt(String.valueOf(times[1])));


            Intent intent = new Intent(this, AlarmReceiver.class);
            intent.putExtra("medicineName", medicineInformation.getMedicineName());
            intent.putExtra("medicineId", medicineInformation.getMedicineId());
            intent.putExtra("dosage", String.valueOf(medicineInformation.getDosage()));
            intent.putExtra("instruction", medicineInformation.getInstruction());
            intent.putExtra("otherInstruction", medicineInformation.getOtherInstruction());
            intent.putExtra("day", String.valueOf(calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.ENGLISH)));
            intent.putExtra("image", medicineInformation.getImageUrl());
            intent.putExtra("time", time);
            intent.putExtra("randomId", randomId);
            intent.putExtra("endDate", endDate.getTime());
            intent.putExtra("userId", userId);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(MedAddActivity.this, randomId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            long timeMilliSeconds = calendar.getTimeInMillis();
            System.out.println(timeMilliSeconds);

            if(endDate.compareTo(currentDate) < 0 && alarmManager != null){
                alarmManager.cancel(pendingIntent);
                System.out.println("Canceling alarm");
            }

            else{
                if(medicineInformation.getEverydayMed())
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, timeMilliSeconds, AlarmManager.INTERVAL_DAY,
                        pendingIntent);
                else{
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, timeMilliSeconds,
                            timeMilliSeconds * 24 * 60 * 60 * medicineInformation.getNoMedIntake(),
                            pendingIntent);
                }
                System.out.println("It comes here ...........");
            }
        }

    }

}