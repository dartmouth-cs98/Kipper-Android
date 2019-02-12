package com.artfara.apps.kipper;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.artfara.apps.kipper.models.Post;

import java.util.ArrayList;

public class ChatReplyListViewActivity extends AppCompatActivity {

    private String mPostId;
    private ChatListViewAdapter customBaseAdapter;
    private static final String TAG = " ChatReply Activity";
    private ListView listview;
    private Handler mHandler;
    private boolean mActionStartFromTop;
    private ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_replies_list_view);

        mPostId = getIntent().getStringExtra(Constants.POST_ID_KEY);

        //Create Custom Adapter
        customBaseAdapter = new ChatListViewAdapter(this, mPostId);

        //Grab a handle on ListView
        listview = (ListView) findViewById(R.id.ListViewReplies);
        listview.setAdapter(customBaseAdapter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("");
            setSupportActionBar(toolbar);
            TextView title = (TextView) toolbar.findViewById(R.id.toolbar_title);
            Typeface typeFaceBold = Typeface.createFromAsset(getAssets(), "Comfortaa-Bold.ttf");
            title.setTypeface(typeFaceBold);
        }
        LinearLayout postLayout = (LinearLayout) findViewById(R.id.wrapper_post);
        postLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatReplyListViewActivity.this, PostActivity.class);
                intent.putExtra(Constants.POST_ID_KEY, mPostId);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
//        Log.d(TAG, "onResume");
        mActionStartFromTop = getIntent().getBooleanExtra(
                Constants.ACTION_START_FROM_TOP_KEY, false);
        //Update replies as soon as they become available
        //update posts as soon as they become available
        customBaseAdapter.setEntries(new ArrayList<Post>());
//        Log.d(TAG, "startAtTop " + mActionStartFromTop);
        if (mActionStartFromTop) {
            PostDatabaseHelper.downloadReplies(mPostId);
        }
        mHandler = new Handler();
        mHandler.postDelayed(mPopulateListViewRunnable, Constants.MILLISECONDS_BEFORE_POLLING);
    }

    Runnable mPopulateListViewRunnable = new Runnable() {
        public void run() {
            //If data has not yet been downloaded, try again later
            if (PostDatabaseHelper.mFinishedDownloading == false) {
//                Log.d(TAG, "posts still null");
                mHandler.postDelayed(this, Constants.MILLISECONDS_BETWEEN_POLLING);
                if (mProgressDialog == null) {
                    //Display loading spinner
                    mProgressDialog = new ProgressDialog(ChatReplyListViewActivity.this);
                    mProgressDialog.setMessage(getString(R.string.loading_message));
                    mProgressDialog.show();
                }
            }
            else {
                if (ChatReplyListViewActivity.this != null && mProgressDialog != null
                        && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }
//                Log.d(TAG, "posts NOT null");
                if (PostDatabaseHelper.contains(mPostId)) {
                    customBaseAdapter.setEntries(PostDatabaseHelper.getReplies(mPostId));
//                    Log.d(TAG, "startAtTop in runnable " + mActionStartFromTop);
                    if (mActionStartFromTop) {
                        getIntent().putExtra(Constants.ACTION_START_FROM_TOP_KEY, false);
                    }
                    else {
                        listview.post(new Runnable() {
                            public void run() {
                                listview.setSelection(listview.getCount() - 1);
                            }
                        });
                    }
                }
            }
        }
    };
}
