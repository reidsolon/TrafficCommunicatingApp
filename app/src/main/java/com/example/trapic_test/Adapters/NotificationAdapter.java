package com.example.trapic_test.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.trapic_test.Model.Notification;
import com.example.trapic_test.R;

import java.util.List;
import java.util.zip.Inflater;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationHolder> {
    Context ctx;
    View v;
    List<Notification> list;
    public NotificationAdapter(Context context, List attrs) {

       this.ctx = context;
       this.list = attrs;
    }

    @NonNull
    @Override
    public NotificationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item, parent, false);
        return new NotificationHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class NotificationHolder extends RecyclerView.ViewHolder{

        public NotificationHolder(View itemView) {
            super(itemView);
        }
    }
}
