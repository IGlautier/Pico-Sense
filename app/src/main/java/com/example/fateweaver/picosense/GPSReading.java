package com.example.fateweaver.picosense;

/**
 * Created by Ivan Glautier on 13/08/2015.
 */
public class GPSReading extends SensorReading {
    double lat, lon;

    public GPSReading(double _lat, double _lon, long _timestamp) {
        super((float) _lat, _timestamp);
        this.lat = _lat;
        this.lon = _lon;
    }

    public double getLat() { return this.lat;}

    public double getLon() { return this.lon;}
}
