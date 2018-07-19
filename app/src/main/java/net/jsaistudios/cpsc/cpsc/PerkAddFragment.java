package net.jsaistudios.cpsc.cpsc;

import android.app.Fragment;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;

import com.google.firebase.database.DatabaseReference;

/**
 * Created by Alec on 7/8/2018.
 */

public class PerkAddFragment extends android.support.v4.app.Fragment {

    Button addPerk;
    String locationName;
    String locationInfo;



    public String getLocationName() {
        return locationName;
    }

    public String getLocationInfo() {
        return locationInfo;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        showPopup(container);
        return null;

    }

    public void showPopup(View anchorView) {


        final View popupView = getLayoutInflater().inflate(R.layout.perk_add_fragment, null);

        final PopupWindow popupWindow = new PopupWindow(popupView,
                650, 550);

        // Initialize  widgets from `popup_layout.xml`

        // If the PopupWindow should be focusable
        popupWindow.setFocusable(true);

        // If you need the PopupWindow to dismiss when when touched outside
        popupWindow.setBackgroundDrawable(new ColorDrawable());

        int location[] = new int[2];

        // Get the View's(the one that was clicked in the Fragment) location
        popupWindow.showAtLocation(anchorView, Gravity.CENTER, 0, 0);


        addPerk = popupView.findViewById(R.id.addPerk);
        addPerk.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO
                //Add if statements to assure something was entered!!

                // Do something in response to button click
                locationName = ((EditText) popupView.findViewById(R.id.location)).getText().toString();
                locationInfo = ((EditText) popupView.findViewById(R.id.location_info)).getText().toString();

                //Close the window
                popupWindow.dismiss();
                addPerk(((MainActivity)getActivity()).getMyRef());

                Log.i("ButtSugar",getLocationName());
            }
        });


    }
    public void addPerk(DatabaseReference db){

        DatabaseReference dbr = db.push();

        dbr.child("name").setValue(locationName);
        dbr.child("info").setValue(getLocationInfo());

    }
}
