package com.example.iris.meetmehalfway;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.AccessToken;
import com.google.android.gms.maps.GoogleMap;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
//import com.amazonaws.auth.CognitoCachingCredentialsProvider;
//import com.amazonaws.regions.Regions;
//import com.amazonaws.services.dynamodbv2.*;
//import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.*;
//import com.amazonaws.services.dynamodbv2.model.*;


public class MainActivity extends AppCompatActivity {

//    CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
//            getApplicationContext(),    /* get the context for the application */
//            "us-east-1:502f9e0a-db62-4eb0-81fe-586814b7a8d2",    /* Identity Pool ID */
//            Regions.US_EAST_1           /* Region for your identity pool--US_EAST_1 or EU_WEST_1*/
//    );
//    AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
//    DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (AccessToken.getCurrentAccessToken() == null) {

            goLoginScreen();
        }
    }

    public void getLocation(View view) {
        Intent intent = new Intent(MainActivity.this, MapsActivityCurrentPlace.class);
        startActivity(intent);
    }

//    public void getFriendList(View view) {
//        GraphRequestAsyncTask graphRequestAsyncTask = new GraphRequest(
//                AccessToken.getCurrentAccessToken(),
//                "/me/friends",
//                null,
//                HttpMethod.GET,
//                new GraphRequest.Callback() {
//                    public void onCompleted(GraphResponse response) {
//                        Intent intent = new Intent(MainActivity.this, getFriendList.class);
//                        try {
//                            JSONArray rawName = response.getJSONObject().getJSONArray("data");
//                            intent.putExtra("jsondata", rawName.toString());
//                            startActivity(intent);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//        ).executeAsync();
//    }

    private void goLoginScreen() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void logout(View view) throws IOException {
        LoginManager.getInstance().logOut();
//        FirebaseInstanceId.getInstance().deleteInstanceId();
        goLoginScreen();
    }

    public void getInvitationList(View view) {
        Intent intent = new Intent(MainActivity.this, TasksPagerFragment.class);
        startActivity(intent);
    }

    public void getConfirmMeeting(View view) {
        Intent intent = new Intent(MainActivity.this, MyMeetings.class);
        startActivity(intent);
    }

}
