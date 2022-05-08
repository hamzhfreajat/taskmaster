package com.example.taskmaster.ui;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmaster.R;
import com.example.taskmaster.data.TaskData;

import java.util.List;

public class CustomRecyclerView extends RecyclerView.Adapter<CustomRecyclerView.CustomHoleder> {

    List<TaskData> dataList ;

    public CustomRecyclerView(List<TaskData> dataList) {
        this.dataList = dataList;
        Log.i("Array", this.dataList.toString());
    }

    @NonNull
    @Override
    public CustomHoleder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItemView = layoutInflater.inflate(R.layout.activity_task ,parent , false);
        return new CustomHoleder(listItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomHoleder holder, int position) {
        holder.resTitle.setText(dataList.get(position).getTitle());
        holder.resBody.setText(dataList.get(position).getBody());
        holder.resStatus.setText(dataList.get(position).getState());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class CustomHoleder extends RecyclerView.ViewHolder{
        TextView resTitle ;
        TextView resBody ;
        TextView resStatus;
        public CustomHoleder(@NonNull View itemView) {
            super(itemView);
            resTitle = itemView.findViewById(R.id.resTitle);
            resBody = itemView.findViewById(R.id.resBody);
            resStatus = itemView.findViewById(R.id.resStatus);
        }
    }
}
