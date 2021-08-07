package com.example.mediguide.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mediguide.DeviceConnectActivity;
import com.example.mediguide.R;
import com.example.mediguide.data.MedicineInformation;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class  DeviceConnectAdapter extends RecyclerView.Adapter<DeviceConnectAdapter.ConnectViewHolder> {

    private Context context;
    private ArrayList<MedicineInformation> activeMedicines;
    private ArrayList<DeviceConnectAdapter.ConnectViewHolder> holders = new ArrayList<>();
    private DeviceConnectActivity deviceConnectActivity;


    public DeviceConnectAdapter(Context context){
        this.context = context;
        this.deviceConnectActivity = (DeviceConnectActivity) context;

    }

    public void setDataToDeviceConnectAdapter(ArrayList<MedicineInformation> list){
        activeMedicines = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DeviceConnectAdapter.ConnectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_connect_card,parent,false);
        return new DeviceConnectAdapter.ConnectViewHolder(view, deviceConnectActivity);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceConnectAdapter.ConnectViewHolder holder, int position) {
        MedicineInformation medicine = activeMedicines.get(position);
        holder.medicineName.setText( medicine.getMedicineName());
        holder.reasonForIntake.setText(medicine.getReasonForIntake());

        //To set the image
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

        if(deviceConnectActivity.position == position){
            holder.checkBox.setChecked(true);
            deviceConnectActivity.position = -1;
        }

        if(deviceConnectActivity.isActionMode){
            Anim anim = new Anim(100, holder.linearLayout);
            anim.setDuration(300);
            holder.linearLayout.setAnimation(anim);

        }
        else{
            Anim anim = new Anim(0, holder.linearLayout);
            anim.setDuration(300);
            holder.linearLayout.setAnimation(anim);
            holder.checkBox.setChecked(false);
        }

        holder.cardLayout.setOnLongClickListener(v -> {
            deviceConnectActivity.startSelection(position);
            return true;
        });

        holder.checkBox.setOnClickListener(v -> {
            deviceConnectActivity.check(v, position);
        });

        holders.add(holder);
    }

    @Override
    public int getItemCount() {
        return activeMedicines.size();
    }

    public class ConnectViewHolder extends RecyclerView.ViewHolder{
        private TextView medicineName, reasonForIntake;
        private ImageView imageView;
        private DeviceConnectActivity deviceConnectActivity;
        private CheckBox checkBox;
        private LinearLayout linearLayout;
        private LinearLayout cardLayout;
        public ConnectViewHolder(@NonNull View medicineView, DeviceConnectActivity deviceConnectActivity){
            super(medicineView);

            medicineName = medicineView.findViewById(R.id.medicineName);
            this.deviceConnectActivity = deviceConnectActivity;
            reasonForIntake = medicineView.findViewById(R.id.reasonForIntake);
            imageView = medicineView.findViewById(R.id.imageView);
            checkBox = medicineView.findViewById(R.id.checkbox);
            linearLayout = medicineView.findViewById(R.id.linearLayout);
            cardLayout = medicineView.findViewById(R.id.cardLayout);
        }
    }


    class Anim extends Animation {
        private int width, startWidth;
        private View view;

        public Anim(int width, View view){
            this.width = width;
            this.view =view;
            this.startWidth = view.getWidth();
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t){
            int newWidth = startWidth + (int) ((width - startWidth) + interpolatedTime);

            view.getLayoutParams().width = newWidth;
            view.requestLayout();

            super.applyTransformation(interpolatedTime, t);
        }

        @Override
        public boolean willChangeBounds(){
            return true;
        }
    }

    public void setTheCheckBox(){
        for(DeviceConnectAdapter.ConnectViewHolder holder: holders){
            if(!holder.checkBox.isChecked()){
                holder.checkBox.setChecked(true);
            }
        }
    }

    public void setUncheckBox(){
        for(DeviceConnectAdapter.ConnectViewHolder holder: holders){
            if(holder.checkBox.isChecked()){
                holder.checkBox.setChecked(false);
            }
        }
    }

}