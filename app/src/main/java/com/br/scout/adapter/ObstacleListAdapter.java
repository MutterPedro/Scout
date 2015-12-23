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

        text.setText(obstacle.getName()+" à "+
                calculationByDistance(MapsActivity.MY_LOC,new LatLng(obstacle.getLatitude(),obstacle.getLongitude()))+" metros");


        return convertView;
    }

    public int calculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        //double km = valueResult / 1;
        //DecimalFormat newFormat = new DecimalFormat("####");
        //int kmInDec = Integer.valueOf(newFormat.format(km));
        //double meter = valueResult % 1000;
        //int meterInDec = Integer.valueOf(newFormat.format(meter));
        //Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
         //       + " Meter   " + meterInDec);

        return (int)(valueResult*1000);
    }
}
