package net.jsaistudios.cpsc.cpsc;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
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
import com.ramotion.foldingcell.FoldingCell;

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
        holder.myPerkImage.setImageResource(mData.get(position).getImage());
        holder.myEditImage.setImageResource(mData.get(position).getImage());

        Bitmap icon = BitmapFactory.decodeResource(holder.myPerkImage.getContext().getResources(),
                mData.get(position).getImage());
        ;

//        Drawable drawable = (GradientDrawable) holder.myPerkImage.getContext().getResources().getDrawable(mData.get(position).getImage());
//        if(drawable!=null)
//            drawable.setStroke(2, getDominantColor(icon)); // set stroke width and stroke color

    }

    private static int getDominantColor(Bitmap bitmap) {
        Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, 1, 1, true);
        final int color = newBitmap.getPixel(0, 0);
        newBitmap.recycle();
        return color;
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
        EditText editPerkName, editPerkInfo;
        LinearLayout editLayout;
        RelativeLayout perksLayout;
        ImageView myPerkImage, myEditButton;
        ImageView myEditImage;
        TextView myEditSave, myEditCancel;
        View fragRoot;


        ViewHolder(final View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.perk_name);
            myLocationInfo = itemView.findViewById(R.id.perk_description);
            myDeleteButton = itemView.findViewById(R.id.delete_perk);
            myEditButton = itemView.findViewById(R.id.edit_button);
            fragRoot = itemView.getRootView();

            editPerkName = itemView.findViewById(R.id.edit_perk_name);
            editPerkInfo = itemView.findViewById(R.id.edit_perk_info);
            editLayout = itemView.findViewById(R.id.edit_layout_holder);

            perksLayout = itemView.findViewById(R.id.perkInfoHolder);
            myPerkImage = itemView.findViewById(R.id.location_image);
            myEditImage = itemView.findViewById(R.id.location_image2);

            myEditSave = itemView.findViewById(R.id.save_edit);
            myEditCancel = itemView.findViewById(R.id.cancel_edit);




//            //Edit Perk Button Listener
            myEditButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final int dur = 500;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        perksLayout.animate().alpha(0).setDuration(dur).withEndAction(new Runnable() {
                            @SuppressLint("NewApi")
                            @Override
                            public void run() {
                                myPerkImage.setVisibility(View.GONE);
                                editLayout.setAlpha(0);
                                myEditImage.setAlpha(0);
                                editLayout.animate().alpha(1).setDuration(dur);
                                editLayout.setVisibility(View.VISIBLE);
                                myEditImage.setVisibility(View.VISIBLE);
                                myEditImage.animate().alpha(1).setDuration(dur);
                                perksLayout.setVisibility(View.GONE);

                            }
                        });
                        myPerkImage.animate().alpha(0).setDuration(dur);



                        editPerkName.setText(myTextView.getText());
                        editPerkInfo.setText(myLocationInfo.getText());


                        myEditButton.setVisibility(View.GONE);
                        fragRoot.animate().scaleY(1);
                        perksLayout.setVisibility(View.VISIBLE);
                        editLayout.setVisibility(View.VISIBLE);
                        editLayout.setVisibility(View.GONE);

                    }

                }
            });
            if(myEditCancel!=null)
            myEditCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Make edit layout gone
                    editLayout.setVisibility(View.GONE);
                    myPerkImage.setVisibility(View.VISIBLE);
                    //Make default perk info visible
                    perksLayout.setVisibility(View.VISIBLE);
                    myPerkImage.setAlpha(1);
                    perksLayout.setAlpha(1);
                    myEditButton.setVisibility(View.VISIBLE);
                 }
            });
            if(myEditSave!=null)
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
// todo doesdnt work
//    private static void animateHeight(final View view, float begin, float end, int mdur){
//        ValueAnimator va = ValueAnimator.ofFloat(begin, end);
//        va.setDuration(mdur);
//        ViewGroup.LayoutParams params = view.getLayoutParams();
//        params.height = (int)begin;
//        view.setLayoutParams(params);
//        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            public void onAnimationUpdate(ValueAnimator animation) {
//                ViewGroup.LayoutParams params = view.getLayoutParams();
//                params.height = (int)((float)animation.getAnimatedValue()*params.height);
//                view.setLayoutParams(params);
//            }
//        });
//        va.start();
//    }

    // convenience method for getting data at click position

}
