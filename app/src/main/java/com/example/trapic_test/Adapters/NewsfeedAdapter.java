package com.example.trapic_test.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.trapic_test.CommentsActivity;
import com.example.trapic_test.MainFragment;
import com.example.trapic_test.Model.Event;
import com.example.trapic_test.Model.User;
import com.example.trapic_test.R;
import com.example.trapic_test.ViewMapActivity;
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

import java.util.List;

public class NewsfeedAdapter extends RecyclerView.Adapter<NewsfeedAdapter.NewsfeedHolder>{
    Context ctx;
    View v;
    List<Event> eventList;
    FirebaseUser firebaseUser;


    public NewsfeedAdapter(Context context, List<Event> eventList){
        this.ctx = context;
        this.eventList = eventList;
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
        Picasso.get().load(event.getEvent_image()).fit().into(holder.imageView);
        holder.timestamp.setText(event.getEvent_time());
        holder.caption.setText(event.getEvent_caption());
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


                    DatabaseReference dbRefs = FirebaseDatabase.getInstance().getReference("Posts");

                    dbRefs.child(event.getEvent_id()).removeValue();
                }
            });

        }else{
            holder.deleteBtn.setVisibility(View.GONE);
        }

    }



    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public class NewsfeedHolder extends RecyclerView.ViewHolder{
        TextView email, user_name, caption, type, timestamp, location, like_txt, cmt_txt;
        LinearLayout like_btn, cmt_btn, viewMapBtn;
        Button deleteBtn;
        ImageView imageView, like_img;
        public NewsfeedHolder(View itemView) {
            super(itemView);

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
        }
        public void countLike(String post_id, final TextView view){
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Likes").child(post_id);

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if(dataSnapshot.getChildrenCount() > 1){
                        view.setText(dataSnapshot.getChildrenCount()+" thanks");
                    }else{
                        view.setText(dataSnapshot.getChildrenCount()+" thank");
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

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
                        user_name.setText(WordUtils.capitalize(user.getUser_firstname()+" "+user.getUser_lastname()));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}
