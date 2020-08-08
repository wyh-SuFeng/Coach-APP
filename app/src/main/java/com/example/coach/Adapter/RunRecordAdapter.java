package com.example.coach.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coach.Adapter.ItemTouchHelper.RecycleItemTouchHelper;
import com.example.coach.R;
import com.example.coach.Record.RecordActivity;
import com.example.coach.Record.RecordShowActivity;
import com.example.coach.dbFlow.DBUtils;
import com.example.coach.dbFlow.RunRecord;

import java.util.Collections;
import java.util.List;

public class RunRecordAdapter extends RecyclerView.Adapter<RunRecordAdapter.MyViewHolder> implements RecycleItemTouchHelper.ItemTouchHelperCallback {
    private RecordActivity recordActivity;
    private List<RunRecord> list;
    public RunRecordAdapter(RecordActivity recordActivity, List<RunRecord> listRecord) {
        this.list=listRecord;
        this.recordActivity=recordActivity;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        View item;
        ImageView imageView;
       public TextView time, distance, sportTime;
int a;
        public MyViewHolder(View itemView) {
            super(itemView);
            item = itemView;
            imageView = itemView.findViewById(R.id.imageView2);
            time = itemView.findViewById(R.id.textView);
            distance = itemView.findViewById(R.id.km);
            sportTime = itemView.findViewById(R.id.textView4);
        }
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recorditem_activity, parent, false);
        final MyViewHolder myViewHolder = new MyViewHolder(view);
        myViewHolder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(recordActivity, RecordShowActivity.class);
                int position=myViewHolder.getAdapterPosition();
                intent.putExtra("groupId", list.get(position).getGroupId());
                recordActivity.startActivity(intent);
            }
        });
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.time.setText(list.get(position).getTime());
        holder.sportTime.setText(String.valueOf(list.get(position).getDuration()));
        String d=String.valueOf(list.get(position).getDistance());
        holder.distance.setText(d.substring(0,d.length()>4?d.indexOf('.')+3:d.indexOf('.')+2));
    }

    @Override
    public int getItemCount() {
       return list.size();
    }

    @Override
    public void onItemDelete(int positon) {
        DBUtils.deleteLocationRecordByGroupId(list.get(positon).getGroupId());
        DBUtils.deleteRunRecordByGroupId(list.get(positon).getGroupId());
        list.remove(positon);
        notifyItemRemoved(positon);
    }

    @Override
    public void onMove(int fromPosition, int toPosition) {
        Collections.swap(list,fromPosition,toPosition);//交换数据
        notifyItemMoved(fromPosition,toPosition);
    }

}
