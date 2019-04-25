package com.example.trapic_test.MainFragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.trapic_test.Adapters.NewsfeedAdapter;
import com.example.trapic_test.Adapters.NotificationAdapter;
import com.example.trapic_test.Model.Event;
import com.example.trapic_test.Model.Notification;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.Inflater;

public class NotificationFragment extends Fragment {

    private RecyclerView recyclerView;
    private View v;
    private NotificationAdapter notificationAdapter;
    private List<Notification> list;
    private FirebaseAuth auth;
    private Button act_acct_btn;
    private FirebaseUser user;

    public NotificationFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            if(user.isEmailVerified() && FirebaseAuth.getInstance().getCurrentUser() != null){
                v = inflater.inflate(R.layout.notification_layout, container, false);
                initView();
            }else{

                v = inflater.inflate(R.layout.unverified_newsfeed, container, false);
                initViewsUnverified();
            }
        }else{

            v = inflater.inflate(R.layout.unverified_newsfeed, container, false);
            act_acct_btn = v.findViewById(R.id.act_acct_btn);
            act_acct_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), "You are not even logged in or registered!", Toast.LENGTH_SHORT).show();
                }
            });
        }


        return v;
    }
    private void initView(){
        recyclerView = v.findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(v.getContext()));

        list = new ArrayList<>();
    }

    public void initViewsUnverified(){

        act_acct_btn = v.findViewById(R.id.act_acct_btn);
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            if(!user.isEmailVerified() && user != null){
                act_acct_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        user.sendEmailVerification();
                        final ProgressDialog dialog = new ProgressDialog(getContext());
                        dialog.setMessage("Sending account activation link...");
                        dialog.show();
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                                Toast.makeText(getContext(), "Verification is sent to your email", Toast.LENGTH_SHORT).show();
                            }
                        }, 1500);
                    }
                });
            }else{
                Toast.makeText(getContext(), "Register your own account!", Toast.LENGTH_SHORT).show();
            }
        }



    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (FirebaseAuth.getInstance().getCurrentUser() != null && FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {
            loadNotification();
        }


    }

    private void loadNotification(){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Query dbRefs = FirebaseDatabase.getInstance().getReference("Notifications").orderByChild("notif_time");

        dbRefs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                list.clear();

                for(DataSnapshot snapshot :  dataSnapshot.getChildren()) {

                        Notification event = snapshot.getValue(Notification.class);

                        if(event.getNotif_pub_id().equals(user.getUid())){

                            if(event.getNotif_userId() != user.getUid()){
                                list.add(event);
                            }

                        }

                }

                notificationAdapter = new NotificationAdapter(getContext(), list);
                recyclerView.setAdapter(notificationAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
