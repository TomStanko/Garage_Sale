package com.adaptivelite.garagesale.startpage;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MapActivity extends Activity {

    //Variables
    private ProgressDialog pDialog;
    private double [] myLoc;

    JSONParser jParser = new JSONParser();

    ArrayList<HashMap<String, String>> postsList;

    // url to get all products list
    private static String url_all_posts = "";

    //JSON Node name
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_POSTS = "posts";
    private static final String TAG_PID = "pid";
    private static final String TAG_FIRST_NAME = "first_name";
    private static final String TAG_LAST_NAME = "last_name";
    private static final String TAG_ADDRESS = "address";
    private static final String TAG_ZIP_CODE = "zip_code";
    private static final String TAG_STATE = "state";
    private static final String TAG_DESCRIPTION = "description";
    private static final String TAG_CREATED_AT = "created_at";
    private static final String TAG_UPDATED_AT = "updated_at";

    // posts JSONArray
    JSONArray posts = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        //Hashmap for ListView
        postsList = new ArrayList<ArrayList<String>>();

        // Loading posts in background thread
        new LoadAllPosts().execute();

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
    //Background Async Task to Load all posts by making HTTP Request
    class LoadAllPosts extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MapActivity.this);
            pDialog.setMessage("Loading posts. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_all_posts, "GET", params);

            Log.d("All Posts: ", json.toString());

            try {

                int success = json.getInt(TAG_SUCCESS);

                if(success == 1) {
                    posts = json.getJSONArray(TAG_POSTS);

                    for(int i = 0; i < posts.length(); i++) {
                        JSONObject c = posts.getJSONObject(i);

                        String id = c.getString(TAG_PID);
                        String first_name = c.getString(TAG_FIRST_NAME);
                        String last_name = c.getString(TAG_LAST_NAME);
                        String address = c.getString(TAG_ADDRESS);
                        String zip_code = c.getString(TAG_ZIP_CODE);
                        String state = c.getString(TAG_STATE);
                        String description = c.getString(TAG_DESCRIPTION);
                        String created_at = c.getString(TAG_CREATED_AT);
                        String updated_at = c.getString(TAG_UPDATED_AT);

                        HashMap<String, String> mappy = new HashMap<String, String>();

                        mappy.put(TAG_PID, id);
                        mappy.put(TAG_FIRST_NAME, first_name);
                        mappy.put(TAG_LAST_NAME, last_name);
                        mappy.put(TAG_ADDRESS, address);
                        mappy.put(TAG_ZIP_CODE, zip_code);
                        mappy.put(TAG_STATE, state);
                        mappy.put(TAG_DESCRIPTION, description);
                        mappy.put(TAG_CREATED_AT, created_at);
                        mappy.put(TAG_UPDATED_AT, updated_at);

                        postsList.add(mappy);
                    }
                } else {

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
