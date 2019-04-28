package com.map.dto;

import org.locationtech.jts.geom.Coordinate;

/**
 * @author xqsong
 * @create 2018/12/27
 * @since 1.0.0
 **/
public class CoordinateDto {
    private double x;
    private double y;

    public CoordinateDto() {
    }

    public CoordinateDto(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public CoordinateDto(Coordinate coordinate){
        this.x = coordinate.x;
        this.y = coordinate.y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
}
