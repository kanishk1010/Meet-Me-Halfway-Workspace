package com.example.iris.meetmehalfway;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by kanis on 12/20/2017.
 */

public class MyMeetingsAction extends AppCompatActivity {

    String meetingDate;
    TextView date,statusText;
    String status;
    Button confirm;

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirm_my_meetings);

        Bundle bundle = getIntent().getExtras();
        final String meetingID = bundle.getString("meetingID");

        TextView meetingIDtv = findViewById(R.id.organizer);
        date = findViewById(R.id.meetingDate);
        meetingIDtv.setText(meetingID);
        statusText = findViewById(R.id.status);
        confirm = findViewById(R.id.button_confirm);

        new GetMeetingInfo().execute(meetingID);

        confirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyMeetingsAction.this, ConfirmMeeting.class);
                intent.putExtra("meetingId", meetingID);
                startActivity(intent);

            }
        });

    }

    public class GetMeetingInfo extends AsyncTask<String, Void, Map<String,String>> {

        @Override
        protected Map<String,String> doInBackground(String... strings) {
            String meetingID = strings[0];
            Map<String,String> result = new HashMap<>();
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
                result.put("Date", meeting.getMeetingDate());


                if (meeting.getFinalStatus()){
                    result.put("status", "done");
                }
                else if (meeting.getConfirmation().isEmpty()) {
                    result.put("status","wait");
                }else result.put("status","ready");
                Log.d("asyncLoad",  result.toString());
                return result;
            }
            Log.d("asyncLoad", "no data");
            return null;
        }
        @Override
        protected void onPostExecute (Map<String,String> result) {
            if (result==null){
                return;
            }

            meetingDate = result.get("Date");
            date.setText(meetingDate);
            status = result.get("status");
            String statusDisplay;
            switch (status){
                case "done": statusDisplay = "Meeting completed";
                    confirm.setVisibility(View.GONE);
                    break;
                case "wait": statusDisplay = "Awaiting responses";
                    confirm.setVisibility(View.GONE);
                    break;
                default: statusDisplay = "Recommendations ready!";

            }
            statusText.setText(statusDisplay);

        }


    }
}