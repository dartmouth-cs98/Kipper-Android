package com.artfara.apps.kipper.models;

import java.util.HashMap;

/**
 * Created by Joe Connolly on 12/13/16.
 */
public class Post {
    public String ID;
    public String text;
    public HashMap<String, Post> replies;
    public int voteCount;
    public long timeInMilliseconds;
    public String userID;
    public String userLetter;
    public String displayedTime;
    public String parentPostID;
    public String image;
    public int pointsNotified;


    public Post() {
        // Default constructor required for calls to DataSnapshot.getValue()
    }

    public Post(String userID, String messsageBody, long time) {
        text = messsageBody;
        replies = new HashMap<>();
        this.userID = userID;
        this.timeInMilliseconds = time;
    }

    public HashMap<String, Object> toMap(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("ID", ID);
        map.put("text", text);
        map.put("replies", replies);
        map.put("voteCount", voteCount);
        map.put("timeInMilliseconds", timeInMilliseconds);
        map.put("userID", userID);
        map.put("parentPostID", parentPostID);
        return map;
    }

    @Override
    public String toString(){
        return "Post: " + text + " " + (replies != null ? replies.size() : "null");
    }

}
