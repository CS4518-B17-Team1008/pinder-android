package team1008.b17.cs4518.wpi.pinderapp;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;

import team1008.b17.cs4518.wpi.pinderapp.request_handler.FirebaseRequestHandler;
import team1008.b17.cs4518.wpi.pinderapp.request_handler.RequestHandler;

/**
 * Created by selph on 12/14/17.
 */

public class MatcherInfoFragment extends Fragment {
    private String projectId;
    Button mAccept;
    Button mDeny;

    public MatcherInfoFragment() {

    }

    public static MatcherInfoFragment newInstance(String projectId) {
        MatcherInfoFragment fragment = new MatcherInfoFragment();
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
        final GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getActivity());
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        if (acct != null) {

        }
        View v = inflater.inflate(R.layout.matcher, container, false);
        final TextView location = v.findViewById(R.id.projectLocation);
        final TextView description = v.findViewById(R.id.description);
        final TextView title = v.findViewById(R.id.projectTitle);
        title.setText("TITLE");
        database.getReference("projects/" + projectId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                description.setText(dataSnapshot.child("description").getValue(String.class));
                double latitude = 0;
                double longitude = 0;
                if(dataSnapshot.child("latitude").getValue(Double.class) != null) {
                    latitude = dataSnapshot.child("latitude").getValue(Double.class);
                    longitude = dataSnapshot.child("longitude").getValue(Double.class);
                }
                Geocoder gcd = new Geocoder(getContext(), Locale.getDefault());
                try {
                    List<Address> addresses = gcd.getFromLocation(latitude, longitude, 1);
                    if (!addresses.isEmpty()) {
                        // City, State, Country
                        location.setText(addresses.get(0).getLocality() +
                                ", " + addresses.get(0).getAdminArea() +
                                ", " + addresses.get(0).getCountryName());
                    }
                } catch (IOException e) {
                    Snackbar.make(getView(), "WARNING: Cannot retrieve city name from project latitude and longitude", 2).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mAccept = v.findViewById(R.id.accept);
        mDeny = v.findViewById(R.id.deny);
        mAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Runnable runnable1 = new Runnable() {
                    @Override
                    public void run() {
                        RequestHandler rh = new FirebaseRequestHandler("https://us-central1-pinder-d3098.cloudfunctions.net/app/");
                        try {
                            JSONObject jo = new JSONObject();
                            jo = jo.put("Hello", "World");
                            JSONObject ret = rh.send(jo, "users/" + acct.getId() + "/match/" + projectId);
                        } catch (Exception e){
                            System.out.println("----ERROR----");
                            System.out.println(e);
                        }
                    }
                };
                Thread thread1 = new Thread(runnable1);
                thread1.start();
            }
        });
        mDeny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Runnable runnable1 = new Runnable() {
                    @Override
                    public void run() {
                        RequestHandler rh = new FirebaseRequestHandler("https://us-central1-pinder-d3098.cloudfunctions.net/app/");
                        try {
                            JSONObject jo = new JSONObject();
                            jo = jo.put("Hello", "World");
                            JSONObject ret = rh.send(jo, "users/" + acct.getId() + "/unmatch/" + projectId);
                        } catch (Exception e){
                            System.out.println("----ERROR----");
                            System.out.println(e);
                        }
                    }
                };
                Thread thread1 = new Thread(runnable1);
                thread1.start();
            }
        });

        database.getReference("users/" + acct.getId() + "/potentialmatch").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.child("projectId").getValue(String.class).equals(projectId)) {
                    mAccept.setText("Accepted");
                    mAccept.setEnabled(false);
                    mDeny.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("projectId").getValue(String.class).equals(projectId)) {
                    mAccept.setVisibility(View.INVISIBLE);
                    mDeny.setText("Unmatched");
                    mDeny.setEnabled(false);
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        database.getReference("users/" + acct.getId() + "/unmatch").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.child("projectId").getValue(String.class).equals(projectId)) {
                    mAccept.setVisibility(View.INVISIBLE);
                    mDeny.setText("Denied");
                    mDeny.setEnabled(false);
                }
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


        return v;
    }
}
