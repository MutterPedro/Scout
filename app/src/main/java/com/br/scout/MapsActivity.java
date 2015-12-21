package com.br.scout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.br.scout.backend.DatabaseOperations;
import com.br.scout.beans.Obstacle;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Context ctx;
    DatabaseOperations dbOperations;

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
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        LatLng marker1 = new LatLng(-21.22752687, -44.97900903);
        mMap.addMarker(new MarkerOptions().position(marker1).title("Árvore"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,10));

        /*mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {

            @Override
            public void onMyLocationChange(Location location) {

                // TODO Auto-generated method stub
                Log.v("current location lat:", "" + location.getLatitude());
                Log.v("current location lng:", "" + location.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
                CameraUpdate center =
                        CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 20);

                mMap.moveCamera(center);
            }
        });*/

        setMapIfNeeded();

    }

    public void setMapIfNeeded(){
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(final LatLng latLng) {
                //TODO verificar se já existe marcação
                final Obstacle obstacle = new Obstacle();
                LayoutInflater factory = LayoutInflater.from(getApplicationContext());
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
                                if (!obstacle.getName().isEmpty()) {
                                    dbOperations.addObstacle(obstacle);
                                    mMap.addMarker(new MarkerOptions().position(new LatLng(obstacle.getLatitude(),obstacle.getLongitude()))
                                            .title(obstacle.getName()));
                                }
                            }
                        })
                        .setNegativeButton("Cancelar", null).show();

            }
        });
    }
}