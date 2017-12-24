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



public class MainActivity extends AppCompatActivity {

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

    private void goLoginScreen() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void logout(View view) throws IOException {
        LoginManager.getInstance().logOut();
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
