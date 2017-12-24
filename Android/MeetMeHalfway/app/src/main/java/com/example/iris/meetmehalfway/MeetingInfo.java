package com.example.iris.meetmehalfway;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Created by Iris on 12/19/17.
 */

public class MeetingInfo extends AppCompatActivity{

    String organizer, meetingDate, status;

    TextView organizerName, date;

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meeting_info);
        Bundle bundle = getIntent().getExtras();
        final String meetingID = bundle.getString("meetingID");
        new GetMeetingInfo().execute(meetingID);
        organizerName = (TextView) findViewById(R.id.organizer);
        date = (TextView) findViewById(R.id.meetingDate);
        Button accept = findViewById(R.id.button_accept);
        accept.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MeetingInfo.this, MapsActivityCurrentPlace.class);
                intent.putExtra("action", "accept");
                intent.putExtra("meetingID", meetingID);
                startActivity(intent);
            }

        });
        Button reject = findViewById(R.id.button_reject);
        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SharedPreferences prefs = getSharedPreferences("Context", MODE_PRIVATE);
                        String userID = prefs.getString("UserID", null);
                        String userName = prefs.getString("name", null);
                        JSONObject object = new JSONObject();
                        try {
                            object.put("UserID", userID);
                            object.put("MeetingId", meetingID);
                            object.put("IsAccepted", false);
                            object.put("userName", userName);
                        } catch (org.json.JSONException je){
                            Log.d("JSON_error", je.getMessage());
                        }

                        OkHttpClient client = new OkHttpClient();
                        RequestBody body = RequestBody.create(JSON, object.toString());
                        Request request = new Request.Builder()
                                .url("https://ii9wvxky84.execute-api.us-east-1.amazonaws.com/Beta").post(body).build();
                        Response response = null;
                        try {
                            response = client.newCall(request).execute();
                            Log.i("reject_request", response.body().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Intent intent = new Intent(MeetingInfo.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }).start();
            }
        });
    }

    public class GetMeetingInfo extends AsyncTask<String, Void, List<String>> {
        @Override
        protected List<String> doInBackground(String... strings) {
            String meetingID = strings[0];
            List<String> result = new LinkedList<>();
            Log.d("asyncLoad", meetingID);
            CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                    getApplicationContext(),    /* get the context for the application */
                    "*********************",    /* Identity Pool ID */
                    Regions.US_EAST_1           /* Region for your identity pool--US_EAST_1 or EU_WEST_1*/
            );
            AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
            DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);
            MeetingTable meeting = mapper.load(MeetingTable.class, meetingID);
            if (meeting != null) {
                result.add(meeting.getOrganizer());
                result.add(meeting.getMeetingDate());
                if (meeting.getConfirmation().isEmpty()) {
                    result.add("ready");
                } else result.add("completed");
                Log.d("asyncLoad",  result.toString());
                return result;
            }
            Log.d("asyncLoad", "no data");
            return null;
        }
        @Override
        protected void onPostExecute (List<String> result) {
            if (result==null) {
                return;
            }
            organizer = result.get(0);
            organizerName.setText(organizer);
            meetingDate = result.get(1);
            date.setText(meetingDate);
            status = result.get(2);
        }
    }
}
