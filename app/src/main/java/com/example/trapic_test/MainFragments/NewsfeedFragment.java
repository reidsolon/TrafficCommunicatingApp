package com.example.trapic_test.MainFragments;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.trapic_test.Adapters.FeedAdapter;
import com.example.trapic_test.Adapters.NewsfeedAdapter;
import com.example.trapic_test.Model.Event;
import com.example.trapic_test.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class NewsfeedFragment extends Fragment {

    private RecyclerView recyclerView;
    private FirebaseFirestore firebaseFirestore;
    private NewsfeedAdapter newsfeedAdapter;
    private FirebaseAuth auth;
    private List<Event> list;
    private FirebaseUser user;
    private Button act_acct_btn;
    View view;
    public NewsfeedFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        user = auth.getInstance().getCurrentUser();
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            if(user.isEmailVerified() && user != null){
                view = inflater.inflate(R.layout.newsfeed_layout, container, false);
                initViews();
                readPosts();

            }else{
                view = inflater.inflate(R.layout.unverified_newsfeed, container, false);
                initViewsUnverified();
            }
        }else{
            view = inflater.inflate(R.layout.unverified_newsfeed, container, false);
            initViewsUnverified();
        }

        return view;
    }

    public void initViews(){
        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        list = new ArrayList<>();
    }

    public void initViewsUnverified(){
        act_acct_btn = view.findViewById(R.id.act_acct_btn);

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(!user.isEmailVerified() && user != null){
            act_acct_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    user.sendEmailVerification();
                    Dialog dialog = new Dialog(getContext());
                    dialog.setTitle("Verification link is sent to your account. Please check your email and try to relog your account here." +
                            "- Trapic Team.");
                    dialog.show();
                }
            });
        }else{
            Toast.makeText(getContext(), "Register your own account!", Toast.LENGTH_SHORT).show();
        }


    }

    private void readPosts(){
        Query query = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("event_date_time");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();

                for(DataSnapshot snapshot :  dataSnapshot.getChildren()) {

                    Event event = snapshot.getValue(Event.class);

                    if(event.getEvent_deleted() == false){
                        list.add(event);
                    }

                }

                newsfeedAdapter = new NewsfeedAdapter(getContext(), list);
                recyclerView.setAdapter(newsfeedAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
