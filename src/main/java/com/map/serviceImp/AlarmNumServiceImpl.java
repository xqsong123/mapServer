package com.map.serviceImp;

import com.map.entity.Global;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlarmNumServiceImpl {

    private Log log = LogFactory.getLog(AlarmNumServiceImpl.class);

    /**
     * 得到在多边形中所有报警点的集合
     *
     * @param pol 多边形的顶点
     * @return 在多边形内的所有报警点的集合
     */
    public List<double[]> getAllAlarmLst(String pol, List<Point> pointList) throws Exception {
        Polygon p = Global.geoCreatorByLtc.createPolygonByWKT(pol);
        List<double[]> pLst = new ArrayList<double[]>();
        for (int i = 0; i < pointList.size(); i++) {
            Object obj = pointList.get(i);
            Point pt = (Point) obj;
            if (p.contains(pt)) {
                double[] d = new double[]{pt.getX(), pt.getY()};
                pLst.add(d);
            }
        }
        return pLst;
    }

    /**
     * 获取动态报警数据
     *
     * @param pointList 所有报警点数据集合
     * @return 在多边形内的动态报警数据集合
     */
    public Map<String, Object> getDynamicAlarmPoints(List<double[]> pointList, int n) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        for (int m = 0; m < n; m++) {
            List<double[]> list = new ArrayList<double[]>();
            for (int i = 0; i < pointList.size(); i += 2 * n / (m + 1)) {
                list.add(pointList.get(i));
            }
            map.put("list" + m, list);
        }
        return map;
    }

}
