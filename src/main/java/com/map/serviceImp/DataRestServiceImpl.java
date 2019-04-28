package com.map.serviceImp;

import com.map.entity.Global;
import com.map.utils.GeoToolsUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.locationtech.jts.algorithm.distance.DistanceToPoint;
import org.locationtech.jts.algorithm.distance.PointPairDistance;
import org.locationtech.jts.geom.*;

import java.io.File;
import java.util.*;

public class DataRestServiceImpl {

    private static Log log = LogFactory.getLog(DataRestServiceImpl.class);

    private static GeoToolsUtils geoTools = new GeoToolsUtils();

    /**
     * rootPath是文件夹路径，会读取整个文件夹中的shp文件
     * isPropsNeed是判断是否需要属性字段
     * @param rootPath
     * @param isPropsNeed
     */
    public static List<Geometry> shpDataInit(String rootPath, boolean isPropsNeed) {

        File f = new File(rootPath);
        if (!f.exists()) {
            log.error(rootPath + " not exists");
            return null;
        }
        List<String> fileNames = new ArrayList();
        File fa[] = f.listFiles();
        for (int i = 0; i < fa.length; i++) {
            File fs = fa[i];
            if (fs.isDirectory()) {
                log.info("文件夹就先不获取了");
            } else {
                fileNames.add(fs.getName());
            }
        }

        List<String> shpLst = new ArrayList<>();
        if (fileNames.size() > 0) {
            for (String s : fileNames) {
                if (s.indexOf(".shp") > 0 && (!s.contains(".xml"))) {
                    shpLst.add(s);
                }
            }
        }


        List<Geometry> gLst = new ArrayList<>();

        if (shpLst != null && shpLst.size() > 0) {

            for (String shp : shpLst) {

                File file = new File(rootPath + File.separator + shp);
                String shpPath = file.getPath();
                System.out.println("开始读取文件" + shpPath);
                List<String> propLst = null;
                if (isPropsNeed) {
                    propLst = geoTools.getShpProps(shpPath);
                }
                try {

                    List<Geometry> geometries = geoTools.readSHP(shpPath, "GBK", propLst, null);
                    if (geometries != null) {
                        gLst.addAll(geometries);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                System.out.println("结束读取文件" + shpPath);
            }

        }

        return gLst;
    }

    public static void resetRoadData(String rootPath){
        if(StringUtils.isBlank(rootPath)){
            log.info("重置路网数据的文件夹为空");
        }
        List<Geometry> gLst = shpDataInit(rootPath, false);
        //Global.roadMap 是以文件名称为key， 以文件中所有shp文件的内容作为value
        //Global.roadMap.put(shp.split(".shp")[0], geometries);
        if(gLst != null && gLst.size() > 0){
            int count = 0;
            for(Geometry g : gLst){
                System.out.println("共有" + gLst.size() + "个几何, 处理第" + count++ + "个");
                String uuid = UUID.randomUUID().toString();
                if(g != null && g.getGeometryType().equals("MultiLineString")){
                    int num = g.getNumGeometries();
                    for(int i = 0; i < num; i++){
                        LineString innerGeo = (LineString) g.getGeometryN(i);
                        String innerUuid = UUID.randomUUID().toString();
                        Global.roadMap.put(innerUuid, innerGeo);
                    }
                }else if(g != null && g.getGeometryType().equals("LineString")){
                    Global.roadMap.put(uuid, (LineString) g);
                }

            }
        }

        //modifyRoadData(Global.roadMap);

    }

    /**
     * 判断路与路之间是否相交，如果不交路的端点距另一条路的距离是否小于阈值
     */
    public static void modifyRoadData(Map<String, LineString> roadMap){

        double D = 4.744684910340124E-8;

        Iterator it = roadMap.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry entity = (Map.Entry) it.next();
            String key = (String) entity.getKey();
            LineString val = (LineString) entity.getValue();
            Iterator innerIt = roadMap.entrySet().iterator();
            while(innerIt.hasNext()){
                Map.Entry innerEntity = (Map.Entry) innerIt.next();
                String innerKey = (String) innerEntity.getKey();
                LineString innerVal = (LineString) innerEntity.getValue();

                if(key.equals(innerKey) || (!val.getEnvelopeInternal().intersects(innerVal.getEnvelopeInternal())) ||
                        (val.intersects(innerVal))){
                    continue;
                }
                Coordinate inner_start = innerVal.getStartPoint().getCoordinate();
                Coordinate inner_end = innerVal.getEndPoint().getCoordinate();
                PointPairDistance pointPairDistance_start = new PointPairDistance();
                DistanceToPoint.computeDistance(val, inner_start, pointPairDistance_start);
                double d_start = pointPairDistance_start.getDistance();
                Coordinate[] cs_start = pointPairDistance_start.getCoordinates();
                LineString newLine = innerVal;
                if(d_start <= D){
                    newLine = jointCoorToLine("start", newLine, cs_start[0]);
                    newLine.setUserData(cs_start[0]);
                }

                PointPairDistance pointPairDistance_end = new PointPairDistance();
                DistanceToPoint.computeDistance(val, inner_end, pointPairDistance_end);
                double d_end = pointPairDistance_end.getDistance();
                Coordinate[] cs_end = pointPairDistance_end.getCoordinates();
                if(d_end <= D && 0 < d_end){
                    newLine = jointCoorToLine("end", newLine, cs_end[0]);
                }

                roadMap.put(innerKey, newLine);
                Global.roadMap.put(innerKey, newLine);
            }
        }
    }

    public static LineString jointCoorToLine(String index, LineString ls, Coordinate c){

        Coordinate[] ls_coors = ls.getCoordinates();
        Coordinate[] new_coors = new Coordinate[ls_coors.length + 1];
        if(index.equals("start")){
            new_coors[0] = c;
            for(int i = 0; i < ls_coors.length; i++){
                new_coors[i+1] = ls_coors[i];
            }
        }
        if(index.equals("end")){
            for(int j = 0; j < ls_coors.length; j++){
                new_coors[j] = ls_coors[j];
            }
            new_coors[ls_coors.length] = c;
        }
        GeometryFactory geometryFactory = new GeometryFactory();
        LineString newl = geometryFactory.createLineString(new_coors);

        return newl;
    }
}
