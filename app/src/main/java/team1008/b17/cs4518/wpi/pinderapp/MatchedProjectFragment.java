package team1008.b17.cs4518.wpi.pinderapp;


import android.graphics.BitmapFactory;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MatchedProjectFragment extends Fragment {

    List<String> projectList;
    RecyclerView mProjectsRecyclerView;
    MatchedProjectFragment.ProjectAdapter mAdapter;
    public MatchedProjectFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        projectList = new ArrayList<>();
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getActivity());
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference("users/" + acct.getId() + "/match").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                projectList.add(dataSnapshot.child("projectId").getValue(String.class));
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.matched_projects, container, false);
        mProjectsRecyclerView = v.findViewById(R.id.matched_project_view);
        mProjectsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new MatchedProjectFragment.ProjectAdapter();
        mProjectsRecyclerView.setAdapter(mAdapter);
        return v;
    }

    private class ProjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView mProjectTitle;
        TextView mProjectManager;
        ImageView mPhotoView;
        String projectId;
      
        public ProjectHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_match, parent, false));
            itemView.setOnClickListener(this);
            mProjectTitle = itemView.findViewById(R.id.project_title);
            mProjectManager = itemView.findViewById(R.id.project_manager);
            mPhotoView = itemView.findViewById(R.id.project_image);
        }

        public void bind(String projectId) {
            this.projectId = projectId;
            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            database.getReference("projects/" + projectId + "/project_name").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mProjectTitle.setText(dataSnapshot.getValue(String.class));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            database.getReference("projects/" + projectId + "/creator").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String creator = dataSnapshot.getValue(String.class);
                    database.getReference("users/" + creator + "/name").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot2) {
                            mProjectManager.setText(dataSnapshot2.getValue(String.class));
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
            storage.getReference("projects/" + projectId).getBytes(1024*1024*10).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    mPhotoView.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));

                }
            });
        }

        @Override
        public void onClick(View v) {
            Intent i = new Intent(getContext(), SeekerPagerActivity.class);
            i.putExtra("projectId", projectId);
            startActivity(i);
        }
    }
    private class ProjectAdapter extends RecyclerView.Adapter<MatchedProjectFragment.ProjectHolder> {
        @Override
        public MatchedProjectFragment.ProjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new MatchedProjectFragment.ProjectHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(MatchedProjectFragment.ProjectHolder holder, int position) {
            holder.bind(projectList.get(position));
        }

        @Override
        public int getItemCount() {
            return projectList.size();
        }
    }

}
