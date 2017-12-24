package com.example.iris.meetmehalfway;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by kanishk on 12/18/2017.
 */
public class TasksPagerFragment extends FragmentActivity{

    static final int NUM_ITEMS = 2;
    static List<String> pendingTasks = new ArrayList<>();
    static List<String> acceptedTasks = new ArrayList<>();

    MyAdapter mAdapter;

    ViewPager mPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_pager);

        SharedPreferences prefs = getSharedPreferences("Context", MODE_PRIVATE);
        final String userID = prefs.getString("UserID", null);
        new GetUserTasks().execute(userID);
        mAdapter = new MyAdapter(getSupportFragmentManager());

        mPager = (ViewPager)findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);

        // Watch for button clicks.
        Button button = (Button)findViewById(R.id.goto_first);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mPager.setCurrentItem(0);
            }
        });
        button = (Button)findViewById(R.id.goto_last);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mPager.setCurrentItem(NUM_ITEMS-1);
            }
        });
    }

    public static class MyAdapter extends FragmentPagerAdapter {
        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem(int position) {
            return ArrayListFragment.newInstance(position);
        }

        @Override
        public int getItemPosition(Object object){
            return POSITION_NONE;
        }
    }

    public static class ArrayListFragment extends ListFragment {
        int mNum;
        static String[] titles = new String[] {"Pending", "Accepted"};

        /**
         * Create a new instance of CountingFragment, providing "num"
         * as an argument.
         */
        static ArrayListFragment newInstance(int num) {
            ArrayListFragment f = new ArrayListFragment();

            // Supply num input as an argument.
            Bundle args = new Bundle();
            args.putInt("num", num);
            args.putStringArray("titles", titles);
            f.setArguments(args);

            return f;
        }

        /**
         * When creating, retrieve this instance's number from its arguments.
         */
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mNum = getArguments() != null ? getArguments().getInt("num") : 1;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_pager_list, container, false);
            View tv = v.findViewById(R.id.text);
            ((TextView)tv).setText(titles[mNum]);
            return v;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            setListAdapter(new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_list_item_1, mNum==0?pendingTasks:acceptedTasks));
        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            Log.i("FragmentList", "Item clicked: " + id);
            TextView tv = (TextView) l.getChildAt(position);
            String meetingID = tv.getText().toString();
            Intent intent = new Intent(getActivity(), MeetingInfo.class);
            intent.putExtra("meetingID", meetingID);
            startActivity(intent);
        }
    }

    public class GetUserTasks extends AsyncTask<String, Void, List<List<String>>> {

        @Override
        protected List<List<String>> doInBackground(String... strings) {
            String userID = strings[0];
            Log.d("asyncLoad", userID);
            List<List<String>> result = new LinkedList<>();
            CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                    getApplicationContext(),    /* get the context for the application */
                    "*********************",    /* Identity Pool ID */
                    Regions.US_EAST_1           /* Region for your identity pool--US_EAST_1 or EU_WEST_1*/
            );
            AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
            DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);
            MeetingStatusDDBModel user = mapper.load(MeetingStatusDDBModel.class, userID);
            if (user != null) {
                result.add(user.getPendingInvitations());
                result.add(user.getAcceptedInvitations());
                Log.d("asyncLoad",  result.toString());
                return result;
            }
            Log.d("asyncLoad", "no data");
            return null;
        }
        @Override
        protected void onPostExecute (List<List<String>> result) {
            if (result==null){
                return;
            }
            pendingTasks = result.get(0);
            acceptedTasks = result.get(1);
            mAdapter.notifyDataSetChanged();
        }


    }
}
