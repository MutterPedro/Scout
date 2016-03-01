package com.br.scout.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.br.scout.MapsActivity;
import com.br.scout.R;
import com.br.scout.beans.Obstacle;
import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Pedro Mutter on 21/12/15.
 */
public class ObstacleListAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<Obstacle> feedItems;

    public ObstacleListAdapter(Activity activity, List<Obstacle> feedItems) {
        this.activity = activity;
        this.feedItems = feedItems;
    }

    @Override
    public int getCount() {
        return feedItems.size();
    }

    @Override
    public Object getItem(int location) {
        return feedItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.obstacle_list_row, null);


        TextView text = (TextView) convertView.findViewById(R.id.obstacle_text);

        Obstacle obstacle = feedItems.get(position);
        text.setText(obstacle.getName()+" à "+obstacle.getDistance() +" metros");
        text.requestFocus();


        return convertView;
    }

}
