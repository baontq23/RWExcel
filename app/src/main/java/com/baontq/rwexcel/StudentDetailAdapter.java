package com.baontq.rwexcel;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StudentDetailAdapter extends RecyclerView.Adapter<StudentDetailAdapter.DetailVH> {
    private List<StudentDetail> list;

    public StudentDetailAdapter(List<StudentDetail> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public DetailVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DetailVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_student_detail, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DetailVH holder, int position) {
        StudentDetail studentDetail = list.get(position);
        holder.tvName.setText(studentDetail.getName());
        holder.tvTx1.setText("TX1: " + studentDetail.getRegularScore1());
        holder.tvTx2.setText("TX2: " + studentDetail.getRegularScore2());
        holder.tvTx3.setText("TX3: " + studentDetail.getRegularScore3());
        holder.tvMidterm.setText("Midterm: " + studentDetail.getMidtermScore());
        holder.tvFinal.setText("Final: " + studentDetail.getFinalScore());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class DetailVH extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvTx1;
        TextView tvTx2;
        TextView tvTx3;
        TextView tvMidterm;
        TextView tvFinal;
        TextView tvTotal;


        public DetailVH(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvTx1 = itemView.findViewById(R.id.tv_tx1);
            tvTx2 = itemView.findViewById(R.id.tv_tx2);
            tvTx3 = itemView.findViewById(R.id.tv_tx3);
            tvMidterm = itemView.findViewById(R.id.tv_midterm);
            tvFinal = itemView.findViewById(R.id.tv_final);
            tvTotal = itemView.findViewById(R.id.tv_total);
        }
    }
}
