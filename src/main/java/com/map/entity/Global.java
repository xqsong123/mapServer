package com.map.entity;

import com.map.utils.GeoCreatorByLtc;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 本实体类创建工程的全局变量
 */
public class Global {
    //geometryCreator
    public static GeoCreatorByLtc geoCreatorByLtc = GeoCreatorByLtc.getInstance();

    //存储城市边界的List
    public static List<Geometry> cityLst = new ArrayList<>();

    //存储总人口点的List
    public static List<Point> popLst = new ArrayList<>();

    //存储总案件点的List
    public static List<Point> caseLst = new ArrayList<>();

    //辖区密度的List
    //public static List<Geometry> areaLst = new ArrayList<>();

    //民警日常点的List
    public static List<Point> policeDailyLst = new ArrayList<>();

    //摄像头点的List
    public static List<Point> cameraNumLst = new ArrayList<>();

    //报警点的List
    public static List<Point> alarmDataLst = new ArrayList<>();

    //redis实例
    //public static Jedis jedis = JedisPoolUtils.getJedis();

    //路网数据，在服务初始化时读取到全局变量
    public static Map<String, LineString> roadMap = new HashMap();

    //山东省的面对象
    public static Polygon polygon;

}
