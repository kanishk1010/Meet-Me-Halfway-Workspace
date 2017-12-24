package com.example.iris.meetmehalfway;

/**
 * Created by Iris on 12/13/17.
 */

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.*;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.*;
import com.google.firebase.iid.FirebaseInstanceId;

public class fillForm extends AppCompatActivity {

    EditText IDText, nameText;

    String name, ID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            name = bundle.getString("name");
            nameText = (EditText) findViewById(R.id.EditTextName);
            nameText.setText(name);
            ID = bundle.getString("id");
            IDText = (EditText) findViewById(R.id.EditUserID);
            IDText.setText(ID);
        }
    }

    public void goMainScreen(View view) {
        Runnable runnable = new Runnable() { public void run() {
            CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                    getApplicationContext(),    /* get the context for the application */
                    "*********************",    /* Identity Pool ID */
                    Regions.US_EAST_1           /* Region for your identity pool--US_EAST_1 or EU_WEST_1*/
            );
            AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
            DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);
            EditText birthdayText = (EditText)findViewById(R.id.EditBirthday);
            EditText phoneText = (EditText)findViewById(R.id.EditUserPhone);
            User user = new User();
            user.setUserName(nameText.getText().toString());
            user.setUserID(IDText.getText().toString());
            user.setUserBirthday(birthdayText.getText().toString());
            user.setUserPhone(phoneText.getText().toString());
            user.setDeviceToken(FirebaseInstanceId.getInstance().getToken());
            mapper.save(user);
        } };
        Thread mythread = new Thread(runnable);
        mythread.start();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}
