package com.map.entity;

import java.util.Objects;

public class Lonlat {
    private double lon;//经度
    private double lat;//纬度

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lonlat lonlat = (Lonlat) o;
        return Double.compare(lonlat.lon, lon) == 0 &&
                Double.compare(lonlat.lat, lat) == 0;
    }

    @Override
    public String toString() {
        return "Lonlat{" +
                "lon=" + lon +
                ", lat=" + lat +
                '}';
    }

    @Override
    public int hashCode() {

        return Objects.hash(lon, lat);
    }
}
