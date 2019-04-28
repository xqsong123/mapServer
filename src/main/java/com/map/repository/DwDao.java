package com.map.repository;

import com.map.utils.MongoDBConnection;
import com.mongodb.*;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.Document;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DwDao {
    private Log log = LogFactory.getLog(DwDao.class);


    /* 根据一定范围查询所有的单位
     * firtype  2:普通单位   3:特种单位  4：保护单位    5：九小场所
     * sectype  饼图中的单位分类
     * @param minHzb 最小横坐标
     * @param maxHzb 最大横坐标
     * @param minZzb 最小纵坐标
     * @param maxZzb 最大纵坐标
     * */
    public List queryDw(String firtype, String sectype, double minHzb, double maxHzb, double minZzb, double maxZzb) {
        MongoCollection<Document> collection = MongoDBConnection.getConnect2().getCollection("sydw_random");
        BasicDBObject query = new BasicDBObject();//条件
        query.put("ZXDHZB", new BasicDBObject("$gte", minHzb).append("$lte", maxHzb));
        query.put("ZXDZZB", new BasicDBObject("$gte", minZzb).append("$lte", maxZzb));
        if (firtype != null && firtype != "") {
            if (firtype.equals("2")) {//普通单位
//                BasicDBList values = new BasicDBList();
//                values.add("N");
//                BasicDBObject in = new BasicDBObject("$in", values);
                query.put("ZDDWBS", "0");
            } else if (firtype.equals("3")) {//特种单位
                BasicDBList values = new BasicDBList();
                values.add("240");
                values.add("216");
                values.add("219");
                values.add("217");
                values.add("220");
                values.add("211");
                values.add("212");
                values.add("213");
                values.add("215");
                values.add("214");
                values.add("218");
                values.add("221");
                values.add("280");
                values.add("291");
                values.add("292");
                values.add("230");
                BasicDBObject in = new BasicDBObject("$in", values);
                query.put("GAGL_DWFL", in);
            } else if (firtype.equals("4")) {//保护单位
                BasicDBList values = new BasicDBList();
                values.add("251");
                values.add("259");
                values.add("269");
                values.add("268");
                values.add("252");
                values.add("271");
                values.add("253");
                values.add("254");
                values.add("255");
                values.add("256");
                values.add("257");
                BasicDBObject in = new BasicDBObject("$in", values);
                query.put("GAGL_DWFL", in);
            } else if (firtype.equals("5")) {//九小场所
                query.put("SFJXCS", "Y");
            }
        }

        if (sectype != null && sectype != "") {//饼图中细小的分类
            query.put("GAGL_DWFL", sectype);
        }

        BasicDBObject key = new BasicDBObject();
        key.put("_id", 0);
        key.put("ZAGLDWBM", 1);
        key.put("GAGL_DWFL", 1);
//        key.put("DWMC", 1);
        key.put("ZDDWBS", 1);
        key.put("SFJXCS", 1);
        key.put("ZXDHZB", 1);
        key.put("ZXDZZB", 1);
        FindIterable<Document> iterable = collection.find(query).limit(5000).projection(key);
        MongoCursor<Document> iterator = iterable.iterator();
        List<Document> documentList = new LinkedList<>();
        while (iterator.hasNext()){
            documentList.add(iterator.next());
        }
        return documentList;
      /*  List dwList = new ArrayList<>();
        FindIterable<Document> findIterable = collection.find(new Document("ZXDHZB", new Document("$gte", minHzb).append("$lte", maxHzb))
                .append("ZXDZZB", new Document("$gte", minZzb).append("$lte", maxZzb)))
                .projection(new BasicDBObject().append("ZAGLDWBM", 1).append("GAGL_DWFL", 1)
                        .append("DWMC", 1).append("DWDZ_DZBM", 1).append("ZDDWBS", 1).append("SFJXCS", 1)
                        .append("ZXDHZB", 1).append("ZXDZZB", 1))
                .sort(new Document("random", 1)).limit(3000);
        MongoCursor cursor = findIterable.iterator();
        int count = 0;
        while (cursor.hasNext()) {
            dwList.add(cursor.next());
            count = count + 1;
        }
        System.out.println("count-----------:" + count);
        return dwList;*/
    }

    /* 根据单位地址编码查询单位详情
     * @param dzbm  地址编码
     * */
    public List getdwDetails(String zagldwbm) {
        MongoCollection<Document> collection = MongoDBConnection.getConnect2().getCollection("sydw");//实有单位集合
        List list = new ArrayList<>();
        FindIterable<Document> findIterable = collection.find(new Document("ZAGLDWBM", zagldwbm))/*.projection(new BasicDBObject().append("ZAGLDWBM", 1)
                .append("GAGL_DWFL", 1).append("DWMC", 1).append("DWDZ_DZBM", 1).append("ZDDWBS", 1).append("SFJXCS", 1)
                .append("ZXDHZB", 1).append("ZXDZZB", 1).append("JYFWZY", 1).append("JYFWJY", 1).append("JYMJ_MJPFM", 1)
                .append("JYFS", 1).append("ZCZB", 1).append("LXDH", 1).append("WZ", 1).append("FDDBR_XM", 1))*/;
        MongoCursor cursor = findIterable.iterator();
        while (cursor.hasNext()) {
            list.add(cursor.next());
        }
        return list;
    }

    /* 查询实有单位表中单位数
     * */
    public int getDwCount() {
        MongoCollection<Document> collection = MongoDBConnection.getConnect2().getCollection("sydw");//实有单位集合
        Long count = collection.count();
        int popCount = count.intValue();
        return popCount;
    }

}
