package com.example.mediguide.forms;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.mediguide.AppointmentActivity;
import com.example.mediguide.R;
import com.example.mediguide.data.Appointment;
import com.example.mediguide.notification.AppointmentNotification;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class AppointmentForm extends Activity {
    TextInputEditText title, hospital, doctor, appointment_date, appointment_time, reminder_time, reminder_date;
    Button appointment_form;
    boolean isTitleValid, isDateValid, isTimeValid;
    DatePickerDialog picker;
    SwitchMaterial set_reminder;
    LinearLayout reminderLayout;

    DatabaseReference reference;
    Appointment appointment;
    ProgressDialog nDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appointment_form);

        title = (TextInputEditText) findViewById(R.id.title_text);
        hospital = (TextInputEditText) findViewById(R.id.hospital_name_text);
        doctor = (TextInputEditText) findViewById(R.id.doctor_name_text);
        appointment_date = (TextInputEditText) findViewById(R.id.date_text);
        appointment_time = (TextInputEditText) findViewById(R.id.time_text);
        appointment_form = (Button) findViewById(R.id.apt_btn);
        reminderLayout = (LinearLayout) findViewById(R.id.reminderLayout);
        set_reminder = (SwitchMaterial) findViewById(R.id.set_reminder);
        reminder_date = findViewById(R.id.reminder_date_text);
        reminder_time = findViewById(R.id.reminder_time_text);

        appointment = new Appointment();
        reference = FirebaseDatabase.getInstance().getReference().child("Appointment");

        appointment_form.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {

                if(SetValidation()){

                    nDialog = new ProgressDialog(AppointmentForm.this);
                    nDialog.setMessage("Loading..");
                    nDialog.setTitle("Saving Data");
                    nDialog.setIndeterminate(false);
                    nDialog.setCancelable(true);
                    nDialog.show();

                    String title_name = title.getText().toString();
                    String hospital_name = hospital.getText().toString();
                    String doctor_name = doctor.getText().toString();
                    String appointmentDate = appointment_date.getText().toString();
                    String appointmentTime = appointment_time.getText().toString();

                    appointment.setAppointmentId(title_name + " " + String.valueOf((new Date()).getTime()));
                    appointment.setAppointment_title(title_name);
                    appointment.setHospital_name(hospital_name);
                    appointment.setDoctor_name(doctor_name);
                    appointment.setDate(appointmentDate);
                    appointment.setTime(appointmentTime);
                    if(set_reminder.isChecked()){
                        appointment.setIsReminderSet(true);
                        appointment.setReminderDate(reminder_date.getText().toString());
                        appointment.setReminderTime(reminder_time.getText().toString());
                        setAppointmentReminder(appointment.getReminderDate(), appointment.getReminderTime(), appointment.getTime(), appointment.getDate());
                    }
                    else
                        appointment.setIsReminderSet(false);

                    //User Id set up
                    FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
                    String userId = currentFirebaseUser.getUid();
                    appointment.setUserId(userId);

                    reference.push().setValue(appointment);

                    nDialog.dismiss();
                    Toast.makeText(AppointmentForm.this, "Saved successfully", Toast.LENGTH_LONG).show();
                    clearForm();
                }
            }
        });

        set_reminder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    reminderLayout.setVisibility(View.VISIBLE);
                else
                    reminderLayout.setVisibility(View.GONE);
            }
        });

        MaterialToolbar mToolbar = (MaterialToolbar) findViewById(R.id.topAppBar);
        mToolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AppointmentForm.this, AppointmentActivity.class);
                startActivity(intent);
                finish();
            }
        });

        appointment_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(AppointmentForm.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                appointment_date.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                picker.show();
            }
        });

        appointment_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(AppointmentForm.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        appointment_time.setText(String.format("%02d:%02d", selectedHour, selectedMinute));

                    }
                }, hour, minute, false);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        reminder_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(AppointmentForm.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                reminder_date.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                picker.show();
            }
        });

        reminder_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(AppointmentForm.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        reminder_time.setText(String.format("%02d:%02d", selectedHour, selectedMinute));

                    }
                }, hour, minute, false);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

    }

    public Boolean SetValidation() {

        if (title.getText().toString().isEmpty()) {
            title.setError(getResources().getString(R.string.name_error));
            isTitleValid = false;
        } else  {
            isTitleValid = true;
        }

        if (appointment_date.getText().toString().isEmpty()) {
            appointment_date.setError(getResources().getString(R.string.email_error));
            isDateValid = false;
        } else  {
            isDateValid = true;
        }

        if (appointment_time.getText().toString().isEmpty()) {
            appointment_time.setError(getResources().getString(R.string.phone_error));
            isTimeValid = false;
        } else  {
            isTimeValid = true;
        }


        if (isTitleValid && isDateValid && isTimeValid) {
            return true;
        }
        return false;
    }

    public void clearForm(){
        finish();
        Intent intent = new Intent(AppointmentForm.this, AppointmentActivity.class);
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setAppointmentReminder(String reminderDateString, String reminderTimeString, String appointmentTimeString, String appointmentDateString){
        Random random = new Random();
        int randomId1, randomId2;

        randomId1 = random.nextInt();
        randomId2 = random.nextInt();

        while(randomId1 == randomId2){
            randomId1 = random.nextInt();
            randomId2 = random.nextInt();
        }

        Date reminderDate = null, appointmentDate = null;
        String[] reminderTime = reminderTimeString.split(":");
        String[] appointmentTime = appointmentTimeString.split(":");

        Calendar remCalender = Calendar.getInstance();
        Calendar appointmentCalender = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        try{
            reminderDate = dateFormat.parse(reminderDateString);
            appointmentDate = dateFormat.parse(appointmentDateString);
        }
        catch (Exception e){ }

        //setting timings and dates to the calendars
        remCalender.setTime(reminderDate);
        appointmentCalender.setTime(appointmentDate);
        remCalender.set(Calendar.HOUR_OF_DAY, Integer.parseInt(String.valueOf(reminderTime[0])));
        remCalender.set(Calendar.MINUTE, Integer.parseInt(String.valueOf(reminderTime[1])));
        appointmentCalender.set(Calendar.HOUR_OF_DAY, (Integer.parseInt(String.valueOf(appointmentTime[0])) - 1));
        appointmentCalender.set(Calendar.MINUTE, Integer.parseInt(String.valueOf(appointmentTime[1])));

        System.out.println(remCalender.getTime());
        System.out.println(appointmentCalender.getTime());

        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        String userId = currentFirebaseUser.getUid();

        //Providing reminder on reminder date
        Intent reminderIntent = new Intent(this, AppointmentNotification.class);
        reminderIntent.putExtra("appointmentName", appointment.getAppointment_title());
        reminderIntent.putExtra("hospitalName", String.valueOf(appointment.getHospital_name()));
        reminderIntent.putExtra("isRemind", "Yes");
        reminderIntent.putExtra("date", appointmentDateString);
        reminderIntent.putExtra("time",appointmentTimeString);
        reminderIntent.putExtra("randomId", randomId1);
        PendingIntent reminderPendingIntent = PendingIntent.getBroadcast(this, randomId1, reminderIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager reminderAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        long remCalendarTimeInMillis = remCalender.getTimeInMillis();
        System.out.println(remCalendarTimeInMillis);
        reminderAlarmManager.set(AlarmManager.RTC_WAKEUP, remCalendarTimeInMillis, reminderPendingIntent);

        //Providing reminder 1 hour before the appointment
        Intent appointmentDayIntent = new Intent(this, AppointmentNotification.class);
        appointmentDayIntent.putExtra("appointmentName", appointment.getAppointment_title());
        appointmentDayIntent.putExtra("hospitalName", String.valueOf(appointment.getHospital_name()));
        appointmentDayIntent.putExtra("isRemind", "No");
        appointmentDayIntent.putExtra("randomId", randomId2);
        PendingIntent appointmentPendingIntent = PendingIntent.getBroadcast(this, randomId2, appointmentDayIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager appointmentAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        long appointmentCalendarTimeMillis = appointmentCalender.getTimeInMillis();
        System.out.println(appointmentCalendarTimeMillis);
        appointmentAlarmManager.set(AlarmManager.RTC_WAKEUP, appointmentCalendarTimeMillis, appointmentPendingIntent);

    }

}
