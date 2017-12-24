package com.example.iris.meetmehalfway;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by kanis on 12/19/2017.
 */

public class MyMeetings extends AppCompatActivity {

    static List<String> myMeetings = new ArrayList<>();
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meetings_layout);
        SharedPreferences user = getSharedPreferences("Context", MODE_PRIVATE);
        String userID = user.getString("UserID", null);
        new GetMyMeetings().execute(userID);
        listView = (ListView) findViewById(R.id.MeetinglistView);
        listView.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,myMeetings));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView lv = (ListView) parent;
                TextView tv = (TextView) lv.getChildAt(position);
                String s = tv.getText().toString();
                Intent intent = new Intent(MyMeetings.this, MyMeetingsAction.class);
                intent.putExtra("meetingID", s);
                startActivity(intent);
            }
        });
    }

    public class GetMyMeetings extends AsyncTask<String, Void, List<MeetingTable>> {

        @Override
        protected List<MeetingTable> doInBackground(String... strings) {
            String userID = strings[0];
            Log.d("asyncLoad", userID);
            CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                    getApplicationContext(),    /* get the context for the application */
                    "***********************",    /* Identity Pool ID */
                    Regions.US_EAST_1           /* Region for your identity pool--US_EAST_1 or EU_WEST_1*/
            );
            AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
            DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);
            MeetingTable meeting = new MeetingTable();
            meeting.setOrganizer(userID);
            DynamoDBQueryExpression<MeetingTable> queryExpression = new DynamoDBQueryExpression<MeetingTable>()
                    .withIndexName("organizer-index").withHashKeyValues(meeting).withConsistentRead(false);
            Log.d("query", queryExpression.toString());
            List<MeetingTable> meetings =  mapper.query(MeetingTable.class, queryExpression);

            return meetings;
        }

        @Override
        protected void onPostExecute (List<MeetingTable> meetings) {
            if (meetings==null){
                return;
            }
            myMeetings.clear();
            for (MeetingTable meeting: meetings) {
                myMeetings.add(meeting.getMeetingID());
            }
            ((BaseAdapter)listView.getAdapter()).notifyDataSetChanged();
        }

    }
}
