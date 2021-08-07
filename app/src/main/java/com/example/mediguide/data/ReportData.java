package com.example.mediguide.data;

public class ReportData {
    private String medicineName;
    private String intakeDate;
    private String intakeTiming;
    private boolean intakeStatus;

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public String getIntakeDate() {
        return intakeDate;
    }

    public void setIntakeDate(String intakeDate) {
        this.intakeDate = intakeDate;
    }

    public String getIntakeTiming() {
        return intakeTiming;
    }

    public void setIntakeTiming(String intakeTiming) {
        this.intakeTiming = intakeTiming;
    }

    public boolean isIntakeStatus() {
        return intakeStatus;
    }

    public void setIntakeStatus(boolean intakeStatus) {
        this.intakeStatus = intakeStatus;
    }
}
