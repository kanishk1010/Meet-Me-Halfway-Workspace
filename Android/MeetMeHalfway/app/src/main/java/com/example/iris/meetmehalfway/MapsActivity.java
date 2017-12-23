package com.example.iris.meetmehalfway;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.tasks.*;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.support.annotation.NonNull;
import java.util.*;

public class MapsActivity extends FragmentActivity {
    private static final String TAG = "MapsActivity";
    protected GeoDataClient mGeoDataClient;
    protected List<PlaceIdInfo> result = new ArrayList<>();
    ArrayAdapter adapter;
    ListView listView;

    class PlaceIdInfo {
        String name;
        double lat;
        double lon;
        PlaceIdInfo(String name, double lat, double lon) {
            this.name = name;
            this.lat = lat;
            this.lon = lon;
        }
        public String toString(){
            return name + ",  " + Double.toString(lat) + ", " + Double.toString(lon);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps1);
        String[] placeIds = {"ChIJDUkb8pFZwokRwOesf2bPdCo", "ChIJ8XF6mpFZwokRveXAm08U9cA", "ChIJI1CTW5JZwokRR70RXRB0Khg", "ChIJDRIZF5JZwokRgg4Ka5hHJT4"};

        mGeoDataClient = Places.getGeoDataClient(this, null);
        mGeoDataClient.getPlaceById(placeIds).addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
            @Override public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
                if (task.isSuccessful()) {
                    PlaceBufferResponse places = task.getResult();
                    for (int i = 0; i < places.getCount(); i++) {
                        Place myPlace = places.get(i);
                        result.add(new PlaceIdInfo(myPlace.getName().toString(), myPlace.getLatLng().latitude,
                                myPlace.getLatLng().longitude));
                        Log.i(TAG, result.toString());
                        adapter.notifyDataSetChanged();
                    }
                    places.release();
                } else {
                    Log.e(TAG, "Place not found.");
                }
            }
        });

        adapter = new ArrayAdapter<PlaceIdInfo>(this,
                R.layout.activity_listview, result);
        listView = (ListView)findViewById(R.id.place_list);
        listView.setAdapter(adapter);
    }
}