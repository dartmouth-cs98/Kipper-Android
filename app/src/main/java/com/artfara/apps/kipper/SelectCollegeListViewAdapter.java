package com.artfara.apps.kipper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.artfara.apps.kipper.models.College;
import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by joeconnolly on 9/5/17.
 */

public final class SelectCollegeListViewAdapter extends BaseAdapter {
    private ArrayList<College> mColleges; //ArrayList to hold Entries in the Adapter
    private Context mContext; //Holds context adapter was created in

    private static final String TAG = "CollegesAdapter ";
    private LayoutInflater mInflater;
    private SharedPreferences mPrefs;

    public SelectCollegeListViewAdapter(Context context) {
        mInflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
        mColleges = new ArrayList<>();
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
    }

    public void setEntries(ArrayList<College> colleges){
        mColleges = colleges;
        notifyDataSetChanged();
    }
    public int getCount() {
        return mColleges.size();
    }

    public Object getItem(int position) {
        return mColleges.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    //Displays Name of college and optionally college's points.
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();

        final College currentCollege = mColleges.get(position);
        View rowView = mInflater.inflate(R.layout.college_custom_row_view, null);

        holder.collegeName = (TextView) rowView.findViewById(R.id.collegeName);


        holder.collegeName.setText(currentCollege.name);


        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.d(TAG, "tapped on " + currentCollege.name + " " + currentCollege.databaseRoot);
                currentCollege.databaseRoot = currentCollege.databaseRoot + "/";
                Gson gson = new Gson();
                mPrefs.edit().putString(Constants.COLLEGE_KEY,
                        gson.toJson(currentCollege)).apply();
                Intent intent = new Intent(mContext.getApplicationContext(), MapsActivity.class);
                mContext.startActivity(intent);
            }
        });

//        Typeface typeFaceBold = Typeface.createFromAsset(mContext.getAssets(), "Comfortaa-Bold.ttf");
//        holder.txtUserLetter.setTypeface(typeFaceBold);

        return rowView;
    }

    //Empty class to hold both TextViews
    static class ViewHolder {
        TextView collegeName;
    }



}
