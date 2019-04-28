package com.map.utils;

import com.map.serviceImp.DataRestServiceImpl;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.jdbc.JDBCDataStore;
import org.junit.Test;
import org.locationtech.jts.algorithm.distance.DistanceToPoint;
import org.locationtech.jts.algorithm.distance.PointPairDistance;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.io.ParseException;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.Filter;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;

public class DataTest {

    @Test
    public void test() {
        String path = "E:\\\\mapData\\\\mapshaperData-\\\\shp1";
        DataRestServiceImpl.resetRoadData(path);
        int i = 0;
    }

    @Test
    public void test7() {
        System.out.println();
    }

    //测试两线相交并添加起始点功能
    @Test
    public void test1() throws ParseException {
        GeoCreatorByLtc geoCreatorByLtc = GeoCreatorByLtc.getInstance();
        String h = "LINESTRING(1 1, 5 2)";
        String v = "LINESTRING(3 2, 2 2)";
        LineString hl = geoCreatorByLtc.createLineByWKT(h);
        LineString vl = geoCreatorByLtc.createLineByWKT(v);
        Point start = vl.getStartPoint();
        Coordinate startCoor = start.getCoordinate();
        PointPairDistance pointPairDistance = new PointPairDistance();
        DistanceToPoint.computeDistance(hl, startCoor, pointPairDistance);
        double d = pointPairDistance.getDistance();
        Coordinate[] cs = pointPairDistance.getCoordinates();

        double x = cs[0].getOrdinate(0);
        double y = cs[0].getOrdinate(1);
        Point p = geoCreatorByLtc.createPoint(x, y, null);

        Coordinate[] vlcoors = vl.getCoordinates();
        Coordinate[] newCoors = new Coordinate[vlcoors.length + 1];
        newCoors[0] = cs[0];

        for (int i = 0; i < vlcoors.length; i++) {
            newCoors[i + 1] = vlcoors[i];
        }

        GeometryFactory geometryFactory = new GeometryFactory();
        LineString newvl = geometryFactory.createLineString(newCoors);

        System.out.println(hl.intersects(newvl));

        System.out.println(newvl.toString());
    }

    @Test
    public void test3() {
        Map<String, String> m = new HashMap<>();
        m.put("1", "a");
        m.put("2", "b");
        m.put("3", "c");

        Iterator it = m.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entity = (Map.Entry) it.next();
            String key = (String) entity.getKey();
            String val = (String) entity.getValue();
            Iterator innerIt = m.entrySet().iterator();
            while (innerIt.hasNext()) {
                Map.Entry innerEntity = (Map.Entry) innerIt.next();
                String innerKey = (String) innerEntity.getKey();
                String innerVal = (String) innerEntity.getValue();
                System.out.println(key + "--" + innerKey);
                System.out.println(val + "--" + innerVal);
            }
        }

        System.out.println(4.744684910340124E-8);


    }

    @Test
    public void test4() throws ParseException {
        String p = "POINT (117.11279936759263 36.694965870892766)";

        String l = "LINESTRING (117.10960410374742 36.69419473460022, 117.11001039586415 36.694267305392714, 117.11070137017316 36.69441273206286, 117.11373892661129 36.69521358643874, 117.11394012293943 36.69527129053853, 117.11490095411682 36.69554679784733, 117.11534757812933 36.69568235176001, 117.11577435410425 36.69575892453565, 117.11634772496859 36.6957847530648, 117.1173705850855 36.695783339330546, 117.11737122180546 36.69578333843123)";
        GeoCreatorByLtc geoCreatorByLtc = GeoCreatorByLtc.getInstance();
        Point pp = geoCreatorByLtc.createPointByWKT(p, null);
        LineString ls = geoCreatorByLtc.createLineByWKT(l);
        PointPairDistance pointPairDistance = new PointPairDistance();
        DistanceToPoint.computeDistance(ls, pp.getCoordinate(), pointPairDistance);

        String al = "LINESTRING (117.11279936759263 36.694965870892766, 117.11279936759263 36.65)";
        LineString als = geoCreatorByLtc.createLineByWKT(al);

        System.out.println(als.intersects(ls));

        /*System.out.println(ls.touches(pp) || ls.contains(pp) || ls.intersects(pp));
        System.out.println(pointPairDistance.getDistance());*/

    }

    @Test
    public void test5() throws ParseException {
        String l = "LINESTRING(0 1, 2 1, 4 1, 8 1)";
        GeoCreatorByLtc geoCreatorByLtc = GeoCreatorByLtc.getInstance();
        LineString ls = geoCreatorByLtc.createLineByWKT(l);
        List<Coordinate> coorLst = GeoToolsUtils.divideDistance(ls);

        System.out.println(coorLst.toString());

    }

    @Test
    public void test6() {
        String host = "localhost";
        String schema = "public";
        String database = "postgis_25_sample";
        String user = "postgres";
        String pass = "postgres";
        String tablename = "citybound";
        int port = 5432;
        //读取
        SimpleFeatureCollection colls1 = readPostgisTable(host, port, user, pass, database, schema, tablename);
        if (colls1 == null) {
            System.out.println("请检查参数，确保jdbc连接正常以及表存在.");
            return;
        }
        //拿到所有features
        SimpleFeatureIterator iters = colls1.features();
        //遍历打印
        while (iters.hasNext()) {
            SimpleFeature sf = iters.next();
            System.out.println(sf.getID() + " , " + sf.getAttributes());
        }
    }

    public SimpleFeatureCollection readPostgisTable(String host, int port, String user, String pass, String dbname, String schema, String tablename) {
        return readPostgisTable(host, port, user, pass, dbname, schema, tablename, null);
    }

    public SimpleFeatureCollection readPostgisTable(String host, int port, String user, String pass, String dbname, String schema, String tablename, Filter filter) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("dbtype", "postgis");
        params.put("host", host);
        params.put("port", port);
        params.put("schema", schema);
        params.put("database", dbname);
        params.put("user", user);
        params.put("passwd", pass);
        try {
            JDBCDataStore dataStore = (JDBCDataStore) DataStoreFinder.getDataStore(params);
            return readDatastore(dataStore, tablename, filter);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public SimpleFeatureCollection readDatastore(JDBCDataStore store, String typeName, Filter filter) {
        try {
            SimpleFeatureSource featureSource = store.getFeatureSource(typeName);
            return filter != null ? featureSource.getFeatures(filter) : featureSource.getFeatures();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Test
    public void test10() {
        System.out.println(Double.valueOf("18.0".toString()).longValue());
        String s = (String) null;

    }

    @Test
    public void test11() throws Exception {
        String path = "E:\\tuyun workspace\\PCS\\FQ_JYGLFQ_PCS_PG1.shp";
        GeoToolsUtils geoTools = new GeoToolsUtils();
        List<String> attr = geoTools.getShpProps(path);
        List<Geometry> geometries = geoTools.readSHP(path, "GBK", attr, null);
        //List<String> areaLst = Arrays.asList(new String[]{"", "MC", "ZZJGDM", "SZDDZ", "SSJGMC", "SSJGDM", "LX", "GXSJ", "SHAPE_AREA", "SHAPE_LEN", "ID"});
        MongoCollection<Document> collection = MongoDBConnection.getajjqConnect().getCollection("caseDensity");
        for (int i = 0; i < geometries.size(); i++) {
            Geometry geometry = geometries.get(i);
            Map<String, String> m = (Map) geometry.getUserData();
            String id = m.get("ID");
            int ID = Integer.parseInt(id.substring(1));
           /* new GeometryFactory().toGeometry(new Envelope()).intersects(g) ;
            new GeometryFactory().toGeometry(new Envelope()).contains(g);*/

            if (ID > 506) {
                String zzjgdm = m.get("ZZJGDM");
                // Map map = queryAjxx(zzjgdm);
                //int count = Integer.parseInt(map.get("count").toString());
                double area = geometry.getArea();
                Document document = new Document();
                //CaseDensity caseDensity = new CaseDensity();
                document.put("pcsId", id);
                if (zzjgdm == null) {
                    document.put("cbdw_bh", null);
                } else {
                    document.put("cbdw_bh", zzjgdm);
                }
                Map map = hah(zzjgdm);
                Set<Map.Entry<Object, Object>> entryseSet = map.entrySet();
                Map<Object, Object> densityMap = new HashMap<>();
                for (Map.Entry<Object, Object> entry : entryseSet) {
                    int count = 0;
                    double density = 0;
                    if (null != entry.getValue()) {
                        count = Integer.parseInt(entry.getValue().toString());
                    }
                    if (area != 0d) {
                        density = count / area / 110 / 110;
                    } else {
                        density = 0;
                    }
                    densityMap.put(entry.getKey(), density);
                    //System.out.println("key："++"==value:"+entry.getValue());
                }
                document.put("density", densityMap);
                document.put("updateTime", new Date());

                collection.insertOne(document);
            }
            //aa = aa + "id==:" + id + "----" + "zzjgdm==:" + zzjgdm + "----" + "count==:" + count + "---area==:" + area + "---density==:" + density + "\n";
            //System.out.println("id==:" + id + "----" + "zzjgdm==:" + zzjgdm + "----" + "count==:" + count + "---area==:" + area + "---density==:" + density);
        }
        //FileTools.createJsonFile("1", aa, "E:\\tuyun workspace", "density");

    }

    public Map<String, Object> queryAjxx(String zzjgdm) {
        MongoCollection<Document> collection = MongoDBConnection.getajjqConnect().getCollection("ajxx");
        FindIterable<Document> findIterable = collection.find(new Document("cbdw_bh", zzjgdm));


        MongoCursor<Document> cursor = findIterable.iterator();
        Map map = new HashMap();
        int i = 0;
        while (cursor.hasNext()) {
            Document document = cursor.next();
            i++;
        }
        map.put("count", i);
        return map;
    }


    public Map hah(String cbdw_bh) throws java.text.ParseException {
        MongoCollection<Document> collection = MongoDBConnection.getajjqConnect().getCollection("ajxx");
        List<BasicDBObject> pipeline = new LinkedList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM");
        BasicDBObject[] condition = new BasicDBObject[]{
                new BasicDBObject("cbdw_bh", cbdw_bh),
                new BasicDBObject("fasj", new BasicDBObject("$gte", sdf.parse("2016-01-01")))
        };
        BasicDBObject match = new BasicDBObject("$match", new BasicDBObject("$and", condition));

        BasicDBObject fieldRemain = new BasicDBObject();
        fieldRemain.put("fasj", new BasicDBObject("$dateToString", new BasicDBObject("format", "%Y-%m").append("date", "$fasj")));
        BasicDBObject project = new BasicDBObject("$project", fieldRemain);

        BasicDBObject sort = new BasicDBObject("$sort", new BasicDBObject("fasj", -1));

        BasicDBObject groupCondition = new BasicDBObject();
        groupCondition.put("_id", new BasicDBObject("fasj", "$fasj"));
        //groupCondition.put("_id", new BasicDBObject("fasj", "$fasj").append("cbdw_bh", "$cbdw_bh"));
        groupCondition.put("count", new BasicDBObject("$sum", 1));
        BasicDBObject group = new BasicDBObject("$group", groupCondition);
        pipeline.add(match);
        pipeline.add(project);
        pipeline.add(sort);
        pipeline.add(group);
        Map resLst = new HashMap();
        MongoCursor<Document> documentMongoCursor = collection.aggregate(pipeline).allowDiskUse(true).iterator();
        while (documentMongoCursor.hasNext()) {
            Document document = documentMongoCursor.next();
            Map map = (Map) document.get("_id");
            String fasj = null;
            if (null != map.get("fasj")) {
                fasj = map.get("fasj").toString();
            }
            int count = Integer.parseInt(document.get("count").toString());

            resLst.put(fasj, count);
        }
        return resLst;
    }

    /**
     * @param bean
     * @return DBObject 返回类型
     * @Description:bean-->DBObject
     */
    public static <T> DBObject getDBObject(T bean) {
        if (bean == null) {
            return null;
        }
        DBObject obj = new BasicDBObject();
        Field[] field = bean.getClass().getDeclaredFields();
        for (Field f : field) {
            String name = f.getName();
            if (!f.isAccessible()) {
                f.setAccessible(true);
            }
            try {
                Object oj = f.get(bean);
                if (oj == null) {
                    obj.put(name, "");
                } else if (oj instanceof Integer) {
                    int value = ((Integer) oj).intValue();
                    obj.put(name, value);
                } else if (oj instanceof Double) {
                    Double value = ((Double) oj).doubleValue();
                    obj.put(name, value);
                } else if (oj instanceof Float) {
                    Float value = ((Float) oj).floatValue();
                    obj.put(name, value);
                } else if (oj instanceof Boolean) {
                    Boolean value = ((Boolean) oj).booleanValue();
                    obj.put(name, value);
                } else if (oj instanceof Long) {
                    Long value = ((Long) oj).longValue();
                    obj.put(name, value);
                } else {
                    obj.put(name, oj);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return obj;

    }

}
