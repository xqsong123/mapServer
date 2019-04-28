package com.map.dto;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;

import java.util.List;

/**
 * @author xqsong
 * @create 2018/12/27
 * @since 1.0.0
 **/
public class GeometryDto {
    private CoordinateDto[] coordinates;
    private String type;
    private Object userData;
    private boolean startPoint;

    public GeometryDto() {
    }

    public GeometryDto(CoordinateDto[] coordinates, String type, Object userData) {
        this.coordinates = coordinates;
        this.type = type;
        this.userData = userData;
        this.startPoint = false;
    }

    public GeometryDto(Geometry geometry){
        Coordinate[] coordinates = geometry.getCoordinates();
        this.coordinates = new CoordinateDto[coordinates.length];
        for (int i = 0; i < coordinates.length; i++) {
            this.coordinates[i] = new CoordinateDto(coordinates[i]);
        }
        this.type = geometry.getGeometryType();
        this.startPoint = false;
        this.userData = geometry.getUserData();
        List<String> ids = (List<String>)geometry.getUserData();
        if (ids != null) {
            for (String s : ids) {
                if ("true".equals(s)) {
                    this.startPoint = true;
                    break;
                }
            }
        }
    }

    public CoordinateDto[] getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(CoordinateDto[] coordinates) {
        this.coordinates = coordinates;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getUserData() {
        return userData;
    }

    public void setUserData(Object userData) {
        this.userData = userData;
    }

    public boolean isStartPoint() {
        return startPoint;
    }

    public void setStartPoint(boolean startPoint) {
        this.startPoint = startPoint;
    }
}
