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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProjectFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ProjectFragment extends Fragment {

    private TextView name_box;
    private TextView status_box;
    private TextView location_box;
    private EditText description_box;
    private EditText contact_info_box;
    private TextView members_box;

    private Geocoder gcd;
    private static final int REQUEST_LOCATION = 1;

    private double latitude = 0;
    private double longitude = 0;

    public ProjectFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.managed_info, container, false);

        name_box = v.findViewById(R.id.projectName);
        status_box = v.findViewById(R.id.projectStatus);
        location_box = v.findViewById(R.id.locationCoord);
        description_box = v.findViewById(R.id.edit_description);
        contact_info_box = v.findViewById(R.id.edit_contactInfo);
        members_box = v.findViewById(R.id.members);

        v.findViewById(R.id.requestLocation2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Get location clicked");
                getLocation(view);
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

    public void apply() {
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getActivity());
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        if (acct != null) {
            DatabaseReference myRef = database.getReference("users").child(acct.getId()).child("projects").push();
            myRef.child("project_name").setValue(name_box.getText().toString());
            myRef.child("status").setValue(status_box.getText().toString());
            myRef.child("description").setValue(description_box.getText(), toString());
            myRef.child("contact_info").setValue(contact_info_box.getText().toString());
            myRef.child("latitude").setValue(latitude);
            myRef.child("longitude").setValue(longitude);
            myRef.child("members").setValue(members_box.getText().toString());
        }
    }

    // get current location
    public void getLocation(View v) {
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Check Permissions Now
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_LOCATION);
        } else { // Permission granted
            Task locationTask = LocationServices.getFusedLocationProviderClient(getActivity())
                    .getLastLocation();
            locationTask.addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
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


                    }
                }
            });

            locationTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Snackbar.make(getView(), "ERROR: Cannot get your current location", 2).show();
                    mShouldTrigger = true;
                }
            });
        }
    }

}
