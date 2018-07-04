package net.jsaistudios.cpsc.cpsc;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Alec on 5/18/2018.
 */

public class MessageRecyclerViewAdapter extends RecyclerView.Adapter<MessageRecyclerViewAdapter.ViewHolder> {

    private List<String> mData;
    private List<String> locationInfo;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    MessageRecyclerViewAdapter(Context context, List<String> data, List<String> locationInfo) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.locationInfo = locationInfo;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.message_fragment_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MessageRecyclerViewAdapter.ViewHolder holder, int position) {
        String animal = mData.get(position);

        String info = locationInfo.get(position);
        holder.myLocationInfo.setText(info);
        holder.myTextView.setText(animal);
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

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.message_bubble);
            myLocationInfo = itemView.findViewById(R.id.message_info);
        }

    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
