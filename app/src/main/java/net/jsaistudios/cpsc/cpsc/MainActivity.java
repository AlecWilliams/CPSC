package net.jsaistudios.cpsc.cpsc;

import android.app.Fragment;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.alexvasilkov.foldablelayout.UnfoldableView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    FirebaseDatabase db;
    ArrayList<String> places;
    List<PerkModel> s = new ArrayList<>();

    public DatabaseReference getMyRef() {
        return myRef;
    }

    DatabaseReference myRef;
    View mListTouchInterceptor;
    FrameLayout mDetailsLayout;
    UnfoldableView mUnfoldableView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ArrayList<String> locationNames = new ArrayList<>();
        final ArrayList<String> locationInfo = new ArrayList<>();

        recyclerView = findViewById(R.id.message_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        final MessageRecyclerViewAdapter adapter = new MessageRecyclerViewAdapter(this,s);
        recyclerView.setAdapter(adapter);
        places = new ArrayList<>();
        FirebaseApp.initializeApp(this);
        SlidingUpPanelLayout slidingUpPanelLayout = findViewById(R.id.sliding_layout);
        slidingUpPanelLayout.setAnchorPoint(0.6f);
        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);

        db = FirebaseDatabase.getInstance();
        myRef = db.getReference("perks");
        createMapboxFragment(R.id.map_holder, "mapFrag");

        //Button Stuff
        final ImageView editButtom = findViewById(R.id.edit_button);
        editButtom.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                //Fragment stuff


                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                PerkAddFragment enterNewPerkFragment = new PerkAddFragment();
                fragmentTransaction.add(R.id.main_frame, enterNewPerkFragment);
                fragmentTransaction.commit();


                //DatabaseReference dbr = myRef.child();
                //enterNewPerkFragment.addPerk(myRef);

                //if(enterNewPerkFragment.getLocationName() == null)
                  //  Log.i("AssTits","It failed and cam back null");
                //dbr.child("name").setValue(enterNewPerkFragment.getLocationName());
                //dbr.child("info").setValue(enterNewPerkFragment.getLocationInfo());

                Log.d("ButtonStuff","Button has been pressed!!");

            }
        });


        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                //String value = dataSnapshot.getValue(String.class);


                Iterable<DataSnapshot> iterable = dataSnapshot.getChildren();
                s.clear();
                int i=0;
                while(iterable.iterator().hasNext()){
                    PerkModel pm = new PerkModel();
                    DataSnapshot child = iterable.iterator().next();
                    pm.setPerkDatabaseNode(child);
                    pm.setName(child.child("name").getValue(String.class));
                    pm.setInfo(child.child("info").getValue(String.class));
                    if(i==0){
                        pm.setImage(R.drawable.fireston_img);
                    } else if(i==1){
                        pm.setImage(R.drawable.bulls);
                    } else if(i==2) {
                        pm.setImage(R.drawable.fireston_img);
                    } else if(i==4) {
                        pm.setImage(R.drawable.slobrew);
                    } else {
                        pm.setImage(R.drawable.fireston_img);
                    }
                    s.add(pm);
                    i++;

                }

                //Notes=================================
                //Make fragment, put in frame layout.
                //Edit button - remove / add
                //input field
                //Add navigation to location
                //


                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });


    }


    private MapboxFragmentViewModel createMapboxFragment(int holder, String tag) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        MapboxFragmentViewModel fragment = new MapboxFragmentViewModel();
        fragment.listener = new MapListener() {
            @Override
            public void locationUpdated(LatLng loc) {
            }
        };
        fragment.mainContext = this;
        fragmentTransaction.add(holder, fragment, tag);
        fragmentTransaction.commitAllowingStateLoss();
        return fragment;
    }




}
