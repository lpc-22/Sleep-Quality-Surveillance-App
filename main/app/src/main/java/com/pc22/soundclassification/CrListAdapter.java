package com.pc22.soundclassification;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pc22.soundclassification.Room.ClassificationRecord;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CrListAdapter extends RecyclerView.Adapter<CrListAdapter.MyViewHolder> {

    public Context context;
    public List<ClassificationRecord> classificationRecords;

    SimpleDateFormat sdf_time = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public CrListAdapter(Context context) {
        this.context = context;
    }

    public void setClassificationRecords(List<ClassificationRecord> classificationRecords){
        this.classificationRecords = classificationRecords;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CrListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_item_classification, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CrListAdapter.MyViewHolder holder, int position) {

        Date ts = this.classificationRecords.get(position).date;
        String dateToString = sdf_time.format(ts);
        holder.RV_time.setText(dateToString);

        holder.RV_breath.setText(Integer.toString(this.classificationRecords.get(position).breath));
        holder.RV_snore.setText(Integer.toString(this.classificationRecords.get(position).snore));
        holder.RV_cough.setText(Integer.toString(this.classificationRecords.get(position).cough));
        holder.RV_move.setText(Integer.toString(this.classificationRecords.get(position).move));
        holder.RV_noise.setText(Integer.toString(this.classificationRecords.get(position).noise));

        String awakeString = Double.toString(this.classificationRecords.get(position).awake);
        Log.d("format", ""+this.classificationRecords.get(position).awake);
        Log.d("format", awakeString);

        if(awakeString == null){
            holder.RV_awake.setText("-");
        }else{
            holder.RV_awake.setText(awakeString);
        }

    }

    @Override
    public int getItemCount() {
        return this.classificationRecords.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView RV_time;
        TextView RV_breath;
        TextView RV_snore;
        TextView RV_cough;
        TextView RV_move;
        TextView RV_noise;
        TextView RV_awake;


        public MyViewHolder(View view){
            super(view);
            RV_time = view.findViewById(R.id.RV_time);
            RV_breath = view.findViewById(R.id.RV_breath);
            RV_snore = view.findViewById(R.id.RV_snore);
            RV_cough = view.findViewById(R.id.RV_cough);
            RV_move = view.findViewById(R.id.RV_move);
            RV_noise = view.findViewById(R.id.RV_noise);
            RV_awake = view.findViewById(R.id.RV_awake);
        }
    }
}
