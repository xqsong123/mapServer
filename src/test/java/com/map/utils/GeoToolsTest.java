package com.map.utils;

import com.map.entity.Global;


import org.locationtech.jts.geom.*;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.json.simple.JSONArray;

import org.junit.Test;

import org.locationtech.jts.io.ParseException;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;


import java.io.File;
import java.nio.charset.Charset;
import java.util.*;

public class GeoToolsTest {
    private GeometryCreator geoCreater = GeometryCreator.getInstance();

    private Polygon screenPoly = geoCreater.createPolygonByWKT("POLYGON ((113.5645488281254 34.11614013006066, " +
            "113.5645488281254 38.37160783702575, 124.08395800781523 38.37160783702575, 124.08395800781523 34.11614013006066,113.5645488281254 34.11614013006066 ))");

    public GeoToolsTest() throws ParseException {
    }

    @Test
    public void testReadShp() throws Exception {

        String path0 = "F:\\mapData\\shandong\\binzhou\\GHFCPT.shp";
        String path1 = "D:\\mapbox\\数据\\zjdemo_shp\\HYD_LN.shp";
        String path = "D:\\mapbox\\数据\\zjdemo_shp\\HYD_PY.shp";
        String path4 = "D:\\mapbox\\数据\\zjdemo_shp\\HYD_PT.shp";
        String path2 = "E:\\mapbox\\data\\mapData\\shandong\\1024\\highway0.001\\GROALN_GAOGUO_0.001.shp";
        String path3 = "F:\\mapData\\shandong_1029\\shandong\\1009\\SD_POI_LEVEL\\SD_POI_LEVEL7_1009.shp";
        //获取开始时间
        long startTime = System.currentTimeMillis();
        String path5 = "F:\\mapData\\shandong\\1009\\SD_POI\\SD_POI.shp";
        String path6 = "F:\\mapData\\shandong_1029\\shandong\\1009\\SD_POI_LEVEL\\SD_POI_LEVEL7_1009.shp";
        String path7 = "F:\\mapData\\fromGDB\\fromGDB_20181031\\shengGDBt.shp";
        String path8 = "F:\\mapData\\G2.shp";
        String path9 = "D:\\人口案件数据\\人口\\population.shp";
        ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();
        try {
            ShapefileDataStore sds = (ShapefileDataStore)dataStoreFactory.createDataStore(new File(path9).toURI().toURL());
            sds.setCharset(Charset.forName("GBK"));
            SimpleFeatureSource featureSource = sds.getFeatureSource();
            SimpleFeatureIterator itertor = featureSource.getFeatures().features();
            List<Point> pLst = new ArrayList<>();
            while(itertor.hasNext()) {
                SimpleFeature feature = itertor.next();
                List<Object> list = feature.getAttributes();
                //String aaa = feature.;
                Object obj = list.get(0);
                if(obj instanceof Point || obj instanceof MultiPoint){
                    Point pt = (Point) obj;
                    //Point pp = geoCreater.createPointByWKT(pt.toString(), null);
                    if(screenPoly.contains(pt)){
                        /*List<AttributeType> attrlst = feature.getFeatureType().getTypes();
                        for(AttributeType attr : attrlst){
                            Name name = attr.getName();
                            String local = name.getLocalPart();
                        }*/
                        Map attrMap = new HashMap();
                        attrMap.put("NAME", list.get(1));
                        attrMap.put("KIND", list.get(2));
                        pt.setUserData(attrMap);
                        pLst.add(pt);
                    }
                    /*System.out.println(pt.toString());
                    Coordinate c = pt.getCoordinate();
                    double[] xy = WGS_Encrypt.WGS2Mars(c.y, c.x);
                    c.setCoordinate(new Coordinate(xy[1], xy[0]));*/
                }
                if(obj instanceof LineString || obj instanceof MultiLineString){
                    Geometry line = (Geometry) obj;
                    int parts = line.getNumGeometries();
                    for(int i = 0; i < parts; i++){
                        LineString l = (LineString) line.getGeometryN(i);
                        for(int j = 0, num = l.getNumPoints();j < num; j++){
                            Coordinate coor = l.getCoordinateN(j);
                            double[] xy = WGS_Encrypt.WGS2Mars(coor.y, coor.x);
                            Coordinate newc = new Coordinate(xy[1], xy[0]);
                            coor.setCoordinate(newc);
                        }
                    }
                }
                if(obj instanceof  Polygon || obj instanceof MultiPolygon){
                    Geometry polygon = (Geometry) obj;
                    int p = polygon.getNumGeometries();
                    for(int m = 0; m < p; m++){
                        Polygon pol = (Polygon) polygon.getGeometryN(m);
                        int num = pol.getNumPoints();
                        System.out.println(pol.toString());
                        for(int n = 0; n < num; n++){
                            Coordinate[] c = pol.getCoordinates();
                        }
                    }

                }
                Iterator<Property> it = feature.getProperties().iterator();
                while(it.hasNext()) {
                    Property pro = it.next();
                    System.out.println(pro);
                }
            }
            itertor.close();
            sds.dispose();
            long startTime1 = System.currentTimeMillis();
            List<Point> newP = new ArrayList<>();
            if(pLst.size() > 500){
                int rate = pLst.size()/500 - 1;
                int i = 0;
                while(i < pLst.size()){
                    newP.add(pLst.get(i));
                    i += rate;
                }
            }
            //获取结束时间
            long endtime = System.currentTimeMillis();
            System.out.println("取点用时：" +  (endtime-startTime1) + " ms");
            System.out.println(JSONArray.toJSONString(newP).getBytes().length);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testPointInPolygon() throws ParseException {
        String pol = "POLYGON ((10 30, 10 60, 60 60, 60 30, 10 30))";
        String pol2 = "POLYGON ((113.5645488281254 34.11614013006066, " +
                "113.5645488281254 38.37160783702575, 124.08395800781523 38.37160783702575, 124.08395800781523 34.11614013006066,113.5645488281254 34.11614013006066 ))";

        String pol3 = "POLYGON ((117.66593659180745 36.18347549180984, " +
                "117.66593659180745 36.30655239463197,  117.8774234082253 36.30655239463197,  117.8774234082253 36.18347549180984,117.66593659180745 36.18347549180984 ))";
        Polygon p = geoCreater.createPolygonByWKT(pol2);

        String pi = "POINT (118.3016423567325 36.80264760739368)";
        Point po = geoCreater.createPointByWKT(pi, null);

        Point point = geoCreater.createPoint(10, 50, null);

        LineString ls = geoCreater.createLineByWKT("LINESTRING (121.5303977840316 36.83852301592532, 121.53057134689118 36.83874236327034, 121.53121617698605 36.83950130833961, 121.53159917396374 36.83993913248588, 121.532134549371 36.84057226869612, 121.53216345717885 36.84060686111866, 121.53251774599823 36.84103378458241, 121.53302714538756 36.841601932184346)");
        GeometryFactory geomFactory = new GeometryFactory();
        Point[] ps = new Point[2];
        ps[0] = geoCreater.createPoint(20, 40, null);
        ps[1] = geoCreater.createPoint(40, 30, null);
        MultiPoint mp = new MultiPoint(ps, geomFactory);
        String mpi = "MULTIPOINT(20 50, 30 40)";

        String wkt = "POINT(6 10) \n" +
                "LINESTRING(3 4,10 50,20 25) \n" +
                "POLYGON((1 1,5 1,5 5,1 5,1 1),(2 2,2 3,3 3,3 2,2 2)) \n" +
                "MULTIPOINT(3.5 5.6, 4.8 10.5) \n" +
                "MULTILINESTRING((3 4,10 50,20 25),(-5 -8,-10 -8,-15 -4)) \n" +
                "MULTIPOLYGON(((1 1,5 1,5 5,1 5,1 1),(2 2,2 3,3 3,3 2,2 2)),((6 3,9 2,9 4,6 3))) \n" +
                "GEOMETRYCOLLECTION(POINT(4 6),LINESTRING(4 6,7 10)) \n" +
                "POINT ZM (1 1 5 60) \n" +
                "POINT M (1 1 80) \n";
        //MultiPoint mpoint = geoCreater.createMulPointByWKT(mpi);
        System.out.println(mp.toString());
        System.out.println(p.contains(ls));
    }

    @Test
    public void testStoreData() throws Exception {
        FileTools fileTools = new FileTools();
        //读取properties配置文件
        String path = "src/main/resources/file.properties";
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        try {
            linkedHashMap = FileTools.inputFile(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String filePath = (String) linkedHashMap.get("file");
        List<List<String>> value = new ArrayList<>();
        /*List<String> a = new ArrayList<>();
        a.add("{id':'1','name':'guodao','hah':'luekljdklln mn'}");
        value.add(a);*/
        String[] titles = {"id", "name", "hah"};
//读取excel中信息
        List<List<String>> lists = fileTools.readExcel(filePath, 0);
        System.out.println("lists---------------:"+lists);

        //String filePath2 = (String) linkedHashMap.get("writefile");
      /* List<String> list = new ArrayList<>();
        list.add("{'id':'1','name':'xiao1','value','yy'}");
        list.add("{'id':'2','name':'xiao2','value','yy2'}");
        List<List<String>> lists = new ArrayList<>();
        lists.add(list);*/
        //fileTools.creatExcel(lists, titles, "test", filePath2);
        //fileTools.addToExcel(filePath2, 0);


    }

    @Test
    public void getAllPonit() throws Exception {
        //读取properties配置文件
        String path = "src/main/resources/file.properties";
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        try {
            linkedHashMap = FileTools.inputFile(path);
        } catch (Exception e) {
            e.printStackTrace();
        }

        GeoToolsUtils geoTools = new GeoToolsUtils();

        //将人口数据存入缓存
        String populationPath = (String) linkedHashMap.get("population");
        List<String> populationLst = Arrays.asList(new String[]{"ADMINNAME", "ADDRESS", "residence", "age", "sex"});
        Global.popLst = geoTools.getPoints(populationPath, "GBK", populationLst);
        String casePath = (String) linkedHashMap.get("case");
        List<String> caseLst = Arrays.asList(new String[]{"ADMINNAME", "ADDRESS", "level"});
        Global.caseLst = geoTools.getPoints(casePath, "GBK", caseLst);
        String pol = "POLYGON ((617153.8292690597 4075570.898067342, " +
                "578295.8212538885 4078748.644669,577876.4595367892 4074534.2419533217,617153.8292690597 4075570.898067342))";
        /*org.locationtech.jts.geom.Polygon p = Global.geoCreatorByLtc.createPolygonByWKT(pol);
        List<org.locationtech.jts.geom.Point> pLst = new ArrayList<>();
        List<org.locationtech.jts.geom.Point> newLst = new ArrayList<>();
        for (int i = 0; i < Global.popLst.size(); i++) {
            Object obj = Global.popLst.get(i);
            org.locationtech.jts.geom.Point pt = (org.locationtech.jts.geom.Point) obj;
            if (p.contains(pt)) {
                pLst.add(pt);
            }
        }
        System.out.println("newLst1111:---------------:"+pLst);
        while (pLst.size() > 500) {
            newLst.clear();
            for (int j = 0; j < pLst.size(); j += 3) {
                newLst.add(pLst.get(j));
            }
            pLst.clear();
            pLst.addAll(newLst);
        }
       System.out.println("pLst:---------------:"+pLst.size()+";;;;;;;"+"newLst:---------------:"+newLst.size());
        List<org.locationtech.jts.geom.Point> allList = new ArrayList<>();
        for(int m = 0; m < 5; m++){
            List<org.locationtech.jts.geom.Point> list = new ArrayList<>();
            for (int i = 0; i < pLst.size(); i+=2*3/(m+1)) {
                list.add(pLst.get(i));
            }
            allList.addAll(list);
        }
        System.out.println("allList:---------------:"+allList);*/
    }


    @Test
    public void testSet() throws Exception {
        Set<double[]> publicPreLst = new HashSet<double[]>();

        double[] d2 = new double[]{117.04377399186838, 36.64345916823918};
        double[] d3 = new double[]{117.04377399186838, 36.64345916823918};

        publicPreLst.add(d2);
        publicPreLst.add(d3);

        System.out.println("set:---------------:"+publicPreLst);
    }

    @Test
    public void testArea() throws ParseException {
        String pol = "POLYGON ((0 0, " +
                "0 1,1 1,1 0,0 0))";
        Polygon p = geoCreater.createPolygonByWKT(pol);
        System.out.println(p.getArea());
    }

}
