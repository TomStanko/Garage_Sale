package com.adaptivelite.garagesale.startpage;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;


public class MapActivity extends Activity {
    private double [] myLoc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        //Get a handle to the Map Fragment
        assert ((MapFragment) getFragmentManager()
                .findFragmentById(R.id.map)) != null;
        GoogleMap map = ((MapFragment) getFragmentManager()
                .findFragmentById(R.id.map)).getMap();


        map.setMyLocationEnabled(true);
        myLoc = getLocation();
        LatLng myLng = new LatLng(myLoc[0], myLoc[1]);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(myLng, 13));

        map.addMarker(new MarkerOptions()
                .title("Me")
                .snippet("Cool Dude")
                .position(myLng));
    }

    public double[] getLocation() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = lm.getProviders(true);

        Location l = null;
        for (int i = 0; i < providers.size(); i++) {
            l = lm.getLastKnownLocation(providers.get(i));
            if (l != null)
                break;
        }
        double[] gps = new double[2];

        if (l != null) {
            gps[0] = l.getLatitude();
            gps[1] = l.getLongitude();
        }
        return gps;
    }
}
