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

import com.example.trapic_test.Model.Comment;
import com.example.trapic_test.Model.User;
import com.example.trapic_test.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.commons.lang3.text.WordUtils;
import org.ocpsoft.prettytime.PrettyTime;

import java.util.Collections;
import java.util.Date;
import java.util.List;



public class UserCommentAdapter extends RecyclerView.Adapter<UserCommentAdapter.UserCommentHolder> {
    private Context ctx;
    private View v;
    private List<Comment> list;
    public UserCommentAdapter(Context context, @Nullable List<Comment> list) {
        this.ctx = context;
       this.list = list;

       Collections.reverse(this.list);
    }

    @NonNull
    @Override
    public UserCommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent ,false);
        return new UserCommentHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull UserCommentHolder holder, int position) {

        Comment comment = list.get(position);

        PrettyTime p = new PrettyTime();
        final String test = p.format(new Date(comment.getComment_date_time()));
        holder.setCommentInfo(comment.getComment_user_id(),holder.publisher);
        holder.commentMsg.setText(comment.getCmt_msg());
        holder.comment_time.setText(test);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class UserCommentHolder extends RecyclerView.ViewHolder{
        TextView publisher, commentMsg, comment_time;
        public UserCommentHolder(View itemView) {
            super(itemView);
            comment_time = itemView.findViewById(R.id.comment_time);
            publisher = itemView.findViewById(R.id.publisher);
            commentMsg = itemView.findViewById(R.id.comment_msg);
        }

        public void setCommentInfo(String id, final TextView publisher){
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(id);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);

                    publisher.setText(WordUtils.capitalize(user.getUser_firstname()+" "+user.getUser_lastname()));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}
