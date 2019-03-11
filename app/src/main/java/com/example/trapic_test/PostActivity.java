package com.example.trapic_test;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trapic_test.Model.Event;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class PostActivity extends AppCompatActivity {

    //vars
    DatabaseReference dbRefs;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    FirebaseStorage fireStore;
    StorageReference storageReference;
    StorageTask uploadTask;

    RelativeLayout layout;
    TextView type, location;
    LinearLayout postBtn, cameraBtn;
    EditText caption ;
    ImageView img1,img2;
    Uri uri;
    ProgressDialog dialog;

    private final int CAMERA_RESULT_CODE = 1; // result code for camera


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.postactivity_layout);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Posting event...");
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        dbRefs = FirebaseDatabase.getInstance().getReference("PostedEvents");
        storageReference = FirebaseStorage.getInstance().getReference("postimage");

        initView();

        String category = getIntent().getStringExtra("Category");
        type.setText(category);

        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validatePost()){
                    dialog.show();
                    addEvent();
                }else{
                    Toast.makeText(getApplicationContext(), "Validation Failed", Toast.LENGTH_LONG).show();
                }
            }
        });

        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA_RESULT_CODE);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CAMERA_RESULT_CODE && resultCode == RESULT_OK){

            uri = data.getData();

        }
    }

    public void initView(){
        layout = findViewById(R.id.postevent_layout);
        caption =  findViewById(R.id.event_caption);
        location =  findViewById(R.id.event_location);
        type =  findViewById(R.id.event_type);
        postBtn =  findViewById(R.id.post_btn);
        cameraBtn = findViewById(R.id.camera_btn);
        img1 = findViewById(R.id.image_1);
        img2 = findViewById(R.id.image_2);
    }

    public void addEvent(){
        final String caption_txt = caption.getText().toString();
        final String type_txt = type.getText().toString();
        final String location_txt = location.getText().toString();
        final StorageReference reference = storageReference.child(System.currentTimeMillis()+"."+getFileExtension(uri));

        uploadTask = reference.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Event event = new Event(caption_txt, type_txt, location_txt, taskSnapshot.getStorage().getDownloadUrl().toString());
                        String id = auth.getUid();
                        String post_id = dbRefs.push().getKey();
                        dbRefs.child(id).child(post_id).setValue(event);
                        dialog.dismiss();

//                        Picasso.with(PostActivity.this).load(uri).fit().into(img1);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                    }
                });


    }
    public boolean validatePost(){

        String caption_txt = caption.getText().toString();
        String type_txt = type.getText().toString();
        String location_txt = location.getText().toString();

        boolean caption_bol;
        boolean location_bol;
        boolean type_bol;

        if(caption_txt.equals("")){
            caption.setError("Enter something about the event");
            caption_bol = false;
        }else{
            caption_bol = true;
        }


        if(location_txt.equals("")){
            location.setError("Adasdasd");
            location_bol = false;
        }else{
            location_bol = true;
        }

        if(type_txt.equals("")){
            type.setText("Select type!");
            type_bol = false;
        }else{
            type_bol= true;
        }




        if(caption_bol && location_bol && type_bol){
            return true;
        }else{
            return false;
        }

    }

    public String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }


}
