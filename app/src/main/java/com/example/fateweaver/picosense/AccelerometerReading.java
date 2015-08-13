package com.example.fateweaver.picosense;

/**
 * Created by Fateweaver on 13/08/2015.
 */
public class AccelerometerReading extends SensorReading {

    private float x, y, z;

    public AccelerometerReading(float [] values, long _timestamp) {
        super(values[0], _timestamp);
        this.x = values[0];
        this.y = values[1];
        this.z = values[2];
    }

    public float getX() {return this.x;}
    public float getY() {return this.y;}
    public float getZ() {return this.z;}
}
