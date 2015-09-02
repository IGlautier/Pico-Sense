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
        if (intent.getBooleanExtra("pressure", false)) if(!localSensors.addSensor(Sensor.TYPE_PRESSURE)) Toast.makeText(this, "Pressure Disabled", Toast.LENGTH_SHORT).show();
        if (intent.getBooleanExtra("accelerometer", false)) if(!localSensors.addSensor(Sensor.TYPE_ACCELEROMETER)) Toast.makeText(this, "Accelerometer Disabled", Toast.LENGTH_SHORT).show();
        if (intent.getBooleanExtra("light", false)) if(!localSensors.addSensor(Sensor.TYPE_LIGHT)) Toast.makeText(this, "Light Disabled", Toast.LENGTH_SHORT).show();
        if (intent.getBooleanExtra("humidity", false)) if(!localSensors.addSensor(Sensor.TYPE_RELATIVE_HUMIDITY)) Toast.makeText(this, "Humidity Disabled", Toast.LENGTH_SHORT).show();
        if (intent.getBooleanExtra("temperature", false)) if(!localSensors.addSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)) Toast.makeText(this, "Temperature Disabled", Toast.LENGTH_SHORT).show();
        if (intent.getBooleanExtra("magnetic", false)) if(!localSensors.addSensor(Sensor.TYPE_MAGNETIC_FIELD)) Toast.makeText(this, "Magnetic Disabled", Toast.LENGTH_SHORT).show();

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
