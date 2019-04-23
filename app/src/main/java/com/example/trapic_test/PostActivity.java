package com.example.trapic_test;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
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

import com.example.trapic_test.Model.Comment;
import com.example.trapic_test.Model.Event;
import com.example.trapic_test.Model.Log;
import com.example.trapic_test.Model.Notification;
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
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.os.Build.ID;

public class PostActivity extends AppCompatActivity {

    //vars
    private DatabaseReference dbRefs;
    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;
    private FirebaseStorage fireStorage;
    private FirebaseFirestore firestore;
    private StorageReference storageReference;
    private Task<Uri> uploadTask;

    private RelativeLayout layout;
    private TextView type, location;
    private LinearLayout postBtn, cameraBtn, view;
    private EditText caption ;

    private ImageView img1,img2;
    private Uri uri;
    private ProgressDialog dialog;

    private String category, address;
    private double location_lat, location_lng;
    private final int CAMERA_RESULT_CODE = 1; // result code for camera
    private int imageCounter;
    private String currentImagePath;

    private ImageView cat_img;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.postactivity_layout);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Posting event...");
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        dbRefs = FirebaseDatabase.getInstance().getReference("Posts");
        storageReference = FirebaseStorage.getInstance().getReference("postimage");
        fireStorage = FirebaseStorage.getInstance();
        firestore = FirebaseFirestore.getInstance();

        initView();
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED){
            requestStoragePermission();
        }else{
            category = getIntent().getStringExtra("Category");
            location_lat = getIntent().getDoubleExtra("Lat", 0);
            location_lng = getIntent().getDoubleExtra("Lng", 0);
            address = getIntent().getStringExtra("Address");

            switch (category){
                case "Construction Area":{
                    cat_img.setImageResource(R.drawable.ic_construction_marker);
                break;
                }

                case "Traffic Jams":{
                    cat_img.setImageResource(R.drawable.ic_traffic_jam);
                    break;
                }

                case "Road Crash":{
                    cat_img.setImageResource(R.drawable.ic_road_crash);
                    break;
                }

            }
            type.setText(category);
            location.setText(address);

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

                    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                    StrictMode.setVmPolicy(builder.build());

                    capturePhoto();
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


    public void capturePhoto(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(this.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, CAMERA_RESULT_CODE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CAMERA_RESULT_CODE && resultCode == RESULT_OK){

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(currentImagePath, options);
            uri = getImageUri(this, bitmap);

            Picasso.get().load(uri).fit().into(img1);

        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "User" + ID + "Image" + imageCounter + "_" + timeStamp;
        imageCounter++;
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String path = storageDir.getPath() + "/ALUMA";
        File temp = new File(path);
        temp.mkdirs();
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".png",         /* suffix */
                temp      /* directory */
        );
        currentImagePath = image.getPath();
        return image;
    }

    public Uri getImageUri(Context ctx, Bitmap bmp){

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
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
        cat_img = findViewById(R.id.category_img);

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

                final String id = auth.getUid();
                final String id3 = dbRefs.push().getKey();
                Uri uri2 = task.getResult();
                final String d_date = (String) DateFormat.format("MMMM dd, yyyy", new Date());
                final String d_time = (String) DateFormat.format("hh:mm:ss a", new Date());
                final String date_time = (String) DateFormat.format("MMMM dd, yyyy hh:mm:ss a", new Date());

//                Add 1 hour for deletion
                java.text.DateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy hh:mm:ss a");
                Calendar calendar = null;
                Date date;
                try {
                    date = dateFormat.parse(date_time);
                    calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    calendar.add(Calendar.HOUR, 1);
                } catch (ParseException e) {
                    e.printStackTrace();
                }


                Event event = new Event(calendar.getTime().toString(), "open", caption_txt, type_txt, location_txt, uri2.toString(), id, id3, d_time, d_date, location_lat, location_lng, date_time);
                dbRefs.child(id3).setValue(event).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dialog.dismiss();

                        Notification notification = new Notification(id3);
                        dbRefs = FirebaseDatabase.getInstance().getReference("Notifications");

                        dbRefs.child(id3).setValue(notification).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
                        Log log = new Log("post", id3, FirebaseAuth.getInstance().getCurrentUser().getUid(), " just posted an event.", date_time);
                        DatabaseReference dbRefs = FirebaseDatabase.getInstance().getReference();
                        FirebaseDatabase.getInstance().getReference("Logs").child(dbRefs.push().getKey()).setValue(log);
                        Snackbar.make(layout, "Event Posted Successfully", Snackbar.LENGTH_LONG).show();

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

//            firestore.collection("Posts").document(id2).set(event).addOnSuccessListener(new OnSuccessListener<Void>() {
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
//                });


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
        });
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
            location.setError("Please select event type again!");
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
