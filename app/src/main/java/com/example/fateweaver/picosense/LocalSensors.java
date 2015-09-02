package com.example.fateweaver.picosense;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.Toast;

import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.http.WebSocket;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;
import com.koushikdutta.async.http.server.HttpServerRequestCallback;

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
 * Created by Ivan Glautier on 13/08/2015.
 */
public class LocalSensors implements SensorEventListener { // Handles sensor events

    private Context context;

    // Holds all sensor readings until they are stored
    private List<SensorReading> temperature;
    private List<SensorReading> light;
    private List<SensorReading> pressure;
    private List<SensorReading> humidity ;
    private ArrayList<AccelerometerReading> acceleration;
    private ArrayList<AccelerometerReading> magnetic;
    private Long lastMag;

    // Controls access to sensors
    private SensorManager sensorManager;
    private List<Sensor> sensors;

    // For websocket server
    private ArrayList<WebSocket> mSockets;
    private AsyncHttpServer mAsyncHttpServer;
    private AsyncHttpServer.WebSocketRequestCallback mWebSocketCallback;


    public LocalSensors (Context _context) {
        this.context = _context;

        // Initialisation
        temperature = new ArrayList<SensorReading>();
        light = new ArrayList<SensorReading>();
        pressure = new ArrayList<SensorReading>();
        humidity = new ArrayList<SensorReading>();
        acceleration = new ArrayList<AccelerometerReading>();
        magnetic = new ArrayList<AccelerometerReading>();
        lastMag = 0L;

        // Websocket server setup
        mSockets = new ArrayList<WebSocket>();
        mAsyncHttpServer = new AsyncHttpServer();


        mWebSocketCallback = new AsyncHttpServer.WebSocketRequestCallback() {
            @Override
            public void onConnected(final WebSocket webSocket, AsyncHttpServerRequest headers) {
                mSockets.add(webSocket);
                webSocket.send("Welcome Client"); // Not essential
                webSocket.setClosedCallback(new CompletedCallback() {
                    @Override
                    public void onCompleted(Exception ex) {
                        try {
                            if (ex != null)
                                Log.e("WebSocket", "Error");
                        } finally {
                            mSockets.remove(webSocket);
                        }
                    }
                });
                webSocket.setStringCallback(new WebSocket.StringCallback() {
                    @Override
                    public void onStringAvailable(String s) {
                    // Can put in some logic for validating clients

                    }
                });
            }
        };

        mAsyncHttpServer.websocket("/",mWebSocketCallback);
        mAsyncHttpServer.listen(3000);

        // Sensors setup
        sensorManager = (SensorManager) context.getSystemService(context.SENSOR_SERVICE);

        sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);


    }

    public void addSensor(int sensorType) {
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(sensorType), SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void onSensorChanged(SensorEvent event) {

        int type = event.sensor.getType();

        switch(type) {
            case Sensor.TYPE_ACCELEROMETER :

                newAcceleration(event);

            case Sensor.TYPE_MAGNETIC_FIELD :

                newMagnetic(event);
                break;

            case Sensor.TYPE_AMBIENT_TEMPERATURE :
                temperature.add(new SensorReading(event.values[0], event.timestamp));


                if (temperature.size() > 100) { // Publish if we have 100 readings
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

                    for (WebSocket socket : mSockets) socket.send(json.toString()); // Push to websocket

                    String filename = "temp" + Long.toString(temperature.get(0).getTime()); // Save json file (need some method of cleaning these and historical requests)
                    if(saveData(filename, json)) temperature.clear();
                }
                break;

            case Sensor.TYPE_LIGHT :
                light.add(new SensorReading(event.values[0], event.timestamp));


                if (light.size() > 1000) { // Publish after 1000 readings
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

                    for (WebSocket socket : mSockets) socket.send(json.toString());

                    String filename = "light" + Long.toString(light.get(0).getTime());
                    if(saveData(filename, json)) light.clear();
                }
                break;

            case Sensor.TYPE_PRESSURE :
                pressure.add(new SensorReading(event.values[0], event.timestamp));


                if (pressure.size() > 1000) { // Publish after 1000 readings
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

                    for (WebSocket socket : mSockets) socket.send(json.toString());

                    String filename = "press" + Long.toString(pressure.get(0).getTime());
                    if(saveData(filename, json)) pressure.clear();
                }
                break;

            case Sensor.TYPE_RELATIVE_HUMIDITY :
                humidity.add(new SensorReading(event.values[0], event.timestamp));


                if (humidity.size() > 100) { // Publish after 100 readings
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

                    for (WebSocket socket : mSockets) socket.send(json.toString());

                    String filename = "humid" + Long.toString(humidity.get(0).getTime());
                    if(saveData(filename, json)) humidity.clear();
                }
                break;

            default :                break;
        }

    }

    public void newAcceleration(SensorEvent event) {
            // Separate sensor event class as we deal with 3 values per reading
            AccelerometerReading reading = new AccelerometerReading(event.values, event.timestamp);

            float accelerationSquareRoot = (reading.getX() * reading.getX()  + reading.getY()  * reading.getY()  + reading.getZ()  * reading.getZ() ) / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);

            if (accelerationSquareRoot >= 2) acceleration.add(reading); // Avoid flooding with values

            if (acceleration.size() > 500) { // Publish after 500 readings

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

                for (WebSocket socket : mSockets) socket.send(json.toString());

                String filename = "accel" + Long.toString(acceleration.get(0).getTime()); // Controls file names
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

    public void newMagnetic(SensorEvent event) {
        if ((event.timestamp - lastMag) > 1000000000L) {
            lastMag = event.timestamp;
            AccelerometerReading reading = new AccelerometerReading(event.values, event.timestamp);

            magnetic.add(reading);
            if (magnetic.size() > 500) {
                JSONArray json = new JSONArray();
                try {
                    for (int i = 0; i < magnetic.size(); i++) {
                        JSONObject data = new JSONObject();
                        data.put("timestamp", magnetic.get(i).getTime());
                        data.put("x", magnetic.get(i).getX());
                        data.put("y", magnetic.get(i).getY());
                        data.put("z", magnetic.get(i).getZ());
                        json.put(data);
                    }
                    Toast.makeText(context, "added magnetic", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                for (WebSocket socket : mSockets) socket.send(json.toString());

                String filename = "mag" + Long.toString(magnetic.get(0).getTime()); // Controls file names
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
