package com.example.trapic_test.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.trapic_test.Model.Event;
import com.example.trapic_test.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

public class FeedAdapter extends FirestoreRecyclerAdapter<Event, FeedAdapter.FeedHolder> {
    View v;
    String name;
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public FeedAdapter(@NonNull FirestoreRecyclerOptions<Event> options) {
        super(options);
    }

    @NonNull
    @Override
    public FeedHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
         v = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_item, parent ,false);
        return new FeedHolder(v);
    }

    @Override
    protected void onBindViewHolder(@NonNull final FeedHolder holder, int position, @NonNull final Event model) {

        FirebaseFirestore firebaseFirestore;
        firebaseFirestore = FirebaseFirestore.getInstance();
        String id = model.getUser_id();
        DocumentReference documentReference = firebaseFirestore.collection("Users").document(id);

        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                holder.userName.setText(documentSnapshot.getString("eMail"));

                Picasso.with(v.getContext()).load(model.getEvent_image()).into(holder.imageView);
            }
        });



    }

    public class FeedHolder extends RecyclerView.ViewHolder {

        TextView userName;
        ImageView imageView;

        public FeedHolder(View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.posted_by);
            imageView = itemView.findViewById(R.id.post_img);
        }
    }
}
