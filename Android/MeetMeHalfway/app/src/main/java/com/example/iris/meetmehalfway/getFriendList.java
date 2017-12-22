package com.example.iris.meetmehalfway;

/**
 * Created by Iris on 12/12/17.
 */

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
import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class getFriendList extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friendlist);

        Intent intent = getIntent();
        String jsondata = intent.getStringExtra("jsondata");
        Bundle extras = getIntent().getExtras();
        JSONArray friendslist;
        ArrayList<String> friends = new ArrayList<String>();
        final Map<String, String> friendsMap = new HashMap<>();
        try {
            friendslist = new JSONArray(jsondata);
            for (int l=0; l < friendslist.length(); l++) {
                friends.add(friendslist.getJSONObject(l).getString("name"));
                friendsMap.put(friendslist.getJSONObject(l).getString("name"), friendslist.getJSONObject(l).getString("id"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.content_friend_list, friends); // simple textview for list item
        final ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,friends));
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView lv = (ListView)parent;
                TextView tv = (TextView) lv.getChildAt(position);
                String s = tv.getText().toString();
                Toast.makeText(getFriendList.this, "Clicked item is "+s, Toast.LENGTH_LONG).show();
            }
        });
        Button btn = (Button) findViewById(R.id.testbutton);

        // create shared preferences
        SharedPreferences prefs = getSharedPreferences("Context", MODE_PRIVATE);
        final SharedPreferences.Editor editor = prefs.edit();

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
                Set<String> allInvitedUser = new HashSet<String>();

                StringBuffer str = new StringBuffer();
                for(int i=0;i<sp.size();i++){
                    if(sp.valueAt(i)){
                        String s = ((TextView) listView.getChildAt(i)).getText().toString();
                        String id = friendsMap.get(s);
                        allInvitedUser.add(id);
                        str = str.append(" ").append(id);
                    }
                }
                editor.putStringSet("invitedFriendList", allInvitedUser);
                editor.apply();
                Toast.makeText(getFriendList.this, "Selected items are "+str.toString(), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getFriendList.this, GetPreferences.class);
                startActivity(intent);

            }
        });

    }

}
