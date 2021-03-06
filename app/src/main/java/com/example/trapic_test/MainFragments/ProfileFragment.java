package com.example.trapic_test.MainFragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trapic_test.Model.Event;
import com.example.trapic_test.Model.User;
import com.example.trapic_test.R;
import com.example.trapic_test.SendFeedbackActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.apache.commons.lang3.text.WordUtils;

import java.util.zip.Inflater;

public class ProfileFragment extends Fragment {

    View v;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    DatabaseReference dbRefs;
    private Button act_acct_btn;
    FirebaseDatabase firebaseDatabase;
    private LinearLayout send_feedback_link;
    private ImageView user_status_img;
    private TextView user_fullname_txt, user_email, activate_btn, report_count, post_count;


    public ProfileFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            if (FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {
                v = inflater.inflate(R.layout.profile_layout, container, false);
                initViews();
                loadUserInfo();
                initClicks();
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
    private void initClicks(){

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
    public void initViews(){

        send_feedback_link = v.findViewById(R.id.send_feedback_link);
        activate_btn = v.findViewById(R.id.activate_btn);
        user_status_img = v.findViewById(R.id.user_status_img);
        user_fullname_txt = v.findViewById(R.id.user_full_name);
        user_email = v.findViewById(R.id.user_email);
        post_count = v.findViewById(R.id.post_count);
        report_count = v.findViewById(R.id.report_count2);

        send_feedback_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SendFeedbackActivity.class));
            }
        });
    }

    public void loadUserInfo(){
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        String id = firebaseUser.getUid();
        dbRefs = FirebaseDatabase.getInstance().getReference("Users").child(id);

        dbRefs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if(firebaseUser.isEmailVerified()){
                    user_status_img.setImageResource(R.drawable.ic_verified_user_black_24dp);

                    activate_btn.setVisibility(View.GONE);
                }else{
                    user_status_img.setImageResource(R.drawable.ic_priority_high_black_24dp);
                    activate_btn.setVisibility(View.VISIBLE);
                }
                user_fullname_txt.setText(WordUtils.capitalize(user.getUser_firstname()+" "+user.getUser_lastname()));
                user_email.setText(user.getUser_eMail());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Query databaseReference = FirebaseDatabase
                .getInstance()
                .getReference("Posts")
                .child("user_id").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataSnapshot.getChildren();

                post_count.setText(""+ dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Reports");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
