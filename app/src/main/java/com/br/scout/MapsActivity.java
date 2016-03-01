package com.br.scout;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.br.scout.adapter.ObstacleListAdapter;
import com.br.scout.backend.DatabaseOperations;
import com.br.scout.beans.Establishment;
import com.br.scout.beans.Obstacle;
import com.br.scout.beans.User;
import com.br.scout.widget.Utility;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    public static LatLng MY_LOC;
    private GoogleMap mMap;
    Activity ctx;
    DatabaseOperations dbOperations;
    ObstacleListAdapter adapter;
    List<Obstacle> obstacleList=new ArrayList<Obstacle>();
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        ctx = this;
        dbOperations = new DatabaseOperations(ctx);

        listView = (ListView) findViewById(R.id.list_item);
        adapter = new ObstacleListAdapter(this, obstacleList);
        listView.setAdapter(adapter);

        if(!Utility.USER.isSpecial()){
            ((View) listView.getParent().getParent()).setVisibility(View.GONE);
        } else{
            findViewById(R.id.tip_text).setVisibility(View.GONE);
            findViewById(R.id.map).setVisibility(View.GONE);
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {

            @Override
            public void onMyLocationChange(Location location) {

                mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
               // CameraUpdate center =
                 //    CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 25);

                //mMap.moveCamera(center);
                MY_LOC = new LatLng(location.getLatitude(), location.getLongitude());
                obstacleList.clear();
                obstacleList.addAll(dbOperations.listAllObstacles());

                for(int i=0;i<obstacleList.size();i++){

                    LatLng marker = new LatLng(obstacleList.get(i).getLatitude(), obstacleList.get(i).getLongitude());
                    mMap.addMarker(new MarkerOptions().position(marker).title(obstacleList.get(i).getName()));
                }

                adapter.notifyDataSetChanged();
                Utility.setListViewHeightBasedOnChildren(listView);

            }
        });

        setMapIfNeeded();

    }

    public void setMapIfNeeded(){
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(final LatLng latLng) {
                //TODO verificar se já existe marcação
                final LayoutInflater factory = LayoutInflater.from(getApplicationContext());
                new AlertDialog.Builder(ctx)
                        .setTitle("Tipo de marcação")
                        .setNegativeButton("Obstaculo", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final Obstacle obstacle = new Obstacle();
                                final View textEntryView = factory.inflate(R.layout.edit_text_dialog, null);
                                new AlertDialog.Builder(ctx)
                                        .setTitle("Nome do obstaculo")
                                        .setView(textEntryView)
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                obstacle.setName(String.valueOf(((EditText) textEntryView.findViewById(R.id.edit_text)).getText()));
                                                obstacle.setLatitude(latLng.latitude);
                                                obstacle.setLongitude(latLng.longitude);
                                                obstacle.setUser(new User());
                                                if (!obstacle.getName().isEmpty()) {
                                                    dbOperations.addObstacle(obstacle);
                                                   // mMap.addMarker(new MarkerOptions().position(new LatLng(obstacle.getLatitude(), obstacle.getLongitude()))
                                                     //       .title(obstacle.getName()));
                                                }
                                            }
                                        })
                                        .setNegativeButton("Cancelar", null).show();
                            }
                        })
                        .setPositiveButton("Estabelecimento", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final Establishment establishment = new Establishment();
                                final View textEntryView = factory.inflate(R.layout.establishment_mark, null);
                                new AlertDialog.Builder(ctx)
                                        .setTitle("Novo estabelecimento")
                                        .setView(textEntryView)
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                establishment.setName(String.valueOf(((EditText) textEntryView.findViewById(R.id.name_text)).getText()));
                                                establishment.setLatitude(latLng.latitude);
                                                establishment.setLongitude(latLng.longitude);
                                                establishment.setUser(new User());
                                                establishment.setDescription(String.valueOf(((EditText) textEntryView.findViewById(R.id.descrip_text)).getText()));

                                                try {
                                                    Geocoder geo = new Geocoder(MapsActivity.this.getApplicationContext(), Locale.getDefault());
                                                    List<Address> addresses = geo.getFromLocation(latLng.latitude, latLng.longitude, 1);
                                                    String newAddress = addresses.get(0).getAddressLine(0) + ", " + addresses.get(0).getAddressLine(1);
                                                    establishment.setAddress(newAddress);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                                if (!establishment.getName().isEmpty() && !establishment.getDescription().isEmpty()) {
                                                    dbOperations.addEstablishment(establishment);
                                                    mMap.addMarker(new MarkerOptions().position(new LatLng(establishment.getLatitude(), establishment.getLongitude()))
                                                            .title(establishment.getName()).snippet(establishment.getAddress()));
                                                }
                                            }
                                        })
                                        .setNegativeButton("Cancelar", null).show();
                            }
                        }).show();


            }
        });
    }

}
