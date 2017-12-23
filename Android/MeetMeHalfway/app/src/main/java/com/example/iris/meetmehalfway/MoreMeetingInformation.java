package com.example.iris.meetmehalfway;


import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Closeable;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
/**
 * Created by Iris on 12/14/17.
 */

public class MoreMeetingInformation extends AppCompatActivity{

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    private EditText date;
    private DatePickerDialog.OnDateSetListener dateListener;
    private EditText time;
    private TimePickerDialog.OnTimeSetListener timeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.more_information);
        date = (EditText)findViewById(R.id.editDate);
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        MoreMeetingInformation.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        dateListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        time = (EditText)findViewById(R.id.editTime);
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int hour = cal.get(Calendar.HOUR_OF_DAY);
                int minute = cal.get(Calendar.MINUTE);

                TimePickerDialog dialog = new TimePickerDialog(
                        MoreMeetingInformation.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        timeListener,
                        hour, minute, false);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        dateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
//                Log.d(TAG, "onDateSet: mm/dd/yyy: " + month + "/" + day + "/" + year);

                String datetext = month + "/" + day + "/" + year;
                date.setText(datetext);
            }
        };

        timeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                //month = month + 1;
//                Log.d(TAG, "onDateSet: mm/dd/yyy: " + month + "/" + day + "/" + year);

                String datetext = hour + ":" + minute;
                time.setText(datetext);
            }
        };
    }

    public void showTimePickerDialog(View v) {
        FragmentManager fm = getFragmentManager();
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(fm, "timePicker");

    }

    public void goMainScreen(View view) throws IOException {
        // retrieve previoous saved data

        new Thread(new Runnable() {
            @Override
            public void run() {


                SharedPreferences prefs = getSharedPreferences("Context", MODE_PRIVATE);
                Set<String> invitedFriends = prefs.getStringSet("invitedFriendList", null);
                Set<String> selectedPreferences = prefs.getStringSet("allPreferences", null);
                String userID = prefs.getString("UserID", null);
                String meetDate = date.getText().toString();
                String meeTime = time.getText().toString();
                String latitude = prefs.getString("latitude", null);
                String longitude = prefs.getString("longitude", null);
                SharedPreferences.Editor editor = prefs.edit();
                editor.remove("invitedFriendList");
                editor.remove("allPreferences");
                editor.remove("latitude");
                editor.remove("longitude");
                editor.apply();
                JSONObject object = new JSONObject();
                JSONArray preferences = new JSONArray(selectedPreferences);
                JSONArray friends = new JSONArray(invitedFriends);
                try {
                    object.put("userID", userID);
                    object.put("meetingDate", meetDate);
                    object.put("meetingTime", meeTime);
                    object.put("preference", preferences);
                    object.put("Friends", friends);
                    object.put("Latitude", latitude);
                    object.put("Longitude", longitude);
                }catch (org.json.JSONException je){
                    Log.d("JSON_error", je.getMessage());
                }

                OkHttpClient client = new OkHttpClient();
                RequestBody body = RequestBody.create(JSON, object.toString());
                Request request = new Request.Builder().url("https://wjsd3hahf2.execute-api.us-east-1.amazonaws.com/beta").post(body).build();
                Response response = null;
                try {
                    response = client.newCall(request).execute();
                    Log.i("new_meeting_request", response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(MoreMeetingInformation.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }).start();



    }

}
