package com.example.trapic_test.Adapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trapic_test.CommentsActivity;
import com.example.trapic_test.MainFragment;
import com.example.trapic_test.Model.Event;
import com.example.trapic_test.Model.User;
import com.example.trapic_test.R;
import com.example.trapic_test.ViewMapActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mapbox.mapboxsdk.maps.Style;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.text.WordUtils;
import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class NewsfeedAdapter extends RecyclerView.Adapter<NewsfeedAdapter.NewsfeedHolder>{
    Context ctx;
    View v;
    List<Event> eventList;
    FirebaseUser firebaseUser;
    PrettyTime prettyTime;

    private long thank_num, report_num;


    public NewsfeedAdapter(Context context, List<Event> eventList){
        this.ctx = context;
        this.eventList = eventList;
        Collections.reverse(this.eventList);
    }
    @NonNull
    @Override
    public NewsfeedHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_item, parent, false);
        return new NewsfeedAdapter.NewsfeedHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final NewsfeedHolder holder, int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final Event event = eventList.get(position);

        Picasso.with(ctx).load(event.getEvent_image()).fit().into(holder.imageView);
        PrettyTime p = new PrettyTime();
        final String pretty = p.format(new Date(event.getEvent_date_time()));
//        Integer num1 = Integer.parseInt(pretty);
//        Integer num2 = Integer.parseInt("2 hours ago");
//        if(num1 >= num2){
//
//            FirebaseDatabase.getInstance().getReference("Posts")
//                    .child(event.getEvent_id())
//                    .child("event_deleted").setValue(true);
//        }

//        if(event.getEvent_status()=="closed"){
//            Integer close_num1 = Integer.parseInt(pretty);
//            Integer close_num2 = Integer.parseInt("20 minutes ago");
//            if(num1 >= num2){
//                FirebaseDatabase.getInstance().getReference("Posts").child(event.getEvent_id())
//                        .child("event_deleted").setValue(true);
//            }
//        }
        FirebaseDatabase.getInstance().getReference("Posts").child(event.getEvent_id()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataSnapshot.getChildren();
                Event event = dataSnapshot.getValue(Event.class);
                if(event.getEvent_status().equals("closed")){
                    prettyTime = new PrettyTime();
                    String date = prettyTime.format(new Date(event.getEvent_closed_time()));
                    holder.event_status_txt.setText("Event is closed "+date);
                    holder.event_status_txt.setBackgroundResource(R.drawable.eventtype_holder);
                }else{
                    holder.event_status_txt.setText("Event is still ongoing");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FirebaseDatabase.getInstance().getReference("Users").child(event.getUser_id()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataSnapshot.getChildren();

                User user = dataSnapshot.getValue(User.class);
                if(user.isUser_isOnline()){
                    holder.isOnline.setVisibility(View.VISIBLE);
                }else{
                    holder.isOnline.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        switch(event.getEvent_type()){
            case "Traffic Jams":{
                holder.event_cat_img.setImageResource(R.drawable.ic_traffic_jam);
                break;
            }

            case "Road Crash":{
                holder.event_cat_img.setImageResource(R.drawable.ic_road_crash);
                break;
            }

            case "Construction Area":{
                holder.event_cat_img.setImageResource(R.drawable.ic_construction_marker);
                break;
            }
        }
        holder.timestamp.setText(pretty);
        holder.caption.setText(event.getEvent_caption());
        holder.caption.setCompoundDrawables(ctx.getResources().getDrawable(R.drawable.ic_construction_marker), null, null, null);
        holder.type.setText(event.getEvent_type());
        holder.setUserInfo(event.getUser_id());
        holder.location.setText(event.getEvent_location());
        holder.cmt_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ctx, CommentsActivity.class);
                intent.putExtra("PostId", event.getEvent_id());
                intent.putExtra("PostBy", holder.user_name.getText().toString());
                intent.putExtra("PublisherId", event.getUser_id());
                ctx.startActivity(intent);

            }

        });

        holder.isLike(event.getEvent_id(), holder.like_img);
        holder.countReport(event.getEvent_id(), holder.report_txt);
        holder.countLike(event.getEvent_id(), holder.like_txt);
        holder.countComment(event.getEvent_id(),holder.cmt_txt);
        holder.like_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.like_img.getTag().equals("like")){
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(event.getEvent_id())
                            .child(firebaseUser.getUid()).setValue(true);
                }else{
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(event.getEvent_id())
                            .child(firebaseUser.getUid()).removeValue();
                }
            }
        });

        holder.viewMapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx, ViewMapActivity.class);
                intent.putExtra("Lat", event.getEvent_lat());
                intent.putExtra("Lng", event.getEvent_lng());
                intent.putExtra("Location", event.getEvent_location());
                intent.putExtra("Time", event.getEvent_time());
                ctx.startActivity(intent);
            }
        });

        if(event.getUser_id().equals(firebaseUser.getUid())){
            holder.deleteBtn.setVisibility(View.VISIBLE);

            holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                    builder.setTitle("Delete Post");
                    builder.setMessage("Are you sure you want to delete this post?");
                    builder.setPositiveButton("Delete Post", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DatabaseReference dbRefs = FirebaseDatabase.getInstance().getReference("Posts");
                            dbRefs.child(event.getEvent_id()).child("event_deleted").setValue(true);
                        }
                    });

                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();

                }
            });

        }else{
            holder.deleteBtn.setVisibility(View.GONE);
        }

        holder.report_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.openDialog(event.getUser_id(), event.getEvent_id());
            }
        });


        long rate = report_num+thank_num;

        holder.trust_rate_txt.setText("Trust rate: "+rate+"%");


    }



    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public class NewsfeedHolder extends RecyclerView.ViewHolder{
        TextView email, user_name, caption, type, timestamp, location, like_txt, cmt_txt, report_txt, trust_rate_txt, event_status_txt;
        LinearLayout like_btn, cmt_btn, viewMapBtn, report_btn;
        Button deleteBtn, dialog_send_btn;
        ImageView imageView, like_img, isOnline, event_cat_img;
        public NewsfeedHolder(View itemView) {
            super(itemView);
            event_cat_img = itemView.findViewById(R.id.event_cat_img);
            isOnline = itemView.findViewById(R.id.isOnline);
            trust_rate_txt = itemView.findViewById(R.id.trust_rate);
            report_txt = itemView.findViewById(R.id.report_count_txt);
            like_img = itemView.findViewById(R.id.like_img);
            location = itemView.findViewById(R.id.location);
            timestamp = itemView.findViewById(R.id.timestamp);
            like_txt = itemView.findViewById(R.id.like_txt);
            type = itemView.findViewById(R.id.event_type);
            user_name = itemView.findViewById(R.id.user_name);
            email = itemView.findViewById(R.id.posted_by);
            imageView = itemView.findViewById(R.id.post_img);
            cmt_txt = itemView.findViewById(R.id.cmt_txt);
            caption = itemView.findViewById(R.id.caption);
            like_btn = itemView.findViewById(R.id.like_btn);
            cmt_btn = itemView.findViewById(R.id.comment_btn);
            viewMapBtn = itemView.findViewById(R.id.viewMapBtn);
            deleteBtn = itemView.findViewById(R.id.delete_btn);
            report_btn = itemView.findViewById(R.id.report_btn);
            event_status_txt = itemView.findViewById(R.id.event_status);
        }
        public void countReport(String post_id, final TextView view){
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Reports").child(post_id).child(post_id);

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if(dataSnapshot.getChildrenCount() > 1){
                        view.setText(dataSnapshot.getChildrenCount()+" Reports");
                        report_num = dataSnapshot.getChildrenCount();
                    }else{
                        view.setText(dataSnapshot.getChildrenCount()+" Report");
                        report_num = dataSnapshot.getChildrenCount();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        public void countLike(String post_id, final TextView view){
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Likes").child(post_id);

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if(dataSnapshot.getChildrenCount() > 1){
                        view.setText(dataSnapshot.getChildrenCount()+" Thanks");
                        thank_num = dataSnapshot.getChildrenCount();
                    }else{
                        view.setText(dataSnapshot.getChildrenCount()+" Thank");
                        thank_num = dataSnapshot.getChildrenCount();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }
        private void openDialog(final String user_id, String event_id){
            final Dialog dialog = new Dialog(ctx);
            dialog.setContentView(R.layout.report_layout);
            dialog.show();

            String uid = user_id;
            final String eid = event_id;
            dialog_send_btn = dialog.findViewById(R.id.report_btn);
            report_txt = dialog.findViewById(R.id.report_msg);

                final Date d = new Date();
                final String d_date = (String) DateFormat.format("MMMM dd, yyyy", d.getDate());
                Date time = new Date();
                final String d_time = (String) DateFormat.format("hh:mm:ss a", time.getTime());

                final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Reports").child(event_id);

                final HashMap<String, Object> map = new HashMap<>();
                String msg = report_txt.getText().toString();
                map.put("report_id", databaseReference.push().getKey());
                map.put("report_user_id", user_id);
                map.put("report_date", d_date);
                map.put("report_time", d_time);
                map.put("report_msg", msg);

                dialog_send_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        if(report_txt.getText().toString().equals("")){
                            report_txt.setError("Please specify your report message!");
                        }else {

                            databaseReference.child(eid).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(!dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).exists()){
                                        databaseReference.child(eid).child(FirebaseAuth
                                                .getInstance()
                                                .getCurrentUser().getUid()).child(databaseReference.push().getKey())
                                                .setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                report_txt.setText("");
                                                Toast.makeText(ctx, "You successfully reported that event", Toast.LENGTH_LONG).show();

                                                Handler handler = new Handler();
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        dialog.dismiss();
                                                    }
                                                }, 1000);
                                            }
                                        });
                                    }else{
                                        Toast.makeText(v.getContext(), "You already reported this post", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                });




        }
        public void countComment(String post_id, final TextView textView){
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Comments").child(post_id);

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    textView.setText("View all "+dataSnapshot.getChildrenCount()+" comment/s");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        public void isLike(String post_id, final ImageView view){
            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Likes").child(post_id);

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.child(user.getUid()).exists()){
                        view.setImageResource(R.drawable.ic_thumb_up_black_24dp);
                        view.setTag("liked");
                    }else{
                        view.setImageResource(R.drawable.ic_thumb_up_black_24dp2);
                        view.setTag("like");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        public void setUserInfo(String id){

            Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("user_id").equalTo(id);

            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        User user = snapshot.getValue(User.class);

                        email.setText(user.getUser_eMail());
                        user_name.setText(WordUtils.capitalize(user.getUser_firstname())+" Lv. 1");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}
