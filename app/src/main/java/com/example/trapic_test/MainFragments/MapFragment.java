package com.example.trapic_test.MainFragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.trapic_test.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;

import java.util.List;

public class MapFragment extends Fragment implements PermissionsListener{

    MapboxMap mMap;
    MapView mapView;
    private String[] list;
    private Button myLocBtn, myLocBtn2;
    PermissionsManager permissionsManager;
    Location location;
    FusedLocationProviderClient client;
    LatLng latLng;

    public MapFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Mapbox.getInstance(getContext(), "pk.eyJ1IjoicmVpZHNvbG9uIiwiYSI6ImNqcnZpZThzMTAyN2Ezemx4eHMzM2RoZGwifQ.j65VGpYO6g84DnR1koippQ");
        return inflater.inflate(R.layout.map_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapView = view.findViewById(R.id.mapView);
        myLocBtn = view.findViewById(R.id.my_loc);
        myLocBtn2 = view.findViewById(R.id.my_loc2);

        myLocBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableLocationComponent();
            }
        });
        myLocBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list = new String[]{"Night Mode Traffic", "Day Mode Traffic", "Normal Streets"};

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Select Map Type");
                builder.setSingleChoiceItems(list, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == 0){
                            mMap.setStyle(Style.TRAFFIC_NIGHT, new Style.OnStyleLoaded() {
                                @Override
                                public void onStyleLoaded(@NonNull Style style) {
                                    enableLocationComponent();
                                }
                            });
                        }else if(which == 1){
                            mMap.setStyle(Style.TRAFFIC_DAY, new Style.OnStyleLoaded() {
                                @Override
                                public void onStyleLoaded(@NonNull Style style) {
                                    enableLocationComponent();
                                }
                            });
                        }else{
                            mMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                                @Override
                                public void onStyleLoaded(@NonNull Style style) {
                                    enableLocationComponent();
                                }
                            });
                        }
                        dialog.dismiss();
                    }
                });

                builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap) {
                mMap = mapboxMap;


                mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {


                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        // Get an instance of the component
                        initLocationEngine();
                    }
                });

            }
        });
    }
    @SuppressWarnings({"MissingPermission"})
    private void initLocationEngine(){
    }

    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationComponent() {

        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(getContext())) {

            // Get an instance of the component
            LocationComponent locationComponent = mMap.getLocationComponent();

            // Activate with options
            locationComponent.activateLocationComponent(getContext(), mMap.getStyle());

            // Enable to make component visible
            locationComponent.setLocationComponentEnabled(true);


            // Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);

            // Set the component's render mode
            locationComponent.setRenderMode(RenderMode.COMPASS);

            CameraPosition position = new CameraPosition.Builder().zoom(17).bearing(180).tilt(30).build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));

        } else {

            permissionsManager = new PermissionsManager(this);

            permissionsManager.requestLocationPermissions(getActivity());

        }
    }

    @Override
    @SuppressWarnings( {"MissingPermission"})
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }


    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {

    }

    @Override
    public void onPermissionResult(boolean granted) {
        if(granted){
            enableLocationComponent();
        }
    }
}
