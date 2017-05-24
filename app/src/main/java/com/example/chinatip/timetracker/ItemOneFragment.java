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

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class ItemOneFragment extends Fragment {
    private DatabaseReference mDatabaseRef;
    private ToggleButton toggle;
    private TextView status, email;
    private Button entertain, study, coding, signout;
    ArrayList<String> timestampLogs, statusLogs;
    private FirebaseAuth mAuth;

    public static ItemOneFragment newInstance() {
        ItemOneFragment fragment = new ItemOneFragment();
        return fragment;
    }

    public void initComponent(View v) {
        entertain = (Button) v.findViewById(R.id.entertain);
        study = (Button) v.findViewById(R.id.study);
        coding = (Button) v.findViewById(R.id.coding);
        toggle = (ToggleButton) v.findViewById(R.id.toggle);
        status = (TextView) v.findViewById(R.id.status);
        email = (TextView) v.findViewById(R.id.displayEmail);
        signout = (Button) v.findViewById(R.id.signout);

        entertain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pushStatus("Entertainment");
                updateStatus("Entertainment");
            }
        });

        study.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pushStatus("Studying");
                updateStatus("Studying");
            }
        });

        coding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pushStatus("Coding");
                updateStatus("Coding");
            }
        });

        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                pushStatus(isChecked + "");
                updateStatus(isChecked + "");
            }
        });
        loadLastStatus();

        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), SignIn.class);
                getActivity().finish();
                startActivity(i);
            }
        });


    }

    public void pushStatus(String s) {
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT+7:00"));
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c.getTime());

        Long tsLong = System.currentTimeMillis() / 1000;
        String ts = tsLong.toString();

        mDatabaseRef = FirebaseDatabase.getInstance().getReference(formattedDate);
        final HashMap<String, String> map1 = new HashMap<>();
        map1.put("status", s);
        map1.put("timestamp", ts);
        mDatabaseRef.push().setValue(map1);

    }


    public void updateStatus(String s) {
        if(!(s.equals("true") || s.equals("false"))) {
            status.setText(s);
            if(!toggle.isChecked()) {
                toggle.setChecked(true);
            }
        } else {
            if(s.equals("true")) {
                status.setText("On");
                toggle.setChecked(true);
            } else {
                status.setText("Off");
                toggle.setChecked(false);
            }
        }
    }

    public void loadLastStatus() {
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT+7:00"));
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String todayDate = df.format(c.getTime());

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


    private void collectLogs(Map<String,Object> users) {

        timestampLogs = new ArrayList<>();
        statusLogs = new ArrayList<>();

        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : users.entrySet()){

            //Get user map
            Map singleUser = (Map) entry.getValue();
            //Get phone field and append to list
            statusLogs.add((String) singleUser.get("status"));
        }

        System.out.println("update : " + statusLogs.get(0));
        updateStatus(statusLogs.get(0));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_item_one, container, false);
        initComponent(v);
        return v;
    }
}
