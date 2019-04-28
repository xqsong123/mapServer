package com.map.serviceImp;

import com.map.entity.Global;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

import java.util.ArrayList;
import java.util.List;

public class CameraNumServiceImpl {
    private Log log = LogFactory.getLog(CameraNumServiceImpl.class);

    /**
     * 得到在一定范围内所有摄像头的点集合
     *
     * @param pol 多边形的顶点
     * @return 在一定范围内的所有摄像头的点集合
     */
    public List<double[]> getCameraNums(String pol, List<Point> pointList) throws Exception {
        Polygon p = Global.geoCreatorByLtc.createPolygonByWKT(pol);
        List<double[]> pLst = getCameraInPolygon(p, pointList);
        return pLst;
    }

    /**
     * 得到在多边形或者圆中所有摄像头的点集合
     *
     * @param p 多边形
     * @return 在多边形或者圆内的所有摄像头的点集合
     */
    public List<double[]> getCameraInPolygon(Polygon p, List<Point> pointList) throws Exception {
        List<double[]> camerasLst = new ArrayList<double[]>();
        for (int i = 0; i < pointList.size(); i++) {
            Object obj = pointList.get(i);
            Point pt = (Point) obj;
            if (p.contains(pt)) {
                double[] d = new double[]{pt.getX(), pt.getY()};
                camerasLst.add(d);
            }
        }
        return camerasLst;
    }


}
