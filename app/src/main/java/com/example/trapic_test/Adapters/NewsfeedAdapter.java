package com.example.trapic_test.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.trapic_test.CommentsActivity;
import com.example.trapic_test.Model.Event;
import com.example.trapic_test.Model.User;
import com.example.trapic_test.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
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

        final Event event = eventList.get(position);
        Picasso.get().load(event.getEvent_image()).into(holder.imageView);
        holder.timestamp.setText(event.getEvent_time());
        holder.caption.setText(event.getEvent_caption());
        holder.setUserInfo(event.getUser_id());

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

    }



    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public class NewsfeedHolder extends RecyclerView.ViewHolder{
        TextView email, user_name, caption, type, timestamp, location;
        LinearLayout like_btn, cmt_btn;
        ImageView imageView;
        public NewsfeedHolder(View itemView) {
            super(itemView);

            location = itemView.findViewById(R.id.location);
            timestamp = itemView.findViewById(R.id.timestamp);
            type = itemView.findViewById(R.id.event_type);
            user_name = itemView.findViewById(R.id.user_name);
            email = itemView.findViewById(R.id.posted_by);
            imageView = itemView.findViewById(R.id.post_img);
            caption = itemView.findViewById(R.id.caption);
            like_btn = itemView.findViewById(R.id.like_btn);
            cmt_btn = itemView.findViewById(R.id.comment_btn);
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
