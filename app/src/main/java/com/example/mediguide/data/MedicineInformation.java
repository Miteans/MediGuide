package com.example.mediguide.data;

import java.util.List;

public class MedicineInformation {
    private String medicineId;
    private String medicineName;
    private int dosage;
    private String formOfMedicine;
    private String reasonForIntake;
    private boolean isEverydayMed;
    private String imageUrl;
    private int frequencyOfMedIntake;
    private List<String> intakeTimes;
    private String setStartDate;
    private int noMedIntake;
    private int duration;
    private String instruction;
    private String otherInstruction;
    private int refillCount;
    private String userId;
    private String modeOfNotification;

    public MedicineInformation() {
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public int getDosage() {
        return dosage;
    }

    public void setDosage(int dosage) {
        this.dosage = dosage;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFormOfMedicine() {
        return formOfMedicine;
    }

    public void setFormOfMedicine(String formOfMedicine) {
        this.formOfMedicine = formOfMedicine;
    }

    public String getReasonForIntake() {
        return reasonForIntake;
    }

    public void setReasonForIntake(String reasonForIntake) {
        this.reasonForIntake = reasonForIntake;
    }

    public boolean getEverydayMed() {
        return isEverydayMed;
    }

    public void setEverydayMed(boolean everydayMed) {
        isEverydayMed = everydayMed;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getFrequencyOfMedIntake() {
        return frequencyOfMedIntake;
    }

    public void setFrequencyOfMedIntake(int frequencyOfMedIntake) {
        this.frequencyOfMedIntake = frequencyOfMedIntake;
    }

    public int getNoMedIntake() {
        return noMedIntake;
    }

    public void setNoMedIntake(int noMedIntake) {
        this.noMedIntake = noMedIntake;
    }


    public List<String> getIntakeTimes() {
        return intakeTimes;
    }

    public void setIntakeTimes(List<String> intakeTimes) {
        this.intakeTimes = intakeTimes;
    }

    public String getSetStartDate() {
        return setStartDate;
    }

    public void setSetStartDate(String setStartDate) {
        this.setStartDate = setStartDate;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public String getOtherInstruction() {
        return otherInstruction;
    }

    public void setOtherInstruction(String otherInstruction) {
        this.otherInstruction = otherInstruction;
    }

    public int getRefillCount(){
        return refillCount;
    }

    public void setRefillCount(int refillCount){
        this.refillCount = refillCount;
    }

    public String getMedicineId() {
        return medicineId;
    }

    public void setMedicineId(String medicineId) {
        this.medicineId = medicineId;
    }

    public String getModeOfNotification() {
        return modeOfNotification;
    }

    public void setModeOfNotification(String modeOfNotification) {
        this.modeOfNotification = modeOfNotification;
    }
}
