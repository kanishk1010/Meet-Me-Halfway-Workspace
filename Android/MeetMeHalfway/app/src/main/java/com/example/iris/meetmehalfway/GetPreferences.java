package com.example.iris.meetmehalfway;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Iris on 12/13/17.
 */

public class GetPreferences extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preferences);
        String[] allChoices = new String[]{"Cafe", "Sushi", "Indian", "Chinese", "Brunch", "Bar"};
        final ListView listView = (ListView) findViewById(R.id.MeetinglistView);
        listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, allChoices));
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView lv = (ListView) parent;
                TextView tv = (TextView) lv.getChildAt(position);
                String s = tv.getText().toString();
                Toast.makeText(GetPreferences.this, "Clicked item is " + s, Toast.LENGTH_LONG).show();
            }
        });

        SharedPreferences prefs = getSharedPreferences("Context", MODE_PRIVATE);
        final SharedPreferences.Editor editor = prefs.edit();
        Button btn = (Button) findViewById(R.id.testbutton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                **** for single item
//                int p = listView.getCheckedItemPosition();
//                if(p!=ListView.INVALID_POSITION) {
//                    String s = ((TextView) listView.getChildAt(p)).getText().toString();
//                    Toast.makeText(getFriendList.this, "Selected item is " + s, Toast.LENGTH_LONG).show();
//                }else{
//                    Toast.makeText(getFriendList.this, "Nothing Selected..", Toast.LENGTH_LONG).show();
//                }
                SparseBooleanArray sp = listView.getCheckedItemPositions();
                StringBuffer str = new StringBuffer();
                Set<String> allPreferences = new HashSet<String>();
                for (int i = 0; i < sp.size(); i++) {
                    if (sp.valueAt(i)) {
                        String s = ((TextView) listView.getChildAt(i)).getText().toString();
                        allPreferences.add(s);
                        str = str.append(" ").append(s);
                    }
                }
                // save preferences in sharedPreferences
                editor.putStringSet("allPreferences", allPreferences);
                editor.apply();

                Toast.makeText(GetPreferences.this, "Selected items are " + str.toString(), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(GetPreferences.this, MoreMeetingInformation.class);

                editor.putStringSet("allPreferences", allPreferences);
                editor.apply();

                //Set<String> invitedFriends = prefs.getStringSet("invitedFriendList", null);

                //Toast.makeText(GetPreferences.this, "Invited friends are " + invitedFriends.toString(), Toast.LENGTH_LONG).show();
                startActivity(intent);

            }

        });
    }
}
