package com.example.iris.meetmehalfway;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * An activity that displays a map showing the place at the device's current location.
 */
public class ConfirmMeeting extends AppCompatActivity implements OnMapReadyCallback {

    String[] places = new String[]{"Uncle Ted's",
            "40.7288123",
            "-73.9999319",
            "Dumpling Kingdom",
            "40.7296364",
            "-73.9995996",
            "Carma Asian Tapas",
            "40.7299464",
            "-74.003182",
            "The Rice Noodle",
            "40.7290943",
            "-74.0015195",
            "Chow house",
            "40.7291541",
            "-74.000719"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_maps);
        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.
        for (int i = 0; i < places.length; i++) {
            String name = places[i];
            String lat = places[++i];
            String lng = places[++i];
            LatLng sydney = new LatLng(Float.valueOf(lat), Float.valueOf(lng));
            googleMap.addMarker(new MarkerOptions().position(sydney)
                    .title(name));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        }

    }
}













//package com.example.iris.meetmehalfway;
//
//import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.v7.app.AppCompatActivity;
//import android.util.Log;
//import android.widget.ArrayAdapter;
//import android.widget.ListView;
//
//import com.google.android.gms.location.places.Place;
//import com.google.android.gms.location.places.PlaceBufferResponse;
//import com.google.android.gms.location.places.Places;
//import com.google.android.gms.location.places.GeoDataClient;
//import com.google.android.gms.location.places.PlaceDetectionClient;
//import com.google.android.gms.tasks.OnCompleteListener;
//
///**
// * Created by Iris on 12/15/17.
// */
//
//public class ConfirmMeeting extends AppCompatActivity {
//
//    String[] allPlaces = new String[]{"ChIJDUkb8pFZwokRwOesf2bPdCo", "ChIJ8XF6mpFZwokRveXAm08U9cA", "ChIJI1CTW5JZwokRR70RXRB0Khg", "ChIJDRIZF5JZwokRgg4Ka5hHJT4", "ChIJ8z3tH5JZwokRU1hSLFhRFQA", "ChIJA9tpfJFZwokRs5jBSUqWc4A", "ChIJ1ZcihpFZwokReWLhAob2PNM", "ChIJ2xsvyJFZwokRlnTRyOOFSm8", "ChIJi6DyvJFZwokR2q3sWmde6Bw", "ChIJybfwIZJZwokRRI56C2SlpZo"};
//    GeoDataClient mGeoDataClient;
//    PlaceDetectionClient mPlaceDetectionClient;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.acceptinvitation1);
//        final ListView listView = (ListView) findViewById(R.id.MeetinglistView);
//        listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, allPlaces));
//
//
//        // Construct a GeoDataClient.
//        mGeoDataClient = Places.getGeoDataClient(this, null);
//
//        // Construct a PlaceDetectionClient.
//        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);
//
//
//
//
//
//    }
//
////    mGeoDataClient.getPlaceById(placeId).addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
////        @Override
////        public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
////        if (task.isSuccessful()) {
////        PlaceBufferResponse places = task.getResult();
////        Place myPlace = places.get(0);
////        Log.i(TAG, "Place found: " + myPlace.getName());
////        places.release();
////        } else {
////        Log.e(TAG, "Place not found.");
////        }
////        }
////    });
//
//
//
//
//}
