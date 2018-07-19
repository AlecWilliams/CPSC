package net.jsaistudios.cpsc.cpsc;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;

import java.util.List;

/**
 * Created by Alec on 5/18/2018.
 */

public class MessageRecyclerViewAdapter extends RecyclerView.Adapter<MessageRecyclerViewAdapter.ViewHolder> {

    private List<PerkModel> mData;
    private LayoutInflater mInflater;
    private ImageButton deleteButton;

    // data is passed into the constructor
    MessageRecyclerViewAdapter(Context context, List<PerkModel> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;

    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.message_fragment_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MessageRecyclerViewAdapter.ViewHolder holder, int position) {
        String animal = mData.get(position).getName();

        holder.myLocationInfo.setText(mData.get(position).getInfo());
        holder.myTextView.setText(animal);
        holder.myDatabaseRef = mData.get(position).getPerkDatabaseNode();
    }

    // binds the data to the TextView in each row

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder  {
        TextView myTextView;
        TextView myLocationInfo;
        ImageView myDeleteButton;
        DataSnapshot myDatabaseRef;
        ImageView myEditButton;
        EditText editPerkName, editPerkInfo, editPerkAddress;
        LinearLayout editLayout;
        LinearLayout perksLayout;
        ImageView myPerkImage;
        ImageView myEditImage, myEditSave, myEditCancel;


        ViewHolder(final View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.perk_name);
            myLocationInfo = itemView.findViewById(R.id.perk_description);
            myDeleteButton = itemView.findViewById(R.id.delete_perk);
            myEditButton = itemView.findViewById(R.id.edit_perk);

            editPerkName = itemView.findViewById(R.id.edit_perk_name);
            editPerkInfo = itemView.findViewById(R.id.edit_perk_info);
            editPerkAddress = itemView.findViewById(R.id.edit_perk_address);
            editLayout = itemView.findViewById(R.id.edit_layout_holder);

            perksLayout = itemView.findViewById(R.id.perkInfoHolder);
            myPerkImage = itemView.findViewById(R.id.location_image);
            myEditImage = itemView.findViewById(R.id.location_image2);

            myEditSave = itemView.findViewById(R.id.save_edit);
            myEditCancel = itemView.findViewById(R.id.cancel_edit);


            //Edit Perk Button Listener
            myEditButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ViewGroup.LayoutParams params = myEditButton.getLayoutParams();
                    myEditButton.setLayoutParams(params);

                    editPerkName.setText(myTextView.getText());
                    editPerkInfo.setText(myLocationInfo.getText());

                    editLayout.setVisibility(View.VISIBLE);
                    perksLayout.setVisibility(View.GONE);
                    myPerkImage.setVisibility(View.GONE);
                    //myEditImage.setAlpha(0);
                    myEditImage.setVisibility(View.VISIBLE);

                    myEditButton.setVisibility(View.GONE);
                    RelativeLayout.LayoutParams editImageParams = (RelativeLayout.LayoutParams) myPerkImage.getLayoutParams();



                }
            });

            //Cancel Edit
            myEditCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Make edit layout gone
                    editLayout.setVisibility(View.GONE);
                    myEditImage.setVisibility(View.GONE);
                    //Make default perk info visible
                    myPerkImage.setVisibility(View.VISIBLE);
                    perksLayout.setVisibility(View.VISIBLE);
                    myEditButton.setVisibility(View.VISIBLE);
                 }
            });

            //Save Edit
            myEditSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Save changes to database
                    String newPerkName = editPerkName.getText().toString();
                    myDatabaseRef.getRef().child("name").setValue(editPerkName.getText().toString());
                    myDatabaseRef.getRef().child("info").setValue(editPerkInfo.getText().toString());

                    //Make edit layout gone
                    editLayout.setVisibility(View.GONE);
                    myEditImage.setVisibility(View.GONE);
                    //Make default perk info visible
                    myPerkImage.setVisibility(View.VISIBLE);
                    perksLayout.setVisibility(View.VISIBLE);
                    myEditButton.setVisibility(View.VISIBLE);

                }
            });


            //Delete Perk Button Listener
            myDeleteButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {

                    if(myDatabaseRef != null){
                        myDatabaseRef.getRef().removeValue();
                    }
                }
            });



        }

    }

    // convenience method for getting data at click position

}
