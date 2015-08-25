package com.example.fateweaver.picosense;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fateweaver on 13/08/2015.
 */
public class LocalSensors implements SensorEventListener {

    private Context context;

    private List<SensorReading> temperature;
    private List<SensorReading> light;
    private List<SensorReading> pressure;
    private List<SensorReading> humidity ;
    private ArrayList<AccelerometerReading> acceleration;


    private SensorManager sensorManager;
    private List<Sensor> sensors;

    public LocalSensors (Context _context) {
        this.context = _context;

        temperature = new ArrayList<SensorReading>();
        light = new ArrayList<SensorReading>();
        pressure = new ArrayList<SensorReading>();
        humidity = new ArrayList<SensorReading>();
        acceleration = new ArrayList<AccelerometerReading>();


        sensorManager = (SensorManager) context.getSystemService(context.SENSOR_SERVICE);

        sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);

        for (Sensor it : sensors) {
            sensorManager.registerListener(this,
                    sensorManager.getDefaultSensor(it.getType()),
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    public void onSensorChanged(SensorEvent event) {
        int type = event.sensor.getType();
        switch(type) {
            case Sensor.TYPE_ACCELEROMETER : newAcceleration(event);
                break;
            case Sensor.TYPE_AMBIENT_TEMPERATURE :
                temperature.add(new SensorReading(event.values[0], event.timestamp));
                Log.d("TEMP", Integer.toString(temperature.size()));
                if (temperature.size() > 100) {
                    Log.d("TEMP", "SAVING");
                    JSONArray json = new JSONArray();
                    try {
                        for (int i = 0; i < temperature.size(); i++) {
                            JSONObject data = new JSONObject();
                            data.put("timestamp", temperature.get(i).getTime());
                            data.put("temperature", temperature.get(i).getValue());
                            json.put(data);
                        }
                        Toast.makeText(context, "added temperature", Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String filename = "temp" + Long.toString(temperature.get(0).getTime());
                    if(saveData(filename, json)) temperature.clear();
                }
                break;
            case Sensor.TYPE_LIGHT :
                light.add(new SensorReading(event.values[0], event.timestamp));
                if (light.size() > 1000) {
                    JSONArray json = new JSONArray();
                    try {
                        for (int i = 0; i < light.size(); i++) {
                            JSONObject data = new JSONObject();
                            data.put("timestamp", light.get(i).getTime());
                            data.put("light", light.get(i).getValue());
                            json.put(data);
                        }
                        Toast.makeText(context, "added light", Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String filename = "light" + Long.toString(light.get(0).getTime());
                    if(saveData(filename, json)) light.clear();
                }
                break;
            case Sensor.TYPE_PRESSURE :
                pressure.add(new SensorReading(event.values[0], event.timestamp));
                if (pressure.size() > 1000) {
                    JSONArray json = new JSONArray();
                    try {
                        for (int i = 0; i < pressure.size(); i++) {
                            JSONObject data = new JSONObject();
                            data.put("timestamp", pressure.get(i).getTime());
                            data.put("pressure", pressure.get(i).getValue());
                            json.put(data);
                        }
                        Toast.makeText(context, "added pressure", Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String filename = "press" + Long.toString(pressure.get(0).getTime());
                    if(saveData(filename, json)) pressure.clear();
                }
                break;
            case Sensor.TYPE_RELATIVE_HUMIDITY :
                humidity.add(new SensorReading(event.values[0], event.timestamp));
                if (humidity.size() > 100) {
                    JSONArray json = new JSONArray();
                    try {
                        for (int i = 0; i < humidity.size(); i++) {
                            JSONObject data = new JSONObject();
                            data.put("timestamp", humidity.get(i).getTime());
                            data.put("humidity", humidity.get(i).getValue());
                            json.put(data);
                        }
                        Toast.makeText(context, "added humidity", Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String filename = "humid" + Long.toString(humidity.get(0).getTime());
                    if(saveData(filename, json)) humidity.clear();
                }
                break;
            default :                break;
        }

    }

    public void newAcceleration(SensorEvent event) {

        AccelerometerReading reading = new AccelerometerReading(event.values, event.timestamp);

        float accelerationSquareRoot = (reading.getX() * reading.getX()  + reading.getY()  * reading.getY()  + reading.getZ()  * reading.getZ() ) / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);

        if (accelerationSquareRoot >= 2) acceleration.add(reading);

        if (acceleration.size() > 1000) {
            JSONArray json = new JSONArray();
            try {
                for(int i = 0; i < acceleration.size(); i++) {
                    JSONObject data = new JSONObject();
                    data.put("timestamp", acceleration.get(i).getTime());
                    data.put("x", acceleration.get(i).getX());
                    data.put("y", acceleration.get(i).getY());
                    data.put("z", acceleration.get(i).getZ());
                    json.put(data);
                }
                Toast.makeText(context, "added acceleration", Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String filename = "accel" + Long.toString(acceleration.get(0).getTime());
            FileOutputStream outputStream;

            try {
                outputStream = context.openFileOutput(filename, context.MODE_PRIVATE);
                outputStream.write(json.toString().getBytes());
                outputStream.close();
                acceleration.clear();
                Toast.makeText(context, "saved file", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public boolean saveData(String filename, JSONArray data) {

        FileOutputStream outputStream;

        try {
            outputStream = context.openFileOutput(filename, context.MODE_PRIVATE);
            outputStream.write(data.toString().getBytes());
            outputStream.close();
            Toast.makeText(context, "saved file", Toast.LENGTH_SHORT).show();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void destroy() {
        for (Sensor it : sensors) {
            sensorManager.unregisterListener(this);
        }
    }

}
