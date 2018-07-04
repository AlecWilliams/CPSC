package net.jsaistudios.cpsc.cpsc;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    FirebaseFirestore db;
    ArrayList<String> places;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ArrayList<String> locationNames = new ArrayList<>();
        final ArrayList<String> locationInfo = new ArrayList<>();

        recyclerView = findViewById(R.id.message_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        final MessageRecyclerViewAdapter adapter = new MessageRecyclerViewAdapter(this,locationNames,locationInfo);
        recyclerView.setAdapter(adapter);
        places = new ArrayList<>();
        FirebaseApp.initializeApp(this);

        db = FirebaseFirestore.getInstance();


        //Button Stuff
        final Button editButtom = (Button) findViewById(R.id.edit_button);
        editButtom.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click

                Log.i("ButtonStuff","Button has been pressed!!");

            }
        });

        //Pull perks from db
        db.collection("perks").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    List<DocumentSnapshot> s = task.getResult().getDocuments();
                    Map<String,Object> m = s.get(0).getData();
                    Set<String> locations = m.keySet();

                    //Iterate through each entry in the list of perks
                    Iterator<String> locationIterator = locations.iterator();
                    while(locationIterator.hasNext()){
                        String location = locationIterator.next();
                        locationNames.add(location);
                        locationInfo.add(m.get(location).toString());
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }



}
