package com.example.trapic_test.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.trapic_test.Model.Comment;
import com.example.trapic_test.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.apache.commons.lang3.text.WordUtils;

public class CommentAdapter extends FirestoreRecyclerAdapter<Comment, CommentAdapter.CommentAdapterHolder> {

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    View v;
    Context ctx;
    public CommentAdapter(@NonNull FirestoreRecyclerOptions options, Context ctx) {
        super(options);
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public CommentAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent ,false);
        return new CommentAdapterHolder(v);
    }

    @Override
    protected void onBindViewHolder(@NonNull final CommentAdapterHolder holder, int position, @NonNull Comment model) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
//

        holder.commentMsg.setText(model.getCmt_msg());
        holder.publisher.setText(WordUtils.capitalize(model.getComment_user_fullname()));

    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    public class CommentAdapterHolder extends RecyclerView.ViewHolder{
        TextView publisher, commentMsg;
        public CommentAdapterHolder(View itemView) {
            super(itemView);

            publisher = itemView.findViewById(R.id.publisher);
            commentMsg = itemView.findViewById(R.id.comment_msg);
        }
    }
}
