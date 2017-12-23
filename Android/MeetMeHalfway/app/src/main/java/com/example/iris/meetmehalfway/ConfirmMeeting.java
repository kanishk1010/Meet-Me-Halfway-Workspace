package com.example.iris.meetmehalfway;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * An activity that displays a map showing the place at the device's current location.
 */
public class ConfirmMeeting extends FragmentActivity{

    static int numItems = 1;
    List<String> recommendedPlaces = new ArrayList<>();
    String meetingId;
    ViewPager pager;
    CustomAdapter adapter;
    protected GeoDataClient mGeoDataClient;
    private GoogleApiClient mGoogleApiClient;
    private static String[] placeIds;
    private static final List<Bitmap> bitmap_images = new ArrayList<>();
    private static final List<String> venueName = new ArrayList<>();
    private static final List<String> venueCategory = new ArrayList<>();
    private static final List<String> ratings = new ArrayList<>();
    private static final List<String> priceLevel = new ArrayList<>();
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve the content view that renders the map.
        setContentView(R.layout.recommendation_layout);
        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        Bundle bundle = getIntent().getExtras();
        meetingId = bundle.getString("meetingId");
        new GetRecommendations().execute(meetingId);
        adapter = new CustomAdapter(getSupportFragmentManager());
        pager = findViewById(R.id.pager);
        pager.setAdapter(adapter);

        Button button = findViewById(R.id.confirm);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //To-Do
                int current_page = pager.getCurrentItem();
                Log.d("confirmApi", Integer.toString(current_page));
                final String selectedPlace = placeIds[current_page];

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        JSONObject object = new JSONObject();
                        try {
                            object.put("MeetingId", meetingId);
                            object.put("placeId", selectedPlace);
                        }catch (org.json.JSONException je){
                            Log.d("JSON_error", je.getMessage());
                        }

                        OkHttpClient client = new OkHttpClient();
                        RequestBody body = RequestBody.create(JSON, object.toString());
                        Request request = new Request.Builder()
                                .url("https://vmvxpsfqh7.execute-api.us-east-1.amazonaws.com/Beta").post(body).build();
                        Response response = null;
                        try {
                            response = client.newCall(request).execute();
                            Log.i("new_meeting_request", response.body().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Intent intent = new Intent(ConfirmMeeting.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }).start();


            }
        });


//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
    }
//
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        // Add a marker in Sydney, Australia,
//        // and move the map's camera to the same location.
//        for (int i = 0; i < places.length; i++) {
//            String name = places[i];
//            String lat = places[++i];
//            String lng = places[++i];
//            LatLng sydney = new LatLng(Float.valueOf(lat), Float.valueOf(lng));
//            googleMap.addMarker(new MarkerOptions().position(sydney)
//                    .title(name));
//            googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//        }
//
//    }

    public static class CustomAdapter extends FragmentPagerAdapter {
        public CustomAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return numItems;
        }

        @Override
        public Fragment getItem(int position) {

            return PlacesFragment.newInstance(position);
        }

        @Override
        public int getItemPosition(Object object){
            return POSITION_NONE;
        }
    }

    public static class PlacesFragment extends Fragment {
        private int pageNo;
        private String venue = "", category = "", rating = "", price = "";
        private Bitmap image = null;
        static PlacesFragment newInstance(int pageNo) {
            PlacesFragment placesFragment = new PlacesFragment();
            Bundle args = new Bundle();
            args.putInt("pageNo",pageNo);
            placesFragment.setArguments(args);

            return placesFragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            pageNo = getArguments().getInt("pageNo", 0);
            try {
                venue = venueName.get(pageNo);
               // category= venueCategory.get(pageNo);
                rating = ratings.get(pageNo);
                price = priceLevel.get(pageNo);
                image = bitmap_images.get(pageNo);

            }catch (Exception e) {
                Log.d("data", "not loaded yet");
            }
        }

        @Override
        public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.places_pager, container, false);
            TextView venueTv = (TextView) view.findViewById(R.id.venue_placeholder);
            venueTv.setText(venue);
            TextView categoryTv = (TextView) view.findViewById(R.id.category_placeholder);
            categoryTv.setText(category);
            TextView ratingsTv = (TextView) view.findViewById(R.id.ratings_placeholder);
            ratingsTv.setText(rating);
            TextView priceTv = (TextView) view.findViewById(R.id.price_level_placeholder);
            priceTv.setText(price);
            ImageView imageView = (ImageView) view.findViewById(R.id.place_image);
            if (image != null) {
                imageView.setImageBitmap(image);
            }
            return view;



        }
    }



    public class GetRecommendations extends AsyncTask<String, Void, List<String>> {

        @Override
        protected List<String> doInBackground(String... strings) {
            String meetingID = strings[0];
            List<String> result;
            Log.d("asyncLoad", meetingID);
            CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                    getApplicationContext(),    /* get the context for the application */
                    "us-east-1:502f9e0a-db62-4eb0-81fe-586814b7a8d2",    /* Identity Pool ID */
                    Regions.US_EAST_1           /* Region for your identity pool--US_EAST_1 or EU_WEST_1*/
            );
            AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
            DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);
            MeetingTable meeting = mapper.load(MeetingTable.class, meetingID);
            if (meeting != null) {
                result = meeting.getConfirmation();
                Log.d("asyncLoad",  result.toString());
                return result;
            }
            Log.d("asyncLoad", "no data");
            return null;
        }
        @Override
        protected void onPostExecute ( final List<String> result) {
            if (result==null){
                return;
            }
            numItems = result.size();
            Log.d("OPE", Integer.toString(numItems));
            placeIds = result.toArray(new String[0]);
            Log.d("OPE", placeIds[0]);
            mGeoDataClient = Places.getGeoDataClient(ConfirmMeeting.this, null);
            mGeoDataClient.getPlaceById(placeIds).addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
                @Override
                public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
                    if (task.isSuccessful()) {
                        PlaceBufferResponse places = task.getResult();
                        for (int i = 0; i < places.getCount(); i++) {
                            Place place = places.get(i);
                            venueName.add(place.getName().toString());
                            ratings.add(Float.toString(place.getRating()));
                            priceLevel.add(Integer.toString(place.getPriceLevel()) + "/4");
//                            result.add(new MapsActivity.PlaceIdInfo(myPlace.getName().toString(), myPlace.getLatLng().latitude,
//                                    myPlace.getLatLng().longitude));
//                            adapter.getItem()
                        }
                        places.release();
                    } else {
                        Log.e("", "Place not found.");
                    }

                    for (String placeId : result) {
                        final Task<PlacePhotoMetadataResponse> photoMetadataResponse = mGeoDataClient.getPlacePhotos(placeId);
                        photoMetadataResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
                            @Override
                            public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
                                if (task.isSuccessful()) {
                                    PlacePhotoMetadataResponse photos = task.getResult();
                                    PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                                    PlacePhotoMetadata photoMetadata = photoMetadataBuffer.get(0);
                                    CharSequence attribution = photoMetadata.getAttributions();
                                    Task<PlacePhotoResponse> photoResponse = mGeoDataClient.getPhoto(photoMetadata);
                                    photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
                                        @Override
                                        public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                                            PlacePhotoResponse photo = task.getResult();
                                            bitmap_images.add(photo.getBitmap());
                                        }
                                    });
                                    photoMetadataBuffer.release();
                                }else {
                                    Log.d("place", "photo meta error");
                                }
                            }
                        });

                    }

                }
            });
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
