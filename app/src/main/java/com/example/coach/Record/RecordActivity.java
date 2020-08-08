package com.example.coach.Record;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coach.Adapter.ItemTouchHelper.RecycleItemTouchHelper;
import com.example.coach.Adapter.RunRecordAdapter;
import com.example.coach.R;
import com.example.coach.dbFlow.DBUtils;
import com.example.coach.dbFlow.RunRecord;

import java.util.ArrayList;
import java.util.List;


public class RecordActivity extends AppCompatActivity {
    private static final String TAG = "uuuuuuuuuuu";
    private RunRecordAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    RecyclerView recycleView;
    List<RunRecord> list = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.runrecord_activity);
        recycleView = findViewById(R.id.recycle_view);
        adapter = new RunRecordAdapter(this, list);
        linearLayoutManager = new LinearLayoutManager(this);
        recycleView.setLayoutManager(linearLayoutManager);
        recycleView.setAdapter(adapter);

        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(new RecycleItemTouchHelper(adapter));
        mItemTouchHelper.attachToRecyclerView(recycleView);

        updateAdapter();
    }

    private void updateAdapter() {
        list.addAll(DBUtils.selectAllRunRecord());
        adapter.notifyDataSetChanged();
    }

}
