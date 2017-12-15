package team1008.b17.cs4518.wpi.pinderapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ManagedProjectFragment extends Fragment {

    List<String> projectList;

    public ManagedProjectFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        projectList = new ArrayList<>();
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getActivity());
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference("users/" + acct.getId() + "/leadprojects").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                projectList.add(dataSnapshot.child("projectId").getValue(String.class));
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
        View v = inflater.inflate(R.layout.managed_projects, container, false);

        return v;
    }

    private class ProjectHolder extends RecyclerView.ViewHolder  {

        TextView mProjectTitle;
        TextView mProjectManager;
        public ProjectHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_match, parent, false));
            mProjectTitle = itemView.findViewById(R.id.project_title);
            mProjectManager = itemView.findViewById(R.id.project_manager);
        }

        public void bind(String projectId) {
            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            database.getReference("projects/" + projectId + "/title").addValueEventListener(new ValueEventListener() {
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
        }

    }
    private class ProjectAdapter extends RecyclerView.Adapter<ManagedProjectFragment.ProjectHolder> {
        @Override
        public ManagedProjectFragment.ProjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new ManagedProjectFragment.ProjectHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(ManagedProjectFragment.ProjectHolder holder, int position) {
            holder.bind(projectList.get(position));
        }

        @Override
        public int getItemCount() {
            return projectList.size();
        }
    }

}
