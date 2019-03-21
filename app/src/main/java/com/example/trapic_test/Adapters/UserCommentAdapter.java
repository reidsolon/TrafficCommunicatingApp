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
import com.example.trapic_test.R;

import java.util.List;



public class UserCommentAdapter extends RecyclerView.Adapter<UserCommentAdapter.UserCommentHolder> {
    private Context ctx;
    private View v;
    private List<Comment> list;
    public UserCommentAdapter(Context context, @Nullable List<Comment> list) {
        this.ctx = context;
       this.list = list;
    }

    @NonNull
    @Override
    public UserCommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent ,false);
        return new UserCommentHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull UserCommentHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class UserCommentHolder extends RecyclerView.ViewHolder{
        TextView publisher, commentMsg;
        public UserCommentHolder(View itemView) {
            super(itemView);

            publisher = itemView.findViewById(R.id.publisher);
            commentMsg = itemView.findViewById(R.id.comment_msg);
        }
    }
}
