/*
 * Copyright (c) 2017. Truiton (http://www.truiton.com/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 * Mohit Gupt (https://github.com/mohitgupt)
 *
 */

package com.example.chinatip.timetracker;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

public class ItemTwoFragment extends Fragment {
    private TextView ent, stu, cod, total, date;
    private DatabaseReference mDatabaseRef;
    ArrayList<String> timestampLogs, statusLogs;

    public static ItemTwoFragment newInstance() {
        ItemTwoFragment fragment = new ItemTwoFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_item_two, container, false);
        initComponent(v);
        return v;
    }

    public void initComponent(View v) {
        ent = (TextView) v.findViewById(R.id.entertainmentP);
        stu = (TextView) v.findViewById(R.id.studyingP);
        cod = (TextView) v.findViewById(R.id.codinP);
        date = (TextView) v.findViewById(R.id.date);
        total = (TextView) v.findViewById(R.id.total);

        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT+7:00"));
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String todayDate = df.format(c.getTime());

        date.setText(todayDate);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(todayDate);
        ref.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get map of users in datasnapshot
                        collectLogs((Map<String,Object>) dataSnapshot.getValue());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });
    }

    public void calculateTracks() {
        int temp = 0;
        long ent = 0;
        long stu = 0;
        long cod = 0;

        for(int i = statusLogs.size() - 1; i >= 0; i--) {
            System.out.println("Log " + i + " : " +timestampLogs.get(i));
            System.out.println("Log " + i + " : " +statusLogs.get(i));
            if(i == statusLogs.size() - 1) {
                temp = statusLogs.size() - 1;
                continue;
            }

            if(statusLogs.get(temp).equals(statusLogs.get(i)) || statusLogs.get(i).equals("true")) {
                continue;
            } else if(statusLogs.get(temp).equals("Entertainment")){
                ent += Long.parseLong(timestampLogs.get(i)) - Long.parseLong(timestampLogs.get(temp));

//                System.out.println("ent : " + ent);
//                System.out.println("ent : " + Long.parseLong(timestampLogs.get(i)) + " - "  + Long.parseLong(timestampLogs.get(temp)) + " = " +
//                        (Long.parseLong(timestampLogs.get(i)) - Long.parseLong(timestampLogs.get(temp))) );
            } else if(statusLogs.get(temp).equals("Studying")){
                stu += Long.parseLong(timestampLogs.get(i)) - Long.parseLong(timestampLogs.get(temp));

            } else if(statusLogs.get(temp).equals("Coding")){
                cod += Long.parseLong(timestampLogs.get(i)) - Long.parseLong(timestampLogs.get(temp));
            }

            temp = i;
        }

        double totalDiff = (Long.parseLong(timestampLogs.get(0)) - (Long.parseLong(timestampLogs.get(statusLogs.size()- 1))));
        double ent2 = ent / totalDiff *100;
        double stu2 = stu / totalDiff *100;
        double cod2 = cod / totalDiff *100;

        System.out.println("ent2 : " + (ent/totalDiff));

        this.ent.setText(ent2+"");
        this.stu.setText(stu2+"");
        this.cod.setText(cod2+"");

        String total2 = (new SimpleDateFormat("mm:ss:SSS")).format(new Date((int) totalDiff));
        this.total.setText(total2);
        System.out.println("totalDiff : " + totalDiff);

    }

    private void collectLogs(Map<String,Object> users) {
        timestampLogs = new ArrayList<>();
        statusLogs = new ArrayList<>();

        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : users.entrySet()){

            //Get user map
            Map singleUser = (Map) entry.getValue();
            //Get phone field and append to list
            timestampLogs.add((String) singleUser.get("timestamp"));
            statusLogs.add((String) singleUser.get("status"));
        }

        System.out.println(statusLogs.get(1));
        calculateTracks();
    }

}
