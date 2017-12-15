package team1008.b17.cs4518.wpi.pinderapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Locale;

public class ProjectFragment extends Fragment {

    private EditText name_box;
    private EditText status_box;
    private TextView location_box;
    private EditText description_box;
    private EditText contact_info_box;
    private TextView members_box;
    private ImageView photo;

    private Geocoder gcd;
    private static final int REQUEST_LOCATION = 1;

    private double latitude = 0;
    private double longitude = 0;

    private static final int RESULT_LOAD_IMG = 1;
    private Uri imageUri;

    String projectId;

    public ProjectFragment() {

    }

    boolean changingData = true;

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
            changingData = false;
        }

        name_box = v.findViewById(R.id.projectName);
        status_box = v.findViewById(R.id.projectStatus);
        location_box = v.findViewById(R.id.locationCoord);
        description_box = v.findViewById(R.id.edit_description);
        contact_info_box = v.findViewById(R.id.edit_contactInfo);
        members_box = v.findViewById(R.id.members);
        photo = v.findViewById(R.id.photo);

        if(getArguments() != null) {
            projectId = getArguments().getString("PROJECT_KEY");
            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getActivity());
            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            if (acct != null) {
                DatabaseReference myRef;
                StorageReference imageRef;

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
                        changingData = false;
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                imageRef = storage.getReference("projects/" + projectId);
                imageRef.getBytes(1024*1024*10).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        photo.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
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


        name_box.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                apply();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        status_box.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                apply();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        location_box.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                apply();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        description_box.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                apply();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        contact_info_box.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                apply();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        members_box.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                apply();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        members_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), SeekerPagerActivity.class);
                i.putExtra("projectId", projectId);
                startActivity(i);
            }
        });

        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
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
        if(changingData ) return;
        System.out.println("Sending info");
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getActivity());
        FirebaseStorage storage = FirebaseStorage.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        if (acct != null) {
            DatabaseReference myRef;
            StorageReference imageRef;
            if(projectId == null) {
                myRef = database.getReference("projects").push();
                projectId = myRef.getKey();
                System.out.println("NEW PROJ KEY: " + myRef.getKey());
            } else {
                myRef = database.getReference("projects").child(projectId);
                System.out.println("OLD KEY: " + myRef.getKey());
            };
            imageRef = storage.getReference("projects").child(projectId);
            if(imageUri != null){
                imageRef.putFile(imageUri);
            }
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

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        if (reqCode == RESULT_LOAD_IMG) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    imageUri = data.getData();
                    final InputStream imageStream = getActivity().getApplicationContext().getContentResolver().openInputStream(imageUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    photo.setImageBitmap(selectedImage);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Snackbar.make(photo, "ERROR: Cannot read image", 2).show();
                }

            } else {
                Snackbar.make(photo, "WARNING: No image picked", 2).show();
            }
        }
    }

}
