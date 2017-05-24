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
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ItemThreeFragment extends Fragment {
    private EditText entertainment, study, coding;
    private Button save;
    private static DatabaseReference mDatabaseRef;
    public static ItemThreeFragment newInstance() {
        ItemThreeFragment fragment = new ItemThreeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_item_three, container, false);
        initComponent(v);
        return v;
    }

    public void initComponent(View v){
        entertainment = (EditText) v.findViewById(R.id.entertainmentP);
        study = (EditText) v.findViewById(R.id.studyingP);
        coding = (EditText) v.findViewById(R.id.codinP);
        save = (Button) v.findViewById(R.id.saveGoal);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateGoal();
            }
        });

        getLastGoal();
    }

    public void updateGoal() {
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("goal");
        mDatabaseRef.setValue(null);
        final HashMap<String, String> map1 = new HashMap<>();
        map1.put("entertainment", entertainment.getText().toString());
        map1.put("studying", study.getText().toString());
        map1.put("coding", coding.getText().toString());
        mDatabaseRef.push().setValue(map1);

        getLastGoal();
    }

    public void getLastGoal() {
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("goal");
        mDatabaseRef.addListenerForSingleValueEvent(
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

        ArrayList<String> goal1 = new ArrayList<>();
        ArrayList<String> goal2 = new ArrayList<>();
        ArrayList<String> goal3 = new ArrayList<>();

        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : users.entrySet()){
            //Get user map
            Map singleUser = (Map) entry.getValue();
            //Get phone field and append to list
            goal1.add((String) singleUser.get("entertainment"));
            goal2.add((String) singleUser.get("studying"));
            goal3.add((String) singleUser.get("coding"));
        }

        entertainment.setText(goal1.get(0));
        study.setText(goal2.get(0));
        coding.setText(goal3.get(0));
    }
}
