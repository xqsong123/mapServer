package com.map.svc;

import com.map.entity.Global;
import com.map.utils.FileTools;
import com.map.utils.GeoToolsUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

public class ServiceInitializer {

    private static Log log = LogFactory.getLog(ServiceInitializer.class);


    /**
     * 初始化服务，将人口、案件等对应的点信息作为缓存数据存储，文件的地址读取配置文件
     */
    public static void init() {

        log.info("读取文件路径配置文件");

        //读取properties配置文件
        String path = "/file.properties";
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        try {
            linkedHashMap = FileTools.inputFile(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        GeoToolsUtils geoTools = new GeoToolsUtils();

        log.info("山东省边界数据");
       String boundaryFilePath = (String) linkedHashMap.get("shandongborder");
        try {
            List<Geometry> roadLst = geoTools.readSHP(boundaryFilePath, "GBK", null, null);
            Geometry line = (Geometry) roadLst.get(0);
            Global.polygon = new GeometryFactory().createPolygon(new GeometryFactory().createLinearRing(line.getCoordinates()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        //log.info("存储城市边界数据");
        //将城市边界数据存入缓存
     /*   String boundaryPath = (String) linkedHashMap.get("boundary");
        List<String> cityLst = Arrays.asList(new String[]{"city_id", "city_cid", "cname", "cename"});
        try {
            Global.cityLst = geoTools.readSHP(boundaryPath, "GBK", cityLst, null);
        } catch (Exception e) {
            e.printStackTrace();
        }*/

       /* log.info("存储人口数据");
        //将人口数据存入缓存
        String populationPath = (String) linkedHashMap.get("population");
        List<String> populationLst = Arrays.asList(new String[]{"ADMINNAME", "ADDRESS", "residence", "age", "sex"});
        Global.popLst = geoTools.getPoints(populationPath, "GBK", populationLst);

        log.info("存储案件数据");
        String casePath = (String) linkedHashMap.get("case");
        List<String> caseLst = Arrays.asList(new String[]{"ADMINNAME", "ADDRESS", "level"});
        Global.caseLst = geoTools.getPoints(casePath, "GBK", caseLst);*/

       /* log.info("存储派出所辖区数据");
        String areaPath = (String) linkedHashMap.get("area");
        List<String> areaLst = Arrays.asList(new String[]{"","MC","ZZJGDM","SZDDZ","SSJGMC","SSJGDM","LX","GXSJ","SHAPE_AREA","SHAPE_LEN","ID"});
        try {
            Global.areaLst =  geoTools.readSHP(areaPath, "GBK", areaLst,null);
        } catch (Exception e) {
            e.printStackTrace();
        }*/

       /* log.info("民警日常数据");
        String policeDailyPath = (String) linkedHashMap.get("policeDaily");
        List<String> dailyLst = Arrays.asList(new String[]{"","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","MJRC"});
        Global.policeDailyLst = geoTools.getPoints(policeDailyPath, "GBK", dailyLst);

        log.info("摄像头数据");
        String cameraPath = (String) linkedHashMap.get("cameraPath");
        Global.cameraNumLst = geoTools.getPoints(cameraPath, "GBK", null);

        log.info("初始化路网数据");
        String rootPath = (String) linkedHashMap.get("road");
        DataRestServiceImpl.resetRoadData(rootPath);


        log.info("报警数据");
        String alarmPath = (String) linkedHashMap.get("alarmPath");
        List<String> alarmLst = Arrays.asList(new String[]{"","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","BJSJ"});
        Global.alarmDataLst = geoTools.getPoints(alarmPath, "GBK", alarmLst);*/

    }

}
