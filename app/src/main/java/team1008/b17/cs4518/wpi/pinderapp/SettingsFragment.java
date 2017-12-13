package team1008.b17.cs4518.wpi.pinderapp;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

/**
 * A simple {@link Fragment} subclass.
 * to handle interaction events.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {

    EditText mEditName;
    EditText mEditEmail;
    EditText mEditPhone;
    Button mRequestLocation;
    // location services
    // private FusedLocationProviderClient mFusedLocationClient;
    private static final int REQUEST_LOCATION = 1;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.account_prefs, container, false);
        mEditName = v.findViewById(R.id.editName);
        mEditEmail = v.findViewById(R.id.editEmail);
        mEditPhone = v.findViewById(R.id.editPhone);
        mRequestLocation = v.findViewById(R.id.requestLocation);
        mRequestLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();
            }
        });
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
            }
        }

    }

    public void getLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Check Permissions Now
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_LOCATION);
        }
        else { // Permission granted
            LocationServices.getFusedLocationProviderClient(getActivity())
                    .getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {

                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                System.out.println("LAT:" + location.getLatitude());
                                System.out.println("LONG:" + location.getLongitude());
                            }
                        }
                    });

        }

    }
}
