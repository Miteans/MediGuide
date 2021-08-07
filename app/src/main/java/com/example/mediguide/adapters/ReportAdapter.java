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
import com.example.mediguide.data.ReportData;
import com.ramotion.foldingcell.FoldingCell;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder>{
    private Context context;
    private ArrayList<ReportData> reportData;

    public ReportAdapter(Context context){
        this.context = context;
    }

    public void setDataToReportAdapter(ArrayList<ReportData> list){
        reportData = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.report_table_rows,parent,false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        ReportData data = reportData.get(position);

        holder.medicineName.setText(data.getMedicineName());
        holder.intakeDate.setText(data.getIntakeDate());
        holder.intakeTiming.setText(data.getIntakeTiming());

        if(data.isIntakeStatus())
            holder.intakeStatus.setText("Yes");
        else
            holder.intakeStatus.setText("No");

    }

    @Override
    public int getItemCount() {
        return reportData.size();
    }

    public class ReportViewHolder extends RecyclerView.ViewHolder{
        private TextView medicineName, intakeTiming, intakeDate, intakeStatus;

        public ReportViewHolder(@NonNull View reportView){
            super(reportView);

            medicineName = reportView.findViewById(R.id.medicineName);
            intakeTiming = reportView.findViewById(R.id.timing);
            intakeDate = reportView.findViewById(R.id.date);
            intakeStatus = reportView.findViewById(R.id.intakeStatus);

        }
    }
}
