package com.example.mediguide.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MedicationReport {
    private String userId;
    private String medicineName;
    private String medicineId;
    private ArrayList<Map<String, ArrayList<Map<String, Boolean>>>> flagValues;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public ArrayList<Map<String, ArrayList<Map<String, Boolean>>>> getFlagValues() {
        return flagValues;
    }

    public void setFlagValues(ArrayList<Map<String, ArrayList<Map<String, Boolean>>>> flagValues) {
        this.flagValues = flagValues;
    }

    public String getMedicineId() {
        return medicineId;
    }

    public void setMedicineId(String medicineId) {
        this.medicineId = medicineId;
    }
}
