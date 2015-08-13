package com.example.fateweaver.picosense;

/**
 * Created by Fateweaver on 13/08/2015.
 */
public class SensorReading {

    private float value;
    private long timestamp;

    public SensorReading() {
        this.value = 0;
        this.timestamp = 0;
    }

    public SensorReading (float _value, long _timestamp) {
        this.value = _value;
        this.timestamp = _timestamp;
    }

    public float getValue() {
        return this.value;
    }

    public long getTime() {
        return this.timestamp;
    }

}
