package com.example.trapic_test;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
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
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Continuation;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.util.Date;

public class PostActivity extends AppCompatActivity {

    //vars
    DatabaseReference dbRefs;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    FirebaseStorage fireStorage;
    FirebaseFirestore firestore;
    StorageReference storageReference;
    Task<Uri> uploadTask;

    RelativeLayout layout;
    TextView type, location;
    LinearLayout postBtn, cameraBtn, view;
    EditText caption ;

    ImageView img1,img2;
    Uri uri;
    ProgressDialog dialog;

    LatLng latLang;

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
        fireStorage = FirebaseStorage.getInstance();
        firestore = FirebaseFirestore.getInstance();

        initView();
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED){
            requestStoragePermission();
        }else{
            String category = getIntent().getStringExtra("Category");
            type.setText(category);

            postBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dialog.show();

                    if(validatePost()){
                        addEvent();
                    }else{

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();

                                Toast.makeText(getApplicationContext(), "Something is empty!", Toast.LENGTH_LONG).show();
                            }
                        }, 2000);
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
    }
    public void requestStoragePermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(PostActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)){
            new AlertDialog.Builder(this).setTitle("Permission").setMessage("Allow this app to write external storage to be able to post")
                    .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(PostActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                        }
                    }).setNegativeButton("Don't Allow", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).create().show();
        }else{
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 1){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                startActivity(new Intent(getApplicationContext(), SelectEvent.class));
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CAMERA_RESULT_CODE && resultCode == RESULT_OK){

            Bitmap photo = (Bitmap) data.getExtras().get("data");
            uri = getImageUri(this, photo);
            Picasso.get().load(uri).fit().into(img1);

        }
    }

    public Uri getImageUri(Context ctx, Bitmap bmp){

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(ctx.getContentResolver(), bmp, "Title", null);
        return Uri.parse(path);

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

        uploadTask =  reference.putFile(uri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                if(!task.isSuccessful()){
                    throw task.getException();
                }
                return reference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {

                String id = auth.getUid();
                String id2 = firestore.collection("Posts").document().getId();
                Uri uri2 = task.getResult();
                Date d = new Date();
                final String d_date = (String) DateFormat.format("MMMM d, yyyy", d.getDate());
                Date time = new Date();
                final String d_time = (String) DateFormat.format("hh:mm:ss a", time.getTime());

                Event event = new Event(caption_txt, type_txt, location_txt, uri2.toString(), id, id2, d_time, d_date);
                firestore.collection("Posts").document(id2).set(event).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            dialog.dismiss();
                            Snackbar.make(layout, "Event Posted Successfully" ,Snackbar.LENGTH_LONG).show();

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startActivity(new Intent(getApplicationContext(), MainFragment.class));
                                       finish();
                                }
                            }, 1000);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Failure to post event", Toast.LENGTH_LONG).show();
                        }
                    });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });


//                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//
//                        String id = auth.getUid();
//                        Event event = new Event(caption_txt, type_txt, location_txt, taskSnapshot.getStorage().getDownloadUrl().toString(), id);
//
//                        firestore.collection("Posts").document().set(event).addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//                            dialog.dismiss();
//                            Snackbar.make(layout, "Event Posted Successfully" ,Snackbar.LENGTH_LONG).show();
//
//                            Handler handler = new Handler();
//                            handler.postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    startActivity(new Intent(getApplicationContext(), MainFragment.class));
//                                       finish();
//                                }
//                            }, 1000);
//
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            dialog.dismiss();
//                            Toast.makeText(getApplicationContext(), "Failure to post event", Toast.LENGTH_LONG).show();
//                        }
//                    });
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//
//                    }
//                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//
//                    }
//                });


    }
    public boolean validatePost(){

        String caption_txt = caption.getText().toString();
        String type_txt = type.getText().toString();
        String location_txt = location.getText().toString();

        boolean caption_bol;
        boolean uri_bol;
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

        if(uri == null){
            Toast.makeText(getApplicationContext(), "You do not have specify the photo!", Toast.LENGTH_LONG).show();
            uri_bol = false;
        }else{
            uri_bol = true;
        }




        if(caption_bol && location_bol && type_bol && uri_bol){
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
