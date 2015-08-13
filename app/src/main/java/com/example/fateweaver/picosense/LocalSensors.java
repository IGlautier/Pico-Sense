package com.example.fateweaver.picosense;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

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
            case Sensor.TYPE_AMBIENT_TEMPERATURE : temperature.add(new SensorReading(event.values[0], event.timestamp));
                break;
            case Sensor.TYPE_LIGHT : light.add(new SensorReading(event.values[0], event.timestamp));
                break;
            case Sensor.TYPE_PRESSURE : pressure.add(new SensorReading(event.values[0], event.timestamp));
                break;
            case Sensor.TYPE_RELATIVE_HUMIDITY : humidity.add(new SensorReading(event.values[0], event.timestamp));
                break;
            default :                break;
        }

    }

    public void newAcceleration(SensorEvent event) {

        AccelerometerReading reading = new AccelerometerReading(event.values, event.timestamp);

        float accelerationSquareRoot = (reading.getX() * reading.getX()  + reading.getY()  * reading.getY()  + reading.getZ()  * reading.getZ() ) / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);

        if (accelerationSquareRoot >= 2) acceleration.add(reading);

    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}
