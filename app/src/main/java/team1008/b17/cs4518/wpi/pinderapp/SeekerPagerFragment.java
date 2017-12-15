package team1008.b17.cs4518.wpi.pinderapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by selph on 12/14/17.
 */

public class SeekerPagerFragment extends Fragment {

    private static final String EXTRA_CRIME_ID =
            "com.bignerdranch.android.criminalintent.crime_id";

    private ViewPager mViewPager;
    private List<String> userList = new ArrayList<>();
    String projectId;

    public static SeekerPagerFragment newInstance(String projectId) {
        SeekerPagerFragment fragment = new SeekerPagerFragment();
        Bundle args = new Bundle();
        args.putString("projectId", projectId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            projectId = getArguments().getString("projectId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.seeker_pager, container, false);

        mViewPager = (ViewPager) v.findViewById(R.id.seeker_view_pager);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference("projects/" + projectId + "/potentialmatch").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                userList.add(dataSnapshot.child("userId").getValue(String.class));
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

        FragmentManager fragmentManager = getFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {

            @Override
            public Fragment getItem(int position) {
                return SeekerInfoFragment.newInstance(userList.get(position), projectId);
            }

            @Override
            public int getCount() {
                return userList.size();
            }
        });
        return v;
    }
}
