package com.baontq.rwexcel;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentVH> {
    private List<Student> list;
private ItemClickListener itemClickListener;
    public StudentAdapter(List<Student> list, ItemClickListener itemClickListener) {
        this.list = list;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public StudentVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new StudentVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_student, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull StudentVH holder, int position) {
        Student student = list.get(position);
        holder.tvStudentId.setText(student.getId());
        holder.tvStudentName.setText(student.getName());
        holder.tvStudentGender.setText(student.getGender());
        holder.tvStudentDob.setText(student.getDob());
        holder.tvStudentParentId.setText(student.getParentId());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.addOnClickListener(student);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class StudentVH extends RecyclerView.ViewHolder {
        TextView tvStudentId;
        TextView tvStudentName;
        TextView tvStudentDob;
        TextView tvStudentGender;
        TextView tvStudentParentId;

        public StudentVH(@NonNull View itemView) {
            super(itemView);
            tvStudentId = itemView.findViewById(R.id.tv_student_id);
            tvStudentName = itemView.findViewById(R.id.tv_student_name);
            tvStudentDob = itemView.findViewById(R.id.tv_student_dob);
            tvStudentGender = itemView.findViewById(R.id.tv_student_gender);
            tvStudentParentId = itemView.findViewById(R.id.tv_student_parent_id);

        }
    }
}
