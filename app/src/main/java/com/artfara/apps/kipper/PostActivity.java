package com.artfara.apps.kipper;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.artfara.apps.kipper.models.CustomPlace;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PostActivity extends AppCompatActivity {
    private static final String TAG = " PostActivity ";
    private String mParentPostID;
    private DatabaseReference mDatabase;
    private String mPostBody;
    private String mMarkerType;
    private double secondsWaited;
    private EditText mEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mParentPostID = getIntent().getStringExtra(Constants.POST_ID_KEY);
        mMarkerType = getIntent().getStringExtra(Constants.MARKER_ID_KEY);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("");
            setSupportActionBar(toolbar);
            TextView title = (TextView) toolbar.findViewById(R.id.toolbar_title);
            Typeface typeFaceBold = Typeface.createFromAsset(getAssets(), "Comfortaa-Bold.ttf");
            title.setTypeface(typeFaceBold);
        }

        mEditText = (EditText) findViewById(R.id.postBody);
        mEditText.setHorizontallyScrolling(false);
        mEditText.setMaxLines(Integer.MAX_VALUE);
        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    onPostClicked(v);
                    return true;
                }
                return false;
            }
        });

        if (mMarkerType != null) {
            mEditText.setHint("Add optional message.");
        }
        LinearLayout sendLayout = (LinearLayout) findViewById(R.id.wrapper_send);
        sendLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPostClicked(v);
            }
        });
    }

    public void onPostClicked(View view) {
        mEditText.setEnabled(false);

        mPostBody = ((EditText) findViewById(R.id.postBody)).getText().toString();

        if (mMarkerType != null) {
            addCustomMarker();
            return;
        }
        //validate input
        if (mPostBody.length() < 1 || mPostBody.length() >= Constants.POST_MAXLENGTH) {
            Toast.makeText(this, "Please write between 1-" + Constants.POST_MAXLENGTH + " characters", Toast.LENGTH_SHORT).show();
            return;
        }

        //We are adding a post
        if (mParentPostID == null) {
            PostDatabaseHelper.addPost(mPostBody, PostActivity.this, null);
        }
        //We are adding a reply
        else {
            PostDatabaseHelper.addReply(mPostBody, mParentPostID, PostActivity.this);
        }
        finish();
    }

    private void addCustomMarker() {
        Double latitude = (Double) Globals.location.get("latitude");
        Double longitude = (Double) Globals.location.get("longitude");
        if (latitude == null || longitude == null) {
            Toast.makeText(getApplicationContext(), "Sorry could not get location",
                    Toast.LENGTH_SHORT).show();
        }
        else {
            if (mPostBody != null && !mPostBody.equals("") && mPostBody.length()
                    <= Constants.POST_MAXLENGTH ) {
                PostDatabaseHelper.addPost(mPostBody, PostActivity.this, mMarkerType);
            }
            CustomPlace marker = new CustomPlace();
            marker.latitude = latitude;
            marker.longitude = longitude;
            marker.customText = mPostBody;
            marker.type = mMarkerType;
            marker.UDID = Utils.getAndroidID(this);
            marker.timestamp = System.currentTimeMillis();
            marker.time = Utils.getFormattedTime();
            Globals.globalCustomPlaces.add(marker);
            mDatabase.child(Globals.CUSTOM_PLACES_TABLE_NAME)
                    .child(mDatabase.push().getKey()).setValue(marker);
        }

        Intent intent = new Intent(this, MapsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
