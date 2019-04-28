package com.map.repository;

import com.map.utils.MongoDBConnection;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.Document;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class FwDao {
    private Log log = LogFactory.getLog(FwDao.class);

    /* 根据一定范围查询所有的房屋
     * type 1:自用   2：出租   3：闲置
     * @param minHzb最小横坐标
     * @param maxHzb最大横坐标
     * @param minZzb最小纵坐标
     * @param maxZzb最大纵坐标
     * */
    public List queryBulidings(String type, double minHzb, double maxHzb, double minZzb, double maxZzb) {
        MongoCollection<Document> collection = MongoDBConnection.getConnect2().getCollection("syfw_200");
        BasicDBObject query = new BasicDBObject();//条件
        query.put("ZXDHZB", new BasicDBObject("$gte", minHzb).append("$lte", maxHzb));
        query.put("ZXDZZB", new BasicDBObject("$gte", minZzb).append("$lte", maxZzb));
        if (type != null && type != "") {
            query.put("SYXS", type);
        }

        BasicDBObject key = new BasicDBObject();
        key.put("_id", 0);
        key.put("DZBM", 1);
        key.put("DZMC", 1);
        key.put("SYXS", 1);
        key.put("ZXDHZB", 1);
        key.put("ZXDZZB", 1);
        FindIterable<Document> iterable = collection.find(query).sort(new BasicDBObject("random", 1)).limit(3000).projection(key);
        MongoCursor<Document> iterator = iterable.iterator();
        List<Document> list = new LinkedList<>();
        while (iterator.hasNext()){
            list.add(iterator.next());
        }
        return list;
    }

    /* 根据房屋地址编码查询实有房屋的详情信息
     * @param dzbm 地址编码
     * */
    public List queryBulidingsByDz(String dzbm) {
        MongoCollection<Document> collection = MongoDBConnection.getConnect2().getCollection("syfw_200");
        List list = new ArrayList<>();
        FindIterable<Document> findIterable = collection.find(new Document("DZBM", dzbm));
               /* .projection(new BasicDBObject().append("DZBM", 1).append("ZXDHZB", 1).append("ZXDZZB", 1)
                        .append("DZMC", 1).append("JZWFWSX", 1).append("SSSJ", 1).append("SSFJ", 1));*/
        MongoCursor cursor = findIterable.iterator();
        while (cursor.hasNext()) {
            list.add(cursor.next());
        }
        return list;
    }

    /* 查询实有建筑物中房屋数量
     * */
    public int getFwCount() {
        MongoCollection<Document> collection = MongoDBConnection.getConnect2().getCollection("syfw_200");
        Long count = collection.count();
        int dwCount = count.intValue();
        return dwCount;
    }


}
