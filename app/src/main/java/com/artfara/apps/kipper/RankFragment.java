package com.artfara.apps.kipper;


import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.artfara.apps.kipper.models.Account;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class RankFragment extends Fragment {
    private Account mAccount;
    private static final String TAG = "Rank Fragment ";
    private TextView mTxtCollegeLabel;
    private TextView mTxtRank;
    private TextView mTxtRelativeRank;
    private TextView mTxtPoints;


    public RankFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FirebaseDatabase.getInstance().getReference()
                .child(Globals.USERS_ACCOUNTS_TABLE_NAME).child(Utils.getAndroidID(getContext()))
                .addListenerForSingleValueEvent(mAccountsSingleEventListener);

        View view = inflater.inflate(R.layout.fragment_rank, container, false);
        mTxtRank = (TextView) view.findViewById(R.id.txtRank);
        mTxtRelativeRank = (TextView) view.findViewById(R.id.txtRelativeRank);
        mTxtPoints = (TextView) view.findViewById(R.id.txtPoints);
        mTxtCollegeLabel = (TextView) view.findViewById(R.id.txtCollegeLabel);
        Typeface typeFaceBold = Typeface.createFromAsset(getContext().getAssets(), "Comfortaa-Bold.ttf");
        mTxtRank.setTypeface(typeFaceBold);
        mTxtRelativeRank.setTypeface(typeFaceBold);
        mTxtPoints.setTypeface(typeFaceBold);
        mTxtCollegeLabel.setTypeface(typeFaceBold);
        // Inflate the layout for this fragment
        return view;
    }


    private ValueEventListener mAccountsSingleEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            mAccount = dataSnapshot.getValue(Account.class);
            Log.d(TAG, "account " + dataSnapshot.getKey());
            mTxtRank.setText(mAccount.rankString);
            mTxtPoints.setText(mAccount.points + " pts");
            mTxtRelativeRank.setText(mAccount.relativeRankString);
            mTxtCollegeLabel.setText("Rank at " + Globals.COLLEGE.name);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.e(TAG, databaseError.getMessage());
        }
    };

}
