package com.example.trapic_test.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.trapic_test.MainFragment;
import com.example.trapic_test.Model.Event;
import com.example.trapic_test.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.Collection;

public class FeedAdapter extends FirestoreRecyclerAdapter<Event, FeedAdapter.FeedHolder> {
    View v;
    String name;
    Context ctx;
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public FeedAdapter(@NonNull FirestoreRecyclerOptions<Event> options, Context ctx) {
        super(options);
        this.ctx = ctx;
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
        final String id = model.getUser_id();
//        final DocumentReference documentReference = firebaseFirestore.collection("Users").document();
//        final CollectionReference documentReference1 = firebaseFirestore.collection("Users");
//
//
//
//                    documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                        @Override
//                        public void onSuccess(DocumentSnapshot documentSnapshot) {
//                            holder.email.setText(documentSnapshot.getString("eMail"));
//                            String fullname = documentSnapshot.get("firstname")+" "+documentSnapshot.getString("lastname");
//                            holder.user_name.setText(fullname);
//
//
//                            Picasso.with(ctx).load(documentSnapshot.getString("event_image")).fit().into(holder.imageView);
//                            holder.caption.setText(documentSnapshot.getString("event_caption"));
//                        }
//                    });
        Picasso.with(ctx).load(model.getEvent_image()).fit().into(holder.imageView);
        holder.caption.setText(model.getEvent_caption());
        final DocumentReference documentReference = firebaseFirestore.collection("Users").document(id);

        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                holder.email.setText(documentSnapshot.getString("eMail"));
                String fullname = documentSnapshot.get("firstname")+" "+documentSnapshot.getString("lastname");
                holder.user_name.setText(fullname);
            }
        });





    }

    public class FeedHolder extends RecyclerView.ViewHolder {

        TextView email, user_name, caption;
        ImageView imageView;

        public FeedHolder(View itemView) {
            super(itemView);
            user_name = itemView.findViewById(R.id.user_name);
            email = itemView.findViewById(R.id.posted_by);
            imageView = itemView.findViewById(R.id.post_img);
            caption = itemView.findViewById(R.id.caption);
        }
    }
}
