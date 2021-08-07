package com.example.mediguide.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mediguide.R;
import com.example.mediguide.RefillActivity;
import com.example.mediguide.data.MedicineInformation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class  RefillAdapter extends RecyclerView.Adapter<RefillAdapter.RefillViewHolder>{
    private Context context;
    private ArrayList<MedicineInformation> activeMedicines;
    private ArrayList<RefillAdapter.RefillViewHolder> holders = new ArrayList<>();
    private RefillActivity refillActivity;
    AlertDialog alert;
    private int myText;
    private int total=0;

    public RefillAdapter(Context context){
        this.context = context;
        this.refillActivity = (RefillActivity) context;

    }
    private void updateDatabase(int total,MedicineInformation medicine){
        DatabaseReference reference;
        reference = FirebaseDatabase.getInstance().getReference().child("MedicineInformation");
        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();

        System.out.println("******* hiiiiiiii");

        reference.orderByChild("userId").equalTo(mFirebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = 0;
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        System.out.println("******* hello111111");
                        System.out.println(snapshot.child("medicineId").getValue().toString());
                        System.out.println(medicine.getMedicineId());
                        if (snapshot.child("medicineId").getValue().toString().equals(medicine.getMedicineId())) {
                            System.out.println("******* hello");
                            reference.child(snapshot.getKey()).child("refillCount").setValue(total);
                            Intent intent = new Intent(context,RefillActivity.class);
                            context.startActivity(intent);
                        }

                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void setDataToRefillAdapter(ArrayList<MedicineInformation> list){
        activeMedicines = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RefillAdapter.RefillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.refill,parent,false);
        return new RefillAdapter.RefillViewHolder(view, refillActivity);
    }
    @Override
    public void onBindViewHolder(@NonNull RefillAdapter.RefillViewHolder holder, int position) {

        MedicineInformation medicine = activeMedicines.get(position);
        System.out.println( medicine.getMedicineName());
        System.out.println( medicine.getRefillCount());
        holder.medicineName.setText(medicine.getMedicineName());

        holder.editIcon.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                AlertDialog.Builder my_dialog = new AlertDialog.Builder(context);
                my_dialog.setTitle("Add count of pills");

                final EditText tablet_count = new EditText(context);
                tablet_count.setInputType(InputType.TYPE_CLASS_NUMBER);
                my_dialog.setView(tablet_count);

                my_dialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        myText= Integer.parseInt(tablet_count.getText().toString());
                        System.out.println(myText+ "-------------------------------------------:)");
                        Toast.makeText(context, "Tablet added is "+myText, Toast.LENGTH_LONG).show();
                        total= medicine.getRefillCount()+myText;
                        updateDatabase(total,medicine);
                        System.out.println("total is:-------------========="+ total);

                    }
                });
                my_dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                    @Override
                    public  void onClick(DialogInterface dialogInterface, int i){
                        myText=0;
                        dialogInterface.cancel();
                    }
                });
                my_dialog.show();
                System.out.println("-------------------edit count-------------------");
            }

        });

        holder.refillCount.setText(String.valueOf(medicine.getRefillCount()) + " meds left");
    }

    @Override
    public int getItemCount() {
        return activeMedicines.size();
    }


    public class RefillViewHolder extends RecyclerView.ViewHolder {
        private TextView medicineName, refillCount,editIcon;
        private RefillActivity refillActivity;

        public RefillViewHolder(@NonNull View medicineView, RefillActivity refillActivity) {
            super(medicineView);

            medicineName = medicineView.findViewById(R.id.medicineName);
            this.refillActivity = refillActivity;
            refillCount = medicineView.findViewById(R.id.refill_count);
            editIcon =  medicineView.findViewById(R.id.edit_icon);
        }
    }

}