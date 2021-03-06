package team1008.b17.cs4518.wpi.pinderapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import team1008.b17.cs4518.wpi.pinderapp.request_handler.FirebaseRequestHandler;
import team1008.b17.cs4518.wpi.pinderapp.request_handler.RequestHandler;


/**
 * Created by selph on 12/14/17.
 */

public class ProjectPagerFragment extends Fragment {

    private static final String EXTRA_CRIME_ID =
            "com.bignerdranch.android.criminalintent.crime_id";

    private ViewPager mViewPager;
    private FragmentStatePagerAdapter mAdapter;
    private List<String> projectList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.project_pager, container, false);

        mViewPager = (ViewPager) v.findViewById(R.id.project_view_pager);

        FragmentManager fragmentManager = getFragmentManager();
        mAdapter = new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                return MatcherInfoFragment.newInstance(projectList.get(position));
            }

            @Override
            public int getCount() {
                return projectList.size();
            }
        };
        mViewPager.setAdapter(mAdapter);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getContext());
        database.getReference("users/" + acct.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Location uLocation = new Location("");
                uLocation.setLatitude(dataSnapshot.child("latitude").getValue(Double.class));
                uLocation.setLongitude(dataSnapshot.child("longitude").getValue(Double.class));
                final int maxDistance = dataSnapshot.child("search_distance").getValue(Integer.class);
                database.getReference("projects").addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        final Location pLocation = new Location("");
                        pLocation.setLatitude(dataSnapshot.child("latitude").getValue(Double.class));
                        pLocation.setLongitude(dataSnapshot.child("longitude").getValue(Double.class));
                        // check if this project is not the one you created yourself
                        // as well that it's actually close to you
                        // in a real app this should all be done server side
                        if (!dataSnapshot.child("creator").getValue(String.class).equals(acct.getId()) &&
                                uLocation.distanceTo(pLocation) < maxDistance*1000) {
                            projectList.add(dataSnapshot.getKey());
                            System.out.println("Found one!");
                        }
                        System.out.println(uLocation.distanceTo(pLocation));

                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        return v;
    }
}
