package com.map.serviceImp;

import com.map.entity.Global;
import com.map.entity.Lonlat;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

import java.util.*;

public class PoliceDailyServiceImpl {
    private Log log = LogFactory.getLog(PoliceDailyServiceImpl.class);

    /**
     * 得到在多边形中所有民警日常点集合
     *
     * @param pol 多边形的顶点
     * @return 在多边形内的所有民警日常点集合
     */
    public Map<String, Object> getPoliceDailys(String pol, List<Point> pointList) throws Exception {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        Polygon p = Global.geoCreatorByLtc.createPolygonByWKT(pol);
        Set taskLst = new HashSet<>();
        Set cluesLst = new HashSet<>();
        Set casesLst = new HashSet<>();
        Set residenceLst = new HashSet<>();
        Set immigrationLst = new HashSet<>();
        Set helpLst = new HashSet<>();
        Set publicPreLst = new HashSet<>();

        for (int i = 0; i < pointList.size(); i++) {
            Object obj = pointList.get(i);
            Point pt = (Point) obj;
            if (p.contains(pt)) {
                Map map = (Map) pt.getUserData();
                int resdValue = Integer.valueOf(map.get("MJRC").toString()).intValue();
                if (resdValue == 7) {//待办任务
                    Lonlat lonlat = new Lonlat();
                    lonlat.setLon(pt.getX());
                    lonlat.setLat(pt.getY());
                    taskLst.add(lonlat);
                } else if (resdValue == 8) {//情报线索
                    Lonlat lonlat = new Lonlat();
                    lonlat.setLon(pt.getX());
                    lonlat.setLat(pt.getY());
                    cluesLst.add(lonlat);
                } else if (resdValue == 9) {//将到期案件
                    Lonlat d = new Lonlat();
                    d.setLon(pt.getX());
                    d.setLat(pt.getY());
                    casesLst.add(d);
                } else if (resdValue == 10) {//居住证到期
                    Lonlat d = new Lonlat();
                    d.setLon(pt.getX());
                    d.setLat(pt.getY());
                    residenceLst.add(d);
                } else if (resdValue == 11) {//常口迁入
                    Lonlat d = new Lonlat();
                    d.setLon(pt.getX());
                    d.setLat(pt.getY());
                    immigrationLst.add(d);
                } else if (resdValue == 12) {//群众求助
                    Lonlat d = new Lonlat();
                    d.setLon(pt.getX());
                    d.setLat(pt.getY());
                    helpLst.add(d);
                } else if (resdValue == 13) {//治安防范
                    Lonlat d = new Lonlat();
                    d.setLon(pt.getX());
                    d.setLat(pt.getY());
                    publicPreLst.add(d);
                }
            }
        }
        resultMap.put("taskLst", taskLst);
        resultMap.put("taskNum", taskLst.size());
        resultMap.put("cluesLst", cluesLst);
        resultMap.put("cluesNum", cluesLst.size());
        resultMap.put("casesLst", casesLst);
        resultMap.put("casesNum", casesLst.size());
        resultMap.put("residenceLst", residenceLst);
        resultMap.put("residenceNum", residenceLst.size());
        resultMap.put("immigrationLst", immigrationLst);
        resultMap.put("immigrationNum", immigrationLst.size());
        resultMap.put("helpLst", helpLst);
        resultMap.put("helpNum", helpLst.size());
        resultMap.put("publicPreLst", publicPreLst);
        resultMap.put("publicPreNum", publicPreLst.size());

        return resultMap;
    }
}
