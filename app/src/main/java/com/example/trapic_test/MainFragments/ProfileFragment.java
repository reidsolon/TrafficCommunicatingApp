package com.example.trapic_test.MainFragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.trapic_test.Model.User;
import com.example.trapic_test.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.commons.lang3.text.WordUtils;

import java.util.zip.Inflater;

public class ProfileFragment extends Fragment {

    View v;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    DatabaseReference dbRefs;
    FirebaseDatabase firebaseDatabase;

    private TextView user_fullname_txt, user_email;
    public ProfileFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.profile_layout, container, false);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadUserInfo();

        initViews();
    }

    public void initViews(){
        user_fullname_txt = getActivity().findViewById(R.id.user_full_name);
        user_email = getActivity().findViewById(R.id.user_email);
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
                    String suffix = " - verified";
                    user_email.setText(user.getUser_eMail()+suffix);
                }else{
                    String suffix = " - unverified";
                    user_email.setText(user.getUser_eMail()+suffix);
                }
                user_fullname_txt.setText(WordUtils.capitalize(user.getUser_firstname()+" "+user.getUser_lastname()));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
