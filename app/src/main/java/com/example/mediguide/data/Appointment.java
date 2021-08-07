package com.example.mediguide.data;

public class Appointment {
    private String appointmentId;
    private String appointment_title;
    private String hospital_name;
    private String doctor_name;
    private String date;
    private String time;
    private boolean isReminderSet;
    private String reminderDate;
    private String reminderTime;
    private String userId;

    public Appointment() {
    }

    public String getAppointment_title() {
        return appointment_title;
    }

    public void setAppointment_title(String appointment_title) {
        this.appointment_title = appointment_title;
    }

    public String getHospital_name() {
        return hospital_name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setHospital_name(String hospital_name) {
        this.hospital_name = hospital_name;
    }

    public String getDoctor_name() {
        return doctor_name;
    }

    public void setDoctor_name(String doctor_name) {
        this.doctor_name = doctor_name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean getIsReminderSet() {
        return isReminderSet;
    }

    public void setIsReminderSet(boolean isReminderSet) {
        this.isReminderSet = isReminderSet;
    }

    public String getReminderDate() {
        return reminderDate;
    }

    public void setReminderDate(String reminderDate) {
        this.reminderDate = reminderDate;
    }

    public String getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(String reminderTime) {
        this.reminderTime = reminderTime;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }
}
