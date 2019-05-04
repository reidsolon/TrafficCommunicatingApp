package com.example.trapic_test.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.trapic_test.Model.Notification;
import com.example.trapic_test.Model.User;
import com.example.trapic_test.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
        Notification notif = list.get(position);

        if(notif.getNotif_type().equals("comment")){
            holder.loadUserInfo(notif.getNotif_userId());
            holder.notification_time.setText(notif.getNotif_time());
        }else{
            holder.loadUserInfo2(notif.getNotif_userId());
            holder.notification_time.setText(notif.getNotif_time());
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class NotificationHolder extends RecyclerView.ViewHolder{

        TextView notification_time, notification_type;

        public NotificationHolder(View itemView) {
            super(itemView);

            notification_time = itemView.findViewById(R.id.notification_time);
            notification_type = itemView.findViewById(R.id.notification_type);
        }

        public void loadUserInfo(String id){
            FirebaseDatabase.getInstance().getReference("Users").child(id)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            dataSnapshot.getChildren();

                            User user = dataSnapshot.getValue(User.class);
                            notification_type.setText(user.getUser_firstname()+" commented on your post.");
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }

        public void loadUserInfo2(String id){
            FirebaseDatabase.getInstance().getReference("Users").child(id)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            dataSnapshot.getChildren();

                            User user = dataSnapshot.getValue(User.class);
                            notification_type.setText(user.getUser_firstname()+" commented on your post.");
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }
    }
}
