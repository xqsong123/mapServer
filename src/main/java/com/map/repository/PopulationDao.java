package com.map.repository;

import com.alibaba.fastjson.JSONObject;
import com.map.utils.Constants;
import com.map.utils.MongoDBConnection;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.Document;
import org.geotools.geojson.GeoJSONUtil;
import org.geotools.geojson.geom.GeometryJSON;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.prefs.BackingStoreException;

public class PopulationDao {
    private Log log = LogFactory.getLog(PopulationDao.class);


    /* 点击饼图显示的点集合
     * @param minHzb 最小横坐标
     * @param maxHzb 最大横坐标
     * @param minZzb 最小纵坐标
     * @param maxZzb 最大纵坐标
     * */
    public List<Document> queryPopulation(double minHzb, double maxHzb, double minZzb, double maxZzb) {
        MongoCollection<Document> collection = MongoDBConnection.getConnect2().getCollection("syrk_300");
        BasicDBObject query = new BasicDBObject();//条件
        query.put("ZXDHZB", new BasicDBObject("$gte", minHzb).append("$lte", maxHzb));
        query.put("ZXDZZB", new BasicDBObject("$gte", minZzb).append("$lte", maxZzb));

        BasicDBObject key = new BasicDBObject();//指定需要显示列
        key.put("_id", 0);
        key.put("RKBM", 1);
        key.put("SYRKGLLBDM", 1);
        key.put("GMSFHM", 1);
        key.put("ZDRYBZ", 1);
        key.put("DJSJ", 1);
        key.put("ZXDHZB", 1);
        key.put("ZXDZZB", 1);
        FindIterable<Document> iterable = collection.find(query).projection(key);
        MongoCursor<Document> iterator = iterable.iterator();
        List<Document> list = new LinkedList<>();
        while (iterator.hasNext()){
            list.add(iterator.next());
        }
        return list;
    }

    /*根据开始时间结束时间查询一定范围内的总人口、流口、常口、重点人口、境外
     * @param minHzb 最小横坐标
     * @param maxHzb 最大横坐标
     * @param minZzb 最小纵坐标
     * @param maxZzb 最大纵坐标
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @param gltype 11：常口   12：流动  20：境外
     * @param zdtype 查询重点人口的标识，值为Y时是重点人口
     * @param zdrkfl 重点人口分类
     * */
    public List<Document> queryPopsByTime(double minHzb, double maxHzb, double minZzb, double maxZzb, Date beginTime, Date endTime, String gltype, String zdtype, String zdrkfl, double level) {
        if(StringUtils.isNotEmpty(zdtype) || StringUtils.isNotEmpty(zdrkfl)){
            MongoCollection<Document> collection = MongoDBConnection.getConnect2().getCollection("zdry");
//            Envelope envelope = new Envelope(minHzb, maxHzb, minZzb, maxZzb);
//            Polygon polygon = (Polygon)new GeometryFactory().toGeometry(envelope);
//            String boundary = new GeometryJSON().toString(polygon);
//            JSONObject jsonObject = JSONObject.parseObject(boundary);
//
//            BasicDBObject query = new BasicDBObject("location", new BasicDBObject("$geoWithin", new BasicDBObject("$geometry",
//                    jsonObject)));

            BasicDBObject query = new BasicDBObject("x", new BasicDBObject("$gt", minHzb).append("$lt",maxHzb))
                    .append("y", new BasicDBObject("$gt", minZzb).append("$lt", maxZzb))
                    .append("zdrylb", new BasicDBObject("$in", Constants.ZDRYLB));
            if (StringUtils.isNotEmpty(zdrkfl)){
                query.append("zdrylb", zdrkfl);
            }
            BasicDBObject project =  new BasicDBObject("_id", 0).append("rkbm",1)
                    .append("x",1).append("y",1);
            Iterator<Document> iterator = collection.find(query).sort(new BasicDBObject("random", 1)).limit(3000).projection(project).iterator();
            List<Document> list = new LinkedList<>();
            while (iterator.hasNext()){
                Document document = iterator.next();
                document.put("ZXDHZB", document.getDouble("x"));
                document.put("ZXDZZB", document.getDouble("y"));
                list.add(document);
            }
            return list;
        }
        MongoCollection<Document> collection;
        if(level < 14) {
            collection = MongoDBConnection.getConnect2().getCollection("syrk_random_200");
        } else {
            collection = MongoDBConnection.getConnect2().getCollection("syrk");
        }
        BasicDBObject query = new BasicDBObject();//条件
        query.put("ZXDHZB", new BasicDBObject("$gte", minHzb).append("$lte", maxHzb));
        query.put("ZXDZZB", new BasicDBObject("$gte", minZzb).append("$lte", maxZzb));
        if (beginTime != null || endTime != null) {
            query.put("DJSJ", new BasicDBObject("$gte", beginTime).append("$lte", endTime));
        }

        if (gltype != null) {//11：常口   12：流动  20：境外
            query.put("SYRKGLLBDM", Integer.parseInt(gltype));
        }
        if (zdtype != null && zdtype != "") {
            query.put("ZDRYBZ", "Y");//查询重点人口
        }
        if (zdrkfl != null && zdtype != "") {//重点人口分的细类
            query.put("ZDRYLB", zdrkfl);
        }
        BasicDBObject key = new BasicDBObject();//指定需要显示列
        key.put("_id", 0);
        key.put("RKBM", 1);
        //key.put("SYRKGLLBDM", 1);
        // key.put("GMSFHM", 1);
        //key.put("ZDRYBZ", 1);
        //key.put("DJSJ", 1);
        key.put("ZXDHZB", 1);
        key.put("ZXDZZB", 1);
        Iterator<Document> iterator;
        if(level < 14) {
            iterator = collection.find(query).limit(5000).projection(key).iterator();
        } else {
            iterator = collection.find(query).limit(5000).sort(new BasicDBObject("random", 1)).projection(key).iterator();
        }
        List<Document> list = new LinkedList<>();
        while (iterator.hasNext()){
            list.add(iterator.next());
        }
        return list;
    }

    /* 根据建筑物地址编码查询出改幢建筑物中流动常住重点人口人数等详情
     * @param dzbm  建筑物地址编码
     * */
    public Object getJzwDetail(String dzbm) {
        MongoCollection<Document> collection = MongoDBConnection.getConnect2().getCollection("detail");
        BasicDBObject query = new BasicDBObject();//条件
        query.put("jzwdzbm", dzbm);
        BasicDBObject key = new BasicDBObject();//指定需要显示列
        key.put("_id", 0);
        key.put("jzwdzbm", 1);
        key.put("jzwdzmc", 1);
        key.put("roomInfoList", 1);
        key.put("companyInfoList", 1);
        key.put("houseInfoList", 1);
        key.put("x", 1);
        key.put("y", 1);
        MongoCursor<Document> iterator = collection.find(query).projection(key).iterator();
        List<Document> list = new LinkedList<>();
        while (iterator.hasNext()){
            list.add(iterator.next());
        }
        if (CollectionUtils.isEmpty(list)){
            return null;
        }
        return list.get(0);
    }

    /* 根据派出所编号查询出该派出所辖区的密度值
     * @param xqbms  与shp文件中派出所的编号对应
     * @param type  第一大类区分标识   1:人口
     * @param secType 第二大类区分标识   1:总人口密度  02：流口密度  03：重点人口密度
     * */
    public List getAreaDensity(List xqbms, String type, String secType) {
        MongoCollection<Document> collection = MongoDBConnection.getConnect2().getCollection("density");
        BasicDBObject query = new BasicDBObject();//条件
        if (xqbms != null && xqbms.size() != 0) {
            query.put("xqbm", new BasicDBObject("$in", xqbms));
        }
        query.put("type", type);
        query.put("secType", secType);

        BasicDBObject key = new BasicDBObject();//指定需要显示列
        key.put("_id", 0);
        key.put("xqbm", 1);
        key.put("type", 1);
        key.put("secType", 1);
        key.put("density", 1);
        MongoCursor<Document> iterator = collection.find(query).projection(key).iterator();
        //把结果集输出成list类型
        List<Document> list = new LinkedList<>();
        while (iterator.hasNext()){
            list.add(iterator.next());
        }
        //System.out.println("list------:" + list);
        return list;
    }

    /* 根据人口编码查询人口信息
     * @param rkbm  人口编码
     * */
    public List getPersonDetail(String rkbm) {
        MongoCollection<Document> collection = MongoDBConnection.getConnect2().getCollection("syrk");//实有人口集合
        FindIterable<Document> findIterable = collection.find(new Document("RKBM", rkbm));
        MongoCursor cursor = findIterable.iterator();
        List list = new ArrayList<>();
        while (cursor.hasNext()) {
            list.add(cursor.next());
        }
        return list;
    }

    /* 查询实有人口表中人口数
     * */
    /*public int getPopCount() {
        MongoCollection<Document> collection = MongoDBConnection.getConnect1().getCollection("syrk");//实有人口集合
        DistinctIterable<String> records = collection.distinct("GMSFHM", String.class);
        int count = 0;
        for (String s : records) {
            count = count + 1;
            System.out.println(s);
        }
        return count;
    }
*/
}
