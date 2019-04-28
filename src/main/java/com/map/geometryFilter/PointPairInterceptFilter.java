package com.map.geometryFilter;

import com.map.service.RoutePlanningService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.locationtech.jts.algorithm.distance.DistanceToPoint;
import org.locationtech.jts.algorithm.distance.PointPairDistance;
import org.locationtech.jts.geom.*;

import java.util.LinkedList;
import java.util.List;

/**
 * 线段过滤器，截取一段线段中给定两点之间的线段。给定的两点应在原始线段上。
 * 默认生成线段的起点与终点
 * @author xqsong
 * @create 2018/12/25
 * @since 1.0.0
 **/
public class PointPairInterceptFilter implements GeometryFilter {

    private final Log log = LogFactory.getLog(PointPairInterceptFilter.class);

    private final GeometryFactory geometryFactory;
    private Point startPoint;
    private Point endPoint;
    private boolean startPointFlag;
    private boolean endPointFlag;
    List<Coordinate> coordinateList;
    private LineString lineString;
    List<Coordinate> remainCoordinateList;
    private LineString remainLingString;

    public PointPairInterceptFilter(){
        geometryFactory = new GeometryFactory();
        coordinateList = new LinkedList<>();
        remainCoordinateList = new LinkedList<>();
        lineString = null;
        remainLingString = null;
    }

    public PointPairInterceptFilter(Coordinate coordinate1, Coordinate coordinate2) {
        this();
        startPoint = geometryFactory.createPoint(coordinate1);
        endPoint = geometryFactory.createPoint(coordinate2);
        startPointFlag = true;
        endPointFlag = true;
    }

    public PointPairInterceptFilter(Point point1, Point point2) {
        this(point1.getInteriorPoint().getCoordinate(), point2.getInteriorPoint().getCoordinate());
    }

    public PointPairInterceptFilter(double x1, double y1, double x2, double y2) {
        this(new Coordinate(x1, y1), new Coordinate(x2, y2));
    }

    public PointPairInterceptFilter(Coordinate coordinate1, Coordinate coordinate2, boolean startPointFlag, boolean endPointFalg) {
        this();
        startPoint = geometryFactory.createPoint(coordinate1);
        endPoint = geometryFactory.createPoint(coordinate2);
        this.startPointFlag = startPointFlag;
        this.endPointFlag = endPointFalg;
    }

    public PointPairInterceptFilter(double x1, double y1, double x2, double y2, boolean startPointFlag, boolean endPointFalg) {
        this(new Coordinate(x1, y1), new Coordinate(x2, y2), startPointFlag, endPointFalg);
    }

    public void reset() {
        coordinateList = null;
        lineString = null;
    }

    public void reset(Coordinate coordinate1, Coordinate coordinate2) {
        startPoint = geometryFactory.createPoint(coordinate1);
        endPoint = geometryFactory.createPoint(coordinate2);
        coordinateList = null;
        lineString = null;
        remainLingString = null;
    }

    public void reset(double x1, double y1, double x2, double y2) {
        startPoint = geometryFactory.createPoint(new Coordinate(x1, y1));
        endPoint = geometryFactory.createPoint(new Coordinate(x2, y2));
        coordinateList = null;
        lineString = null;
        remainLingString = null;
    }

    public void reset(Coordinate coordinate1, Coordinate coordinate2, boolean startPointFlag, boolean endPointFalg) {
        reset(coordinate1, coordinate2);
        this.startPointFlag = startPointFlag;
        this.endPointFlag = endPointFalg;
    }

    public void reset(double x1, double y1, double x2, double y2, boolean startPointFlag, boolean endPointFalg) {
        reset(x1, y1, x2, y2);
        this.startPointFlag = startPointFlag;
        this.endPointFlag = endPointFalg;
    }

    public LineString getLineString() {
        return lineString;
    }

    public List<Coordinate> getCoordinateList(){ return coordinateList; }

    public LineString getRemainLingString() {
        return remainLingString;
    }

    public List<Coordinate> getRemainCoordinateList() {
        return remainCoordinateList;
    }

    public void setRemainCoordinateList(List<Coordinate> remainCoordinateList) {
        this.remainCoordinateList = remainCoordinateList;
    }

    @Override
    public void filter(Geometry geom) {
        int startIndex = -1;
        int endIndex = -1;
        if (geom == null) {
            return;
        }
        boolean isRing = false;
        if (geom instanceof LineString && ((LineString)geom).isRing()) {
            isRing = true;
        }

        if (startPoint.equalsExact(endPoint, 0)) {
            return;
        }
        if (!RoutePlanningService.isOnLine(geom,startPoint)) {
            PointPairDistance pointPairDistance = new PointPairDistance();
            DistanceToPoint.computeDistance(geom, startPoint.getCoordinate(), pointPairDistance);
            startPoint = new GeometryFactory().createPoint(pointPairDistance.getCoordinate(0));
        }
//        if (!RoutePlanningService.isOnLine(geom,endPoint)) {
//            log.warn("点：" + endPoint.getCoordinate().toString() + " 不在线段上，无法截取！");
//            return;
//        }

        Coordinate[] coordinates = geom.getCoordinates();
        for (int i = 0; i < coordinates.length - 1; i++) {
            if (startPoint.getX() >= Math.min(coordinates[i].x, coordinates[i + 1].x) &&
                    startPoint.getX() <= Math.max(coordinates[i].x, coordinates[i + 1].x) &&
                    startPoint.getY() >= Math.min(coordinates[i].y, coordinates[i + 1].y) &&
                    startPoint.getY() <= Math.max(coordinates[i].y, coordinates[i + 1].y)) {
                startIndex = i;
            }
            if (endPoint.getX() >= Math.min(coordinates[i].x, coordinates[i + 1].x) &&
                    endPoint.getX() <= Math.max(coordinates[i].x, coordinates[i + 1].x) &&
                    endPoint.getY() >= Math.min(coordinates[i].y, coordinates[i + 1].y) &&
                    endPoint.getY() <= Math.max(coordinates[i].y, coordinates[i + 1].y)) {
                endIndex = i;
            }
        }
        if (startIndex < endIndex){
            for (int i = startIndex + 1; i < endIndex + 1; i++) {
                coordinateList.add(coordinates[i]);
            }
            if (isRing) {
                if (startIndex >= 0) {
                    for (int i = startIndex; i >= 0; i--) {
                        remainCoordinateList.add(coordinates[i]);
                    }
                }
                if (endIndex <= geom.getNumPoints()) {
                    for (int i = geom.getNumPoints() - 1; i > endIndex; i--) {
                        remainCoordinateList.add(coordinates[i]);
                    }
                }
            }
        } else {
            for (int i = startIndex; i > endIndex; i--) {
                coordinateList.add(coordinates[i]);
            }
            if (isRing) {
                if (startIndex <= geom.getNumPoints()){
                    for (int i = startIndex + 1; i < geom.getNumPoints(); i++) {
                        remainCoordinateList.add(coordinates[i]);
                    }
                }
                if (endIndex >= 0){
                    for (int i = 0; i < endIndex + 1; i++) {
                        remainCoordinateList.add(coordinates[i]);
                    }
                }
            }
        }
        if (startPointFlag) {
            if (!coordinateList.contains(startPoint.getCoordinate())) {
                ((LinkedList<Coordinate>) coordinateList).addFirst(startPoint.getCoordinate());
            }
            if (isRing) {
                if (!remainCoordinateList.contains(startPoint.getCoordinate())) {
                    ((LinkedList<Coordinate>) remainCoordinateList).addFirst(startPoint.getCoordinate());
                }
            }
        } else {
            if (coordinateList.contains(startPoint.getCoordinate())) {
                coordinateList.remove(0);
            }
            if (isRing) {
                if (remainCoordinateList.contains(startPoint.getCoordinate())) {
                    remainCoordinateList.remove(0);
                }
            }
        }
        if (endPointFlag) {
            if (!coordinateList.contains(endPoint.getCoordinate())) {
                ((LinkedList<Coordinate>) coordinateList).addLast(endPoint.getCoordinate());
            }
            if (isRing) {
                if (!remainCoordinateList.contains(endPoint.getCoordinate())) {
                    ((LinkedList<Coordinate>) remainCoordinateList).addLast(endPoint.getCoordinate());
                }
            }
        } else {
            if (coordinateList.contains(endPoint.getCoordinate())) {
                coordinateList.remove(coordinateList.size() - 1);
            }
            if (isRing) {
                if (remainCoordinateList.contains(endPoint.getCoordinate())) {
                    remainCoordinateList.remove(coordinateList.size() - 1);
                }
            }
        }
        Coordinate[] coordinates1 = new Coordinate[coordinateList.size()];
        coordinateList.toArray(coordinates1);
        this.lineString = new GeometryFactory().createLineString(coordinates1);

        if (isRing) {
            Coordinate[] coordinates2 = new Coordinate[remainCoordinateList.size()];
            remainCoordinateList.toArray(coordinates2);
            this.remainLingString = new GeometryFactory().createLineString(coordinates2);
        }

    }
}
