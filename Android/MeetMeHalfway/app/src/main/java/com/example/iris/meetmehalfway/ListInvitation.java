package com.example.iris.meetmehalfway;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Iris on 12/15/17.
 */

public class ListInvitation extends AppCompatActivity{

    //final List<String> IDlist = new LinkedList<>() {"1", "2"};
     AtomicBoolean listReady = new AtomicBoolean(false);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invitation);


        // retrieve data from dynamoDB
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),    /* get the context for the application */
                "us-east-1:502f9e0a-db62-4eb0-81fe-586814b7a8d2",    /* Identity Pool ID */
                Regions.US_EAST_1           /* Region for your identity pool--US_EAST_1 or EU_WEST_1*/
        );
        final AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
        DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);

        // scan the entire table
        new Thread(new Runnable() {
            @Override
            public void run() {
                ScanRequest scanRequest = new ScanRequest().withTableName("MeetingTable");
                ScanResult result = ddbClient.scan(scanRequest);
                for (Map<String, AttributeValue> item : result.getItems()){
                    //Toast.makeText(ListInvitation.this, "Clicked item is " + item, Toast.LENGTH_LONG).show();
                    Log.d("watchout", item.get("MeetingId").toString());
                    //IDlist.add(item.get("MeetingId").getS());
                }
                listReady.getAndSet(true);
            }
        }).start();

//        Intent intent = getIntent();
//        String jsondata = intent.getStringExtra("jsondata");
//        Bundle extras = getIntent().getExtras();
//        JSONArray friendslist;
//        ArrayList<String> friends = new ArrayList<String>();
//        final Map<String, String> frienIDdsMap = new HashMap<>();
//        try {
//            friendslist = new JSONArray(jsondata);
//            for (int l=0; l < friendslist.length(); l++) {
//                friends.add(friendslist.getJSONObject(l).getString("name"));
//                friendsMap.put(friendslist.getJSONObject(l).getString("name"), friendslist.getJSONObject(l).getString("id"));
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }


        //MeetingTable allMeetings = mapper.load(MeetingTable.class, id);

        //String queryString = id;

//        Condition condition = new Condition().withComparisonOperator(ComparisonOperator.CONTAINS).
//                withAttributeValueList(new AttributeValue().withS(queryString));
//
//        DynamoDBQueryExpression queryExpression = new DynamoDBQueryExpression().
//                withRangeKeyCondition("acceptStatus", condition);


//        // get this person's facebookID
//        SharedPreferences prefs = getSharedPreferences("Context", MODE_PRIVATE);
//        String ID = prefs.getString("UserID", null);
       // while (Thread.currentThread().isInterrupted()) {
            //if (listReady.get()) {
                ListView lv = (ListView) findViewById(R.id.listview);
                lv.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, friendList));
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        ListView lv = (ListView) parent;
                        TextView tv = (TextView) lv.getChildAt(position);
                        SharedPreferences prefs = getSharedPreferences("Context", MODE_PRIVATE);
                        final SharedPreferences.Editor editor = prefs.edit();

                        String s = tv.getText().toString();
                        editor.putString("MeetingID", s);
                        editor.apply();
                        Toast.makeText(ListInvitation.this, "Clicked item is " + position, Toast.LENGTH_LONG).show();
                        if (position == 0) {
                            // invitation from kanishk to all
                            Intent intent = new Intent(ListInvitation.this, AcceptInviation1.class);
                            startActivity(intent);

                        } else {
                            // invitation from tory to kanishk
                            Intent intent = new Intent(ListInvitation.this, AcceptInvitation2.class);
                            startActivity(intent);
                        }



                    }
                });
            //}
       // }


    }


}
