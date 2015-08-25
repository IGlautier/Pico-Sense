package com.example.fateweaver.picosense;

import android.app.Service;
import android.content.Intent;
import android.location.LocationManager;
import android.os.IBinder;
import android.widget.Toast;


/**
 * Created by Fateweaver on 12/08/2015.
 */
public class SenseService extends Service {
    LocalSensors localSensors;
    LocationManager locationManager;
    GPSSensor gps;


    @Override
    public void onCreate() {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

        localSensors = new LocalSensors(this);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            gps = new GPSSensor(this);
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 5000, 10, gps);

        }
        else Toast.makeText(this, "GPS Disabled", Toast.LENGTH_SHORT).show();



    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        if(gps != null) locationManager.removeUpdates(gps);
        localSensors.destroy();
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();

    }



}
