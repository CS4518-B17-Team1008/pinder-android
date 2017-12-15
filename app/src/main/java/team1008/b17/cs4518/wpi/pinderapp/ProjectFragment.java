package team1008.b17.cs4518.wpi.pinderapp;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ProjectFragment extends Fragment {

    private EditText name_box;
    private EditText status_box;
    private TextView location_box;
    private EditText description_box;
    private EditText contact_info_box;
    private TextView members_box;

    private Geocoder gcd;
    private static final int REQUEST_LOCATION = 1;

    private double latitude = 0;
    private double longitude = 0;

    String projectId;

    public ProjectFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.managed_info, container, false);
        gcd = new Geocoder(getContext(), Locale.getDefault());

        if (savedInstanceState != null) {
            projectId = savedInstanceState.getString("PROJECT_KEY");
        }
        else {
            projectId = null;
        }

        name_box = v.findViewById(R.id.projectName);
        status_box = v.findViewById(R.id.projectStatus);
        location_box = v.findViewById(R.id.locationCoord);
        description_box = v.findViewById(R.id.edit_description);
        contact_info_box = v.findViewById(R.id.edit_contactInfo);
        members_box = v.findViewById(R.id.members);

        if(getArguments() != null) {
            projectId = getArguments().getString("PROJECT_KEY");
            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getActivity());
            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            if (acct != null) {
                DatabaseReference myRef;

                myRef = database.getReference("projects").child(projectId);
                System.out.println("OLD KEY: " + myRef.getKey());

                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        name_box.setText(dataSnapshot.child("project_name").getValue().toString());
                        status_box.setText(dataSnapshot.child("status").getValue().toString());
                        description_box.setText(dataSnapshot.child("description").getValue().toString());
                        contact_info_box.setText(dataSnapshot.child("contact_info").getValue().toString());
                        members_box.setText(dataSnapshot.child("members").getValue().toString());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

        }

        v.findViewById(R.id.requestLocation2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Get location clicked");
                getLocation(view);
                apply();
            }
        });


        name_box.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                apply();
                return false;
            }
        });

        status_box.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                apply();
                return false;
            }
        });

        location_box.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                apply();
                return false;
            }
        });

        description_box.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                apply();
                return false;
            }
        });

        contact_info_box.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                apply();
                return false;
            }
        });

        members_box.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                apply();
                return false;
            }
        });

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (projectId != null) {
            outState.putString("PROJECT_KEY", projectId);
        }
    }

    public void apply() {
        System.out.println("Sending info");
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getActivity());
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        if (acct != null) {
            DatabaseReference myRef;
            if(projectId == null) {
                myRef = database.getReference("projects").push();
                projectId = myRef.getKey();
                System.out.println("NEW PROJ KEY: " + myRef.getKey());
            } else {
                myRef = database.getReference("projects").child(projectId);
                System.out.println("OLD KEY: " + myRef.getKey());
            };

            myRef.child("project_name").setValue(name_box.getText().toString());
            myRef.child("status").setValue(status_box.getText().toString());
            myRef.child("description").setValue(description_box.getText().toString());
            myRef.child("contact_info").setValue(contact_info_box.getText().toString());
            myRef.child("latitude").setValue(latitude);
            myRef.child("longitude").setValue(longitude);
            myRef.child("members").setValue(members_box.getText().toString());
            myRef.child("creator").setValue(acct.getId());
        }
    }

    // get current location
    public void getLocation(View v) {
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            System.out.println("Getting permission for location");
            // Check Permissions Now
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_LOCATION);
        } else { // Permission granted
            System.out.println("got permission for location");
            Task locationTask = LocationServices.getFusedLocationProviderClient(getActivity())
                    .getLastLocation();
            locationTask.addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        System.out.println("Latitude: " + latitude);
                        System.out.println("Longitude: " + longitude);
                        try {
                            List<Address> addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            if (!addresses.isEmpty()) {
                                // City, State, Country
                                location_box.setText(addresses.get(0).getLocality() +
                                        ", " + addresses.get(0).getAdminArea() +
                                        ", " + addresses.get(0).getCountryName());
                                System.out.println(addresses.get(0).getLocality() +
                                        ", " + addresses.get(0).getAdminArea() +
                                        ", " + addresses.get(0).getCountryName());
                            }
                        } catch (IOException e) {
                            Snackbar.make(getView(), "WARNING: Cannot retrieve city name from current location", 2).show();
                            location_box.setText("");
                        }


                    } else {
                        System.out.println("location null");
                    }
                }
            });

            locationTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Snackbar.make(getView(), "ERROR: Cannot get your current location", 2).show();
                }
            });
        }
    }

}
