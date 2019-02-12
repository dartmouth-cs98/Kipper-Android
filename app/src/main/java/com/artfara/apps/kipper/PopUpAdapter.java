package com.artfara.apps.kipper;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by Van on 9/11/16.
 */
class PopUpAdapter implements GoogleMap.InfoWindowAdapter {
    LayoutInflater inflater = null;

    PopUpAdapter(LayoutInflater inflater) {
        this.inflater = inflater;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return(null);
    }

    @Override
    public View getInfoContents(Marker marker) {
        View popup = inflater.inflate(R.layout.popup, null);

        Typeface typeFaceBold = Typeface.createFromAsset(inflater.getContext().getAssets(), "Comfortaa-Bold.ttf");
        Typeface typeFaceLight = Typeface.createFromAsset(inflater.getContext().getAssets(), "Comfortaa-Light.ttf");

        TextView tvTitle =(TextView) popup.findViewById(R.id.title);
        tvTitle.setText(marker.getTitle());
        tvTitle.setTypeface(typeFaceLight);

        TextView tvSnippet = (TextView) popup.findViewById(R.id.snippet);
        tvSnippet.setText(marker.getSnippet());
        tvSnippet.setTypeface(typeFaceBold);




        return(popup);
    }
}