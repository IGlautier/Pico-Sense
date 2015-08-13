package com.example.fateweaver.picosense;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fateweaver on 13/08/2015.
 */
public class GPSSensor implements LocationListener{
    Context context;



    private List<GPSReading> gps;

    public GPSSensor(Context _context) {
        this.context = _context;
        gps = new ArrayList<GPSReading>();
    }

    @Override
    public void onLocationChanged(Location loc) {
        Toast.makeText(context, "location changed", Toast.LENGTH_SHORT).show();
        gps.add(new GPSReading(loc.getLatitude(), loc.getLongitude(), loc.getTime()));
    }

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
}
