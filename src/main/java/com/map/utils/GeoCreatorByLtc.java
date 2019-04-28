package com.map.utils;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

import java.util.List;
import java.util.Map;

/**
 * 本类采用org.locationtech jar作为构建geometry工具
 */
public class GeoCreatorByLtc {

    /**
     * WKT(Well-known text)是一种文本标记语言，用于表示矢量几何对象、空间参照系统及空间参照系统之间的转换
     * POINT(6 10)
     * LINESTRING(3 4,10 50,20 25)
     * POLYGON((1 1,5 1,5 5,1 5,1 1),(2 2,2 3,3 3,3 2,2 2))
     * MULTIPOINT(3.5 5.6, 4.8 10.5)
     * MULTILINESTRING((3 4,10 50,20 25),(-5 -8,-10 -8,-15 -4))
     * MULTIPOLYGON(((1 1,5 1,5 5,1 5,1 1),(2 2,2 3,3 3,3 2,2 2)),((6 3,9 2,9 4,6 3)))
     * GEOMETRYCOLLECTION(POINT(4 6),LINESTRING(4 6,7 10))
     */

    private Log log = LogFactory.getLog(GeoCreatorByLtc.class);

    public static GeoCreatorByLtc geoCreatorByLtc = null;
    private GeometryFactory geometryFactory = new GeometryFactory();

    private GeoCreatorByLtc() {
    }

    /**
     * 返回本类的唯一实例
     * @return
     */
    public static GeoCreatorByLtc getInstance() {
        if (geoCreatorByLtc == null) {
            return new GeoCreatorByLtc();
        }
        return geoCreatorByLtc;
    }

    /**
     * 构建点
     */
    public Point createPoint(double x, double y, Map attrsMap){
        Coordinate coor = new Coordinate(x, y);
        Point p = geometryFactory.createPoint(coor);
        if(attrsMap != null){
            p.setUserData(attrsMap);
        }
        return p;
    }

    public Point createPointByWKT(String PointWKT, Map attrsMap) throws ParseException {
        WKTReader reader = new WKTReader(geometryFactory);
        Point p = (Point) reader.read(PointWKT);
        if(attrsMap != null){
            p.setUserData(attrsMap);
        }
        return p;
    }


    public MultiPoint createMulPointByWKT(String MPointWKT)throws ParseException{
        WKTReader reader = new WKTReader( geometryFactory );
        MultiPoint mpoint = (MultiPoint) reader.read(MPointWKT);
        return mpoint;
    }


    public LineString createLine(double ax,double ay,double bx,double by){
        Coordinate[] coords  = new Coordinate[] {new Coordinate(ax, ay), new Coordinate(bx, by)};
        LineString line = geometryFactory.createLineString(coords);
        return line;
    }


    public LineString createLineByWKT(String LineStringWKT) throws ParseException{
        WKTReader reader = new WKTReader( geometryFactory );
        LineString line = (LineString) reader.read(LineStringWKT);
        return line;
    }


    public MultiLineString createMLine(List<Coordinate[]> list){

        MultiLineString ms = null;


        if(list == null){
            return ms;
        }

        LineString[] lineStrings = new LineString[list.size()];


//      Coordinate[] coords1  = new Coordinate[] {new Coordinate(2, 2), new Coordinate(2, 2)};
//      LineString line1 = geometryFactory.createLineString(coords1);
//
//      Coordinate[] coords2  = new Coordinate[] {new Coordinate(2, 2), new Coordinate(2, 2)};
//      LineString line2 = geometryFactory.createLineString(coords2);

        int i = 0;
        for (Coordinate[] coordinates : list) {
            lineStrings[i] = geometryFactory.createLineString(coordinates);
        }

        ms = geometryFactory.createMultiLineString(lineStrings);

        return ms;
    }



    public MultiLineString createMLineByWKT(String MLineStringWKT)throws ParseException{
        WKTReader reader = new WKTReader( geometryFactory );
        MultiLineString line = (MultiLineString) reader.read(MLineStringWKT);
        return line;
    }


    public Polygon createPolygonByWKT(String PolygonWKT) throws ParseException{
        WKTReader reader = new WKTReader( geometryFactory );
        Polygon polygon = (Polygon) reader.read(PolygonWKT);
        return polygon;
    }

    public MultiPolygon createMulPolygonByWKT(String MPolygonWKT) throws ParseException{
        WKTReader reader = new WKTReader( geometryFactory );
        MultiPolygon mpolygon = (MultiPolygon) reader.read(MPolygonWKT);
        return mpolygon;
    }


    public MultiPolygon createMulPolygonByPolygon(Polygon[] polygons) throws ParseException{

        return geometryFactory.createMultiPolygon(polygons);
    }


    public GeometryCollection createGeoCollect(Geometry[] geoArray) throws ParseException{
//            LineString line = createLine(125.12,25.4,85.63,99.99);
//            Polygon poly    =  createPolygonByWKT("POLYGON((20 10, 30 0, 40 10, 30 20, 20 10))");
//            Geometry g1     = geometryFactory.createGeometry(line);
//            Geometry g2     = geometryFactory.createGeometry(poly);
//            Geometry[] geoArray = new Geometry[]{g1,g2};
        GeometryCollection gc = geometryFactory.createGeometryCollection(geoArray);
        return gc;
    }

    /**
     * create a Circle  创建一个圆，圆心(x,y) 半径RADIUS
     * @param x
     * @param y
     * @param RADIUS
     * @return
     */
    public Polygon createCircle(double x, double y, final double RADIUS){
        final int SIDES = 32;//圆上面的点个数
        Coordinate coords[] = new Coordinate[SIDES+1];
        for( int i = 0; i < SIDES; i++){
            double angle = ((double) i / (double) SIDES) * Math.PI * 2.0;
            double dx = Math.cos( angle ) * RADIUS;
            double dy = Math.sin( angle ) * RADIUS;
            coords[i] = new Coordinate( (double) x + dx, (double) y + dy );
        }
        coords[SIDES] = coords[0];
        //线性环
        LinearRing ring = geometryFactory.createLinearRing(coords);
        Polygon polygon = geometryFactory.createPolygon(ring, null);
        return polygon;
    }



    public LinearRing createLinearRingByWKT(String ringWKT) throws ParseException{
        WKTReader reader = new WKTReader( geometryFactory );
        LinearRing ring = (LinearRing) reader.read(ringWKT);
        return ring;
    }
}
