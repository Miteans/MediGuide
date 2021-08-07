package com.example.mediguide.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mediguide.R;
import com.example.mediguide.SettingsActivity;
import com.example.mediguide.data.MedicineInformation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ramotion.foldingcell.FoldingCell;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MedicationAdapter extends RecyclerView.Adapter<MedicationAdapter.MedicineViewHolder>{
    private Context context;
    private ArrayList<MedicineInformation> medicines;

    public MedicationAdapter(Context context){
        this.context = context;
    }

    public void setDataToMedicationAdapter(ArrayList<MedicineInformation> list){
        medicines = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MedicineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.med_card,parent,false);
        return new MedicineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicineViewHolder holder, int position) {
        MedicineInformation medicine = medicines.get(position);
        holder.medNum.setText(String.valueOf(position+1));
        holder.medicineName.setText(medicine.getMedicineName());
        holder.medicineNameTitle.setText(medicine.getMedicineName());
        holder.reasonForIntake.setText(medicine.getReasonForIntake());
        holder.dosage.setText(String.valueOf(medicine.getDosage()));
        holder.dosageTitle.setText(String.valueOf(medicine.getDosage()));
        holder.frequencyOfMedIntake.setText(medicine.getFrequencyOfMedIntake() + " Times a day");
        holder.setStartDate.setText(medicine.getSetStartDate());
        holder.duration.setText(medicine.getDuration() + " Days");
        holder.otherInstruction.setText(medicine.getOtherInstruction());
        holder.formOfMedicine.setText(medicine.getFormOfMedicine());
        if(!medicine.getInstruction().equals("Doesn't matter"))
            holder.instruction.setText("Take medicine " + medicine.getInstruction());
        else
            holder.instruction.setText(medicine.getInstruction());
        holder.instructionTitle.setText(medicine.getInstruction());

        if(medicine.getEverydayMed())
            holder.isEverydayMedGrp.setText("Yes");
        else
            holder.isEverydayMedGrp.setText("No");

        try {
            Picasso.with(context)
                    .load(medicine.getImageUrl())
                    /*.placeholder(R.drawable.placeholder) //optional*/
                    .resize(158, 108)         //optional
                    /*.centerCrop()       */                 //optional
                    .into(holder.imageView);
        }
        catch (Exception e) {
            System.out.println(e);
        }

        if(holder.intakeTimes.getChildCount() > 0)
            holder.intakeTimes.removeAllViews();

        for(String msg: medicine.getIntakeTimes()){
            holder.intakeTimes.addView(createTextView(msg));
        }

        holder.foldingCell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.foldingCell.toggle(true);
            }
        });

        holder.deleteMed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Confirm");
                builder.setMessage("Are you sure to delete this medicine ?")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                                StorageReference storageReference = firebaseStorage.getReferenceFromUrl(medicine.getImageUrl());
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("MedicineInformation");
                                FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
                                FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
                                DatabaseReference reportReference = FirebaseDatabase.getInstance().getReference().child("MedicationReport");

                                reference.orderByChild("userId").equalTo(mFirebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                if(snapshot.child("medicineId").getValue().toString().equals(medicine.getMedicineId())){
                                                    storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            // File deleted successfully
                                                            snapshot.getRef().removeValue();
                                                            reportReference.orderByChild("userId").equalTo(mFirebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(DataSnapshot reportDataSnapshot) {
                                                                    if (reportDataSnapshot.exists()) {
                                                                        for (DataSnapshot reportSnapshot : reportDataSnapshot.getChildren()) {
                                                                            if (reportSnapshot.child("medicineId").getValue().toString().equals(medicine.getMedicineId())) {
                                                                                reportSnapshot.getRef().removeValue();
                                                                                medicines.remove(position);
                                                                                notifyDataSetChanged();
                                                                                Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_LONG).show();
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                                @Override
                                                                public void onCancelled(DatabaseError databaseError) {
                                                                }
                                                            });
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception exception) {
                                                            // Uh-oh, an error occurred!
                                                            Toast.makeText(context, "Something went wrong please try again....", Toast.LENGTH_LONG).show();
                                                        }
                                                    });
                                                }
                                            }
                                        }
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                });
                            }
                        })
                        .setNegativeButton("Cancel",null);

                builder.show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return medicines.size();
    }

    private TextView createTextView(String msg){
        TextView textView = new TextView(context);
        textView.setPadding(5, 0, 5, 0);
        textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT));
        textView.setTextColor(context.getResources().getColor(R.color.primary));
        textView.setTextSize(15);
        textView.setText(msg);
        return textView;
    }

    public class MedicineViewHolder extends RecyclerView.ViewHolder{
        private FoldingCell foldingCell;
        private TextView medicineName, medicineNameTitle, reasonForIntake, dosage,frequencyOfMedIntake, medNum;
        private TextView setStartDate, dosageTitle, instructionTitle, duration, otherInstruction, formOfMedicine, instruction, isEverydayMedGrp;
        private ImageView imageView;
        private ImageButton deleteMed;
        private LinearLayout intakeTimes;

        public MedicineViewHolder(@NonNull View medicineView){
            super(medicineView);

            foldingCell = medicineView.findViewById(R.id.folding_cell);
            medNum = medicineView.findViewById(R.id.medNum);
            medicineNameTitle = medicineView.findViewById(R.id.medicineNameTitle);
            reasonForIntake = medicineView.findViewById(R.id.reasonForIntake);
            dosageTitle = medicineView.findViewById(R.id.dosageMedTitle);
            frequencyOfMedIntake = medicineView.findViewById(R.id.freqOfMedIntake);
            setStartDate = medicineView.findViewById(R.id.setStartDate);
            duration = medicineView.findViewById(R.id.durationMed);
            otherInstruction = medicineView.findViewById(R.id.otherInstructionMed);
            formOfMedicine = medicineView.findViewById(R.id.formOfMedicine);
            instructionTitle = medicineView.findViewById(R.id.instructionMedTitle);
            isEverydayMedGrp = medicineView.findViewById(R.id.isEverydayMed);
            imageView = medicineView.findViewById(R.id.imageView);
            medicineName = medicineView.findViewById(R.id.medicineName);
            dosage = medicineView.findViewById(R.id.dosageMed);
            instruction = medicineView.findViewById(R.id.instructionMed);
            intakeTimes = medicineView.findViewById(R.id.intakeTimes);
            deleteMed = medicineView.findViewById(R.id.deleteMed);

        }
    }
}
