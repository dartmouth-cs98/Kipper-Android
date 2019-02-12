package com.artfara.apps.kipper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.artfara.apps.kipper.models.CustomPlace;
import com.artfara.apps.kipper.models.Post;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Joe on 10/2/16.
 * Listview Adapter to Display Posts for chat
 */

public class SelectMarkerListViewAdapter extends BaseAdapter {
     private Context c; //Holds context adapter was created in

    private static final String TAG = "  MarkerListAdapter";
    private LayoutInflater mInflater;
    private SharedPreferences mPrefs;
    private ArrayList<String> mMarkers;

    public SelectMarkerListViewAdapter(Context context) {
        mInflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        c = context;
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        mMarkers = new ArrayList<>();
    }

    public void setEntries(){
        mMarkers = Constants.MARKER_VALUES;
        notifyDataSetChanged();
    }
    public int getCount() {
        return mMarkers.size();
    }

    public Object getItem(int position) {
        return mMarkers.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    //Displays Name of Place, and #of string for each row in listView.
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = new ViewHolder();
        View rowView = mInflater.inflate(R.layout.marker_custom_row_view, null);

        final String markerType = mMarkers.get(position);

        holder.markerImage = (ImageView) rowView.findViewById(R.id.markerImage);
        holder.markerImage.setImageDrawable(rowView.getResources()
                .getDrawable(Constants.CUSTOM_PLACES.get(markerType), c.getTheme()));

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(c, PostActivity.class);
                intent.putExtra(Constants.MARKER_ID_KEY, markerType);
                c.startActivity(intent);
            }
        });

        return rowView;
    }

    //Empty class to hold both TextViews
    static class ViewHolder {
        ImageView markerImage;
    }
}
