package com.dragmap;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends AppCompatActivity implements LocationListener{
    private AlertDialog locationAlertDialog;

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    private static final long MIN_TIME_BETWEEN_UPDATES = 1000 * 60 * 1; // 1 minute
    private LocationManager manager;
    private LatLng latLng = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        enableLocation();
    }


    private void initLocationManager(){
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_DISTANCE_CHANGE_FOR_UPDATES, MIN_TIME_BETWEEN_UPDATES, this);
    }
    public void enableLocation(){
        manager = (LocationManager)getSystemService(LOCATION_SERVICE);
        initLocationManager();
        Location location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(location != null){
           setLatLng(new LatLng(location.getLatitude(),location.getLongitude()));

        }else {
            manager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    MIN_TIME_BETWEEN_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_exit) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showLocationOffDialog() {

        try {
            AlertDialog.Builder gpsBuilder = new AlertDialog.Builder(
                    MainActivity.this);
            gpsBuilder.setCancelable(false);
            gpsBuilder
                    .setTitle("Locaton service is off.")
                    .setMessage("please turn on location")
                    .setPositiveButton(
                            "Go to Location Service",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    // continue with delete
                                    dialog.dismiss();
                                    Intent viewIntent = new Intent(
                                            Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivity(viewIntent);

                                }
                            })
                    .setNegativeButton("Exit",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    // do nothing
                                    dialog.dismiss();
                                    finish();
                                }
                            });
            locationAlertDialog = gpsBuilder.create();
            locationAlertDialog.show();

        } catch (Exception e) {

        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if(location != null) {
            setLatLng(new LatLng(location.getLatitude(),location.getLongitude()));
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        removeLocationoffDialog();
    }

    private void removeLocationoffDialog() {
        if (locationAlertDialog != null && locationAlertDialog.isShowing()) {
            locationAlertDialog.dismiss();
            locationAlertDialog = null;
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        if (locationAlertDialog == null ) {
            showLocationOffDialog();
        }else if(locationAlertDialog != null && !locationAlertDialog.isShowing()){
            showLocationOffDialog();
        }
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }
}
