package com.example.fateweaver.picosense;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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

        if (gps.size() >  2) {
            JSONArray json = new JSONArray();
            try {
                for(int i = 0; i < gps.size(); i++) {
                    JSONObject data = new JSONObject();
                    data.put("timestamp", gps.get(i).getTime());
                    data.put("latitude", gps.get(i).getLat());
                    data.put("longitude", gps.get(i).getLon());
                    json.put(data);
                }
                Toast.makeText(context, "added items", Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //String filename = Long.toString(gps.get(0).getTime());
            String filename = "hello.txt";
            FileOutputStream outputStream;

            try {
                outputStream = context.openFileOutput(filename, context.MODE_PRIVATE);
                outputStream.write(json.toString().getBytes());
                outputStream.close();
                gps.clear();
                Toast.makeText(context, "saved file", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }


            int ch;
            StringBuffer fileContent = new StringBuffer("");
            FileInputStream fis;
            try {
                fis = context.openFileInput( "hello.txt" );
                try {
                    while( (ch = fis.read()) != -1)
                        fileContent.append((char)ch);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            String data = new String(fileContent);
            Log.d("NQM", data);
        }
    }

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
}
