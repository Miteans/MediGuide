package com.example.mediguide.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mediguide.R;
import com.example.mediguide.data.MedicineInformation;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeViewHolder>{
    private Context context;
    private ArrayList<MedicineInformation> todayMedicines;

    public HomeAdapter(Context context){
        this.context = context;
    }

    public void setDataToMedicationAdapter(ArrayList<MedicineInformation> list){
        todayMedicines = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_card,parent,false);
        return new HomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, int position) {
        MedicineInformation medicine = todayMedicines.get(position);
        System.out.println();
        holder.medicineName.setText(medicine.getMedicineName());
        holder.dosage.setText("Dosage : " + String.valueOf(medicine.getDosage()));
        holder.frequencyOfMedIntake.setText("Frequency : " + String.valueOf(medicine.getFrequencyOfMedIntake()));

        if(holder.intakeTimes.getChildCount() > 0)
            holder.intakeTimes.removeAllViews();

        for(String msg: medicine.getIntakeTimes()){
            holder.intakeTimes.addView(createTextView(msg));
        }


        try {
            Picasso.with(context)
                    .load(medicine.getImageUrl())
                    /*.placeholder(R.drawable.placeholder) //optional*/
                    .resize(158, 108)         //optional
                    /*.centerCrop()       */                 //optional
                    .into(holder.imageView);
        }
        catch (Exception e){}


    }

    private TextView createTextView(String msg){
        TextView textView = new TextView(context);
        textView.setPadding(5, 0, 5, 0);
        textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT));
        textView.setTextSize(15);
        textView.setText(msg);
        return textView;
    }

    @Override
    public int getItemCount() {
        return todayMedicines.size();
    }

    public class HomeViewHolder extends RecyclerView.ViewHolder{
        private TextView medicineName, dosage,frequencyOfMedIntake;
        private ImageView imageView;
        private LinearLayout intakeTimes;

        public HomeViewHolder(@NonNull View homeView){
            super(homeView);

            medicineName = homeView.findViewById(R.id.medicineName);
            dosage = homeView.findViewById(R.id.dosage);
            frequencyOfMedIntake = homeView.findViewById(R.id.freqOfMedIntake);
            imageView = homeView.findViewById(R.id.imageView);
            intakeTimes = homeView.findViewById(R.id.intakeTimes);
        }
    }
}
