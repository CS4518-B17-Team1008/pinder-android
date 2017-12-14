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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * to handle interaction events.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {

    EditText mEditName;
    EditText mEditPhone;
    EditText mEditLocation;
    Button mRequestLocation;
    CheckBox mSeeker;
    CheckBox mOwner;
    Button mApply;
    SeekBar mDistance;
    // this variable controls whether we should trigger the location editor's listener
    boolean mShouldTrigger = true;
    // location services
    // private FusedLocationProviderClient mFusedLocationClient;
    private Geocoder gcd;
    private static final int REQUEST_LOCATION = 1;

    private double latitude = 0;
    private double longitude = 0;
    public SettingsFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(GoogleSignInAccount account) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
        gcd = new Geocoder(getContext(), Locale.getDefault());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.account_prefs, container, false);
        mEditName = v.findViewById(R.id.editName);
        mEditPhone = v.findViewById(R.id.editPhone);
        mEditLocation = v.findViewById(R.id.editLocation);
        mEditLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mShouldTrigger) {
                    getLocation();
                }
            }
        });
        mRequestLocation = v.findViewById(R.id.requestLocation);
        mRequestLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditLocation.setText("");
                // getLocation();
            }
        });
        mSeeker = v.findViewById(R.id.pSeeker);
        mOwner = v.findViewById(R.id.pOwner);
        mApply = v.findViewById(R.id.apply);
        mApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                apply();
            }
        });
        mDistance = v.findViewById(R.id.seekBar);
        return v;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                       String permissions[], int[] grantResults) {

        if (requestCode == REQUEST_LOCATION) {
            if(grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                // Permission was denied or request was cancelled
                Snackbar.make(getView(), "ERROR: Cannot get location permission", 2).show();
            }
        }

    }

    // get current location
    public void getLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Check Permissions Now
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_LOCATION);
        }
        else { // Permission granted
            mShouldTrigger = false;
            if (mEditLocation.getText().length() == 0) { // get current location
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
                                    mEditLocation.setText(addresses.get(0).getLocality() +
                                    ", " + addresses.get(0).getAdminArea() +
                                    ", " + addresses.get(0).getCountryName());
                                }
                            } catch (IOException e) {
                                Snackbar.make(getView(), "WARNING: Cannot retrieve city name from current location", 2).show();
                                mEditLocation.setText("");
                            }

                        }
                        mShouldTrigger = true;
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
            else { // get location entered
                try {
                    List<Address> addresses = gcd.getFromLocationName(mEditLocation.getText().toString(), 1);
                    if (!addresses.isEmpty()) {
                        latitude = addresses.get(0).getLatitude();
                        longitude = addresses.get(0).getLongitude();
                    }
                } catch (IOException e) {
                    Snackbar.make(getView(), "ERROR: Cannot retrieve location from inputted text", 2).show();
                    mEditLocation.setText("");
                }
                mShouldTrigger = true;
            }
        }
    }

    // save and submit data
    public void apply() {
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getActivity());
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        if (acct != null) {
            DatabaseReference myRef = database.getReference("users").child(acct.getId());
            myRef.child("name").setValue(mEditName.getText().toString());
            myRef.child("phone").setValue(mEditPhone.getText().toString());
            myRef.child("is_seeker").setValue(mSeeker.isChecked());
            myRef.child("is_owner").setValue(mOwner.isChecked());
            myRef.child("latitude").setValue(latitude);
            myRef.child("longitude").setValue(longitude);
            myRef.child("search_distance").setValue(mDistance.getProgress());
        }
    }
}
