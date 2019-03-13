package com.example.trapic_test.MainFragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.trapic_test.Adapters.FeedAdapter;
import com.example.trapic_test.Model.Event;
import com.example.trapic_test.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class NewsfeedFragment extends Fragment {

    private RecyclerView recyclerView;
    private FeedAdapter feedAdapter;
    private FirebaseFirestore firebaseFirestore;

    private DocumentReference documentReference;

    private List<Event> list;
    View view;
    public NewsfeedFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){

        view = inflater.inflate(R.layout.newsfeed_layout, container, false);

        initViews();

        firebaseFirestore = FirebaseFirestore.getInstance();

        documentReference = firebaseFirestore.collection("Posts").document();

        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                Event event = documentSnapshot.toObject(Event.class);

                list.add(event);

                feedAdapter = new FeedAdapter(getContext(), list);

                recyclerView.setAdapter(feedAdapter);
            }

        });

        return view;
    }

    public void initViews(){
        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        list = new ArrayList<>();
    }
}
