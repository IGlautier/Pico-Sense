package com.example.fateweaver.picosense;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.location.LocationManager;
import android.os.IBinder;
import android.widget.Toast;

import com.koushikdutta.async.http.WebSocket;


/**
 * Created by Ivan Glautier on 12/08/2015.
 */
public class SenseService extends Service { // Background service that keeps listening for sensor events while minimised

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
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getBooleanExtra("pressure", false)) localSensors.addSensor(Sensor.TYPE_PRESSURE);
        if (intent.getBooleanExtra("accelerometer", false)) localSensors.addSensor(Sensor.TYPE_ACCELEROMETER);
        if (intent.getBooleanExtra("light", false)) localSensors.addSensor(Sensor.TYPE_LIGHT);
        if (intent.getBooleanExtra("humidity", false)) localSensors.addSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        if (intent.getBooleanExtra("temperature", false)) localSensors.addSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        if (intent.getBooleanExtra("magnetic", false)) localSensors.addSensor(Sensor.TYPE_MAGNETIC_FIELD);

        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
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
