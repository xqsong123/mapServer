package com.map.repository;

import com.map.utils.MongoDBConnection;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.BasicBSONCallback;
import org.bson.Document;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.BaseStream;

public class CaseDao {
    private Log log = LogFactory.getLog(CaseDao.class);

    /* 根据一定范围查询一定时间范围内全部的案件
     * @param minHzb最小横坐标
     * @param maxHzb最大横坐标
     * @param minZzb最小纵坐标
     * @param maxZzb最大纵坐标
     * @param beginTime开始时间
     * @param endTime结束时间
     * */
    public List<Map<String, Object>> queryAllCases(double minHzb, double maxHzb, double minZzb, double maxZzb, Date beginTime, Date endTime) {
        long start = System.currentTimeMillis();
        MongoCollection<Document> collection = MongoDBConnection.getajjqConnect().getCollection("ajxx");
        BasicDBObject query = new BasicDBObject();//条件
        query.put("zbx", new BasicDBObject("$gte", minHzb).append("$lte", maxHzb));
        query.put("zby", new BasicDBObject("$gte", minZzb).append("$lte", maxZzb));
        if (beginTime != null && endTime != null) {
            query.put("fasj", new BasicDBObject("$gte", beginTime).append("$lte", endTime));
        }
        BasicDBObject key = new BasicDBObject();
        key.put("_id", 0);
        key.put("ajbh", 1);
        key.put("zbx", 1);
        key.put("zby", 1);

        FindIterable<Document> iterable = collection.find(query).sort(new BasicDBObject("random", 1)).limit(3000).projection(key);
        MongoCursor<Document> iterator = iterable.iterator();
        //List<double[]> caseList = new LinkedList<>();
        List<Map<String, Object>> caseList = new LinkedList<>();
        while (iterator.hasNext()) {
            Document document = iterator.next();
            double zbx = 0d;
            double zby = 0d;
            if (null != document.get("zbx")) {
                zbx = Double.parseDouble(document.get("zbx").toString());
            }
            if (null != document.get("zby")) {
                zby = Double.parseDouble(document.get("zby").toString());
            }
            //double[] xy = new double[]{zbx, zby};
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("ajbh", document.get("ajbh").toString());
            map.put("x", zbx);
            map.put("y", zby);
            caseList.add(map);
        }
        long end = System.currentTimeMillis();
        System.out.println("caseDao queryAllCases need time: " + (end - start) / 1000d + "s" + "--List size:" + caseList.size());
        return caseList;
    }


    /* 根据一定范围查询一定时间范围内一级单个类型的案件
     * @param minHzb最小横坐标
     * @param maxHzb最大横坐标
     * @param minZzb最小纵坐标
     * @param maxZzb最大纵坐标
     * @param beginTime开始时间
     * @param endTime结束时间
     * @param firType 一级分类编码
     * */
    public List<Map<String, Object>> queryFirTypeCases(double minHzb, double maxHzb, double minZzb, double maxZzb, Date beginTime, Date endTime, String firType) {
        MongoCollection<Document> collection = MongoDBConnection.getajjqConnect().getCollection("ajxx");
        BasicDBObject query = new BasicDBObject();//条件
        query.put("zbx", new BasicDBObject("$gte", minHzb).append("$lte", maxHzb));
        query.put("zby", new BasicDBObject("$gte", minZzb).append("$lte", maxZzb));
        if (beginTime != null && endTime != null) {
            query.put("fasj", new BasicDBObject("$gte", beginTime).append("$lte", endTime));
        }
        List oneLst = new ArrayList();
        oneLst.add("101");//治安管理
        oneLst.add("212");//刑事侵财
        oneLst.add("105");//交通
        oneLst.add("209");//公共安全
        List twoLst = new ArrayList();
        twoLst.add("101030068");//殴打他人
        twoLst.add("101030078");//盗窃
        twoLst.add("101030083");//故意损毁财物
        twoLst.add("101030079");//诈骗

        List qitaLst = getChildByOneLevel("qita");
        BasicDBList values = new BasicDBList();
        for (int i = 0; i < qitaLst.size(); i++) {
            Map map = (Map) qitaLst.get(i);
            values.add(map.get("ajbh_2"));
        }
        if (StringUtils.isNotEmpty(firType)) {
            if (oneLst.contains(firType)) {
                query.put("aybh_1", firType);
            }
            if (twoLst.contains(firType)) {
                query.put("aybh", firType);
            }
            if ("qita".equals(firType)) {//其它
              /*  BasicDBList values = new BasicDBList();
                values.add("102");
                values.add("103");
                values.add("104");
                values.add("106");
                values.add("107");
                values.add("208");
                values.add("210");
                values.add("211");
                values.add("213");
                values.add("214");
                values.add("215");
                values.add("216");
                values.add("217");*/
                query.put("aybh_1", new BasicDBObject("$in", values));
            }
        }

        BasicDBObject key = new BasicDBObject();
        key.put("_id", 0);
        key.put("ajbh", 1);
        key.put("zbx", 1);
        key.put("zby", 1);

        FindIterable<Document> iterable = collection.find(query).sort(new BasicDBObject("random", 1)).limit(3000).projection(key);
        MongoCursor<Document> iterator = iterable.iterator();
        List<Map<String, Object>> caseList = new LinkedList<>();
        while (iterator.hasNext()) {
            Document document = iterator.next();
            double zbx = 0d;
            double zby = 0d;
            if (null != document.get("zbx")) {
                zbx = Double.parseDouble(document.get("zbx").toString());
            }
            if (null != document.get("zby")) {
                zby = Double.parseDouble(document.get("zby").toString());
            }

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("ajbh", document.get("ajbh").toString());
            map.put("x", zbx);
            map.put("y", zby);
            caseList.add(map);
        }
        return caseList;
    }

    /* 根据一定范围查询一定时间范围内二级单个类型的案件
     * @param minHzb最小横坐标
     * @param maxHzb最大横坐标
     * @param minZzb最小纵坐标
     * @param maxZzb最大纵坐标
     * @param beginTime开始时间
     * @param endTime结束时间
     * @param firType 一级分类编码
     * @param secType 二级分类编码
     * */
    public List<Map<String, Object>> querySecTypeCases(double minHzb, double maxHzb, double minZzb, double maxZzb, Date beginTime, Date endTime, String firType, String secType) {
        MongoCollection<Document> collection = MongoDBConnection.getajjqConnect().getCollection("ajxx");
        BasicDBObject query = new BasicDBObject();//条件
        query.put("zbx", new BasicDBObject("$gte", minHzb).append("$lte", maxHzb));
        query.put("zby", new BasicDBObject("$gte", minZzb).append("$lte", maxZzb));
        if (beginTime != null && endTime != null) {
            query.put("fasj", new BasicDBObject("$gte", beginTime).append("$lte", endTime));
        }

        if (StringUtils.isNotEmpty(firType) && StringUtils.isNotEmpty(secType)) {
            if ("101".equals(firType)) {//治安管理
                query.put("aybh_2", secType);
            } else if ("212".equals(firType)) {//刑事侵财
                query.put("aybh", secType);
            } else if ("105".equals(firType)) {//交通
                query.put("aybh", secType);
            } else if ("209".equals(firType)) {//公共安全
                query.put("aybh", secType);
            } else if ("qita".equals(firType)) {//其它
                query.put("aybh_1", secType);
            }
        }

        BasicDBObject key = new BasicDBObject();
        key.put("_id", 0);
        key.put("ajbh", 1);
        key.put("zbx", 1);
        key.put("zby", 1);

        FindIterable<Document> iterable = collection.find(query).sort(new BasicDBObject("random", 1)).limit(3000).projection(key);
        MongoCursor<Document> iterator = iterable.iterator();
        List<Map<String, Object>> caseList = new LinkedList<>();
        while (iterator.hasNext()) {
            Document document = iterator.next();
            double zbx = 0d;
            double zby = 0d;
            if (null != document.get("zbx")) {
                zbx = Double.parseDouble(document.get("zbx").toString());
            }
            if (null != document.get("zby")) {
                zby = Double.parseDouble(document.get("zby").toString());
            }

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("ajbh", document.get("ajbh").toString());
            map.put("x", zbx);
            map.put("y", zby);
            caseList.add(map);
        }
        return caseList;
    }

    /*查询一定范围一段时间内饼图的数据
     * @param minHzb最小横坐标
     * @param maxHzb最大横坐标
     * @param minZzb最小纵坐标
     * @param maxZzb最大纵坐标
     * @param beginTime开始时间
     * @param endTime结束时间
     * */
    public List<Map<String, Object>> queryPieChartData(double minHzb, double maxHzb, double minZzb, double maxZzb, Date beginTime, Date endTime) {
        long start = System.currentTimeMillis();
        MongoCollection<Document> collection = MongoDBConnection.getajjqConnect().getCollection("ajxx");
        BasicDBObject query = new BasicDBObject();//条件
        List<BasicDBObject> optionList = new ArrayList<>();
        BasicDBObject gtexStart = new BasicDBObject("zbx", new BasicDBObject("$gte", minHzb));
        BasicDBObject matchXStart = new BasicDBObject("$match", gtexStart);
        optionList.add(matchXStart);
        BasicDBObject gteXEnd = new BasicDBObject("zbx", new BasicDBObject("$lte", maxHzb));
        BasicDBObject matchXEnd = new BasicDBObject("$match", gteXEnd);
        optionList.add(matchXEnd);

        BasicDBObject gteyStart = new BasicDBObject("zby", new BasicDBObject("$gte", minZzb));
        BasicDBObject matchYStart = new BasicDBObject("$match", gteyStart);
        optionList.add(matchYStart);
        BasicDBObject gteYEnd = new BasicDBObject("zby", new BasicDBObject("$lte", maxZzb));
        BasicDBObject matchYEnd = new BasicDBObject("$match", gteYEnd);
        optionList.add(matchYEnd);

        if (beginTime != null && endTime != null) {
            BasicDBObject queryFasjStart = new BasicDBObject("fasj", new BasicDBObject("$gte", beginTime));
            BasicDBObject matchFasjStart = new BasicDBObject("$match", queryFasjStart);
            optionList.add(matchFasjStart);
            BasicDBObject queryFasjEnd = new BasicDBObject("fasj", new BasicDBObject("$lte", endTime));
            BasicDBObject matchFasjEnd = new BasicDBObject("$match", queryFasjEnd);
            optionList.add(matchFasjEnd);
        }

        BasicDBObject group = new BasicDBObject("$group", new BasicDBObject("_id", "$oneLevel")
                .append("num", new BasicDBObject("$sum", 1)));
        optionList.add(group);
        MongoCursor<Document> iterator = collection.aggregate(optionList).iterator();
        List<Map<String, Object>> resLst = new ArrayList<>();
        while (iterator.hasNext()) {
            Document document = iterator.next();
            Map<String, Object> map = new HashMap<String, Object>();
            String type = document.getString("_id");
            long num = Long.valueOf(document.get("num").toString());
          /*  if("101".equals(type)){
                map.put("name", "治安管理");
            }else if("212".equals(type)){
                map.put("name", "刑事侵财");
            }else if("105".equals(type)){
                map.put("name", "交通");
            }else if("209".equals(type)){
                map.put("name", "公共安全");
            }else if("101030068".equals(type)){
                map.put("name", "殴打他人");
            }else if("101030078".equals(type)){
                map.put("name", "盗窃");
            }else if("101030083".equals(type)){
                map.put("name", "故意损毁财物");
            }else if("101030079".equals(type)){
                map.put("name", "诈骗");
            }else if("qita".equals(type)){
                map.put("name", "其它");
            }*/
            if (null != type) {
                String ajName = getAjNameByAjbh(type, "1");
                map.put("code", type);
                map.put("count", num);
                map.put("name", ajName);
                resLst.add(map);
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("caseDao queryPieChartData need time: " + (end - start) / 1000d + "s");
        return resLst;
    }


    /*查询一定范围一段时间内饼图的数据
     * @param minHzb最小横坐标
     * @param maxHzb最大横坐标
     * @param minZzb最小纵坐标
     * @param maxZzb最大纵坐标
     * @param beginTime开始时间
     * @param endTime结束时间
     * @param flag 标识  ajxx表中的一二级之分  1：一级分类        2：二级分类            qita:其它
     * */
    /*public List<Map<String, Object>> queryPieChartData(double minHzb, double maxHzb, double minZzb, double maxZzb, Date beginTime, Date endTime, String flag) {
        MongoCollection<Document> collection = MongoDBConnection.getajjqConnect().getCollection("ajxx");
        BasicDBObject query = new BasicDBObject();//条件
        List<BasicDBObject> optionList = new ArrayList<>();
        BasicDBObject gtexStart = new BasicDBObject("zbx", new BasicDBObject("$gte", minHzb));
        BasicDBObject matchXStart = new BasicDBObject("$match", gtexStart);
        optionList.add(matchXStart);
        BasicDBObject gteXEnd = new BasicDBObject("zbx", new BasicDBObject("$lte", maxHzb));
        BasicDBObject matchXEnd = new BasicDBObject("$match", gteXEnd);
        optionList.add(matchXEnd);

        BasicDBObject gteyStart = new BasicDBObject("zby", new BasicDBObject("$gte", minZzb));
        BasicDBObject matchYStart = new BasicDBObject("$match", gteyStart);
        optionList.add(matchYStart);
        BasicDBObject gteYEnd = new BasicDBObject("zby", new BasicDBObject("$lte", maxZzb));
        BasicDBObject matchYEnd = new BasicDBObject("$match", gteYEnd);
        optionList.add(matchYEnd);

        if (beginTime != null && endTime != null) {
            BasicDBObject queryFasjStart = new BasicDBObject("fasj", new BasicDBObject("$gte", beginTime));
            BasicDBObject matchFasjStart = new BasicDBObject("$match", queryFasjStart);
            optionList.add(matchFasjStart);
            BasicDBObject queryFasjEnd = new BasicDBObject("fasj", new BasicDBObject("$lte", endTime));
            BasicDBObject matchFasjEnd = new BasicDBObject("$match", queryFasjEnd);
            optionList.add(matchFasjEnd);
        }
        if ("1".equals(flag)) {//一级分类
            BasicDBList values = new BasicDBList();
            values.add("101");//治安管理
            values.add("212");//刑事侵财
            values.add("105");//交通
            values.add("209");//公共安全
            BasicDBObject matchTypes = new BasicDBObject("aybh_1", new BasicDBObject("$in", values));
            BasicDBObject queryType = new BasicDBObject("$match", matchTypes);
            optionList.add(queryType);
            BasicDBObject group = new BasicDBObject("$group", new BasicDBObject("_id", "$aybh_1")
                    .append("num", new BasicDBObject("$sum", 1)));
            optionList.add(group);
        } else if ("2".equals(flag)) {//二级分类
            BasicDBList values = new BasicDBList();
            values.add("101030078");//盗窃
            values.add("101030068");//殴打他人
            values.add("101030083");//故意损毁财物
            values.add("101030079");//诈骗
            BasicDBObject matchTypes = new BasicDBObject("aybh", new BasicDBObject("$in", values));
            BasicDBObject queryType = new BasicDBObject("$match", matchTypes);
            optionList.add(queryType);
            BasicDBObject group = new BasicDBObject("$group", new BasicDBObject("_id", "$aybh")
                    .append("num", new BasicDBObject("$sum", 1)));
            optionList.add(group);
        } else if ("qita".equals(flag)) {
            List qitaLst = getChildByOneLevel("qita");
            BasicDBList values = new BasicDBList();
            for (int i = 0; i < qitaLst.size(); i++) {
                Map map = (Map) qitaLst.get(i);
                values.add(map.get("ajbh_2"));
            }

            BasicDBObject matchTypes = new BasicDBObject("aybh_1", new BasicDBObject("$in", values));
            BasicDBObject queryType = new BasicDBObject("$match", matchTypes);
            optionList.add(queryType);
            BasicDBObject group = new BasicDBObject("$group", new BasicDBObject("_id", "$aybh_1")
                    .append("num", new BasicDBObject("$sum", 1)));
            optionList.add(group);
        }
        MongoCursor<Document> iterator = collection.aggregate(optionList).iterator();
        List<Map<String, Object>> resLst = new ArrayList<>();
        while (iterator.hasNext()) {
            Document document = iterator.next();
            Map<String, Object> map = new HashMap<String, Object>();
            String type = document.getString("_id");
            long num = Long.valueOf(document.get("num").toString());
            map.put("code", type);
            map.put("count", num);
            if ("1".equals(flag)) {
                String ajName = getAjNameByAjbh(type, "1");
                map.put("name", ajName);
            } else if ("2".equals(flag)) {
                String ajName = getAjNameByAjbh(type, "1");
                map.put("name", ajName);
            } else if ("qita".equals(flag)) {
                map.put("name", "qita");
            }
            resLst.add(map);
        }
        return resLst;
    }*/

    /*查询一定范围一段时间内饼图的数据
     * @param minHzb最小横坐标
     * @param maxHzb最大横坐标
     * @param minZzb最小纵坐标
     * @param maxZzb最大纵坐标
     * @param beginTime开始时间
     * @param endTime结束时间
     * @param flag 标识    1：一级分类        2：二级分类            qita:其它
     * */
   /* public List<Map<String, Object>> queryPieChartData(double minHzb, double maxHzb, double minZzb, double maxZzb, Date beginTime, Date endTime, String flag) {
        MongoCollection<Document> collection = MongoDBConnection.getajjqConnect().getCollection("ajxx");
        BasicDBObject query = new BasicDBObject();//条件
        List<BasicDBObject> optionList = new ArrayList<>();
        BasicDBObject gtexStart = new BasicDBObject("x", new BasicDBObject("$gte", minHzb));
        BasicDBObject matchXStart = new BasicDBObject("$match", gtexStart);
        optionList.add(matchXStart);
        BasicDBObject gteXEnd = new BasicDBObject("x", new BasicDBObject("$lte", maxHzb));
        BasicDBObject matchXEnd = new BasicDBObject("$match", gteXEnd);
        optionList.add(matchXEnd);

        BasicDBObject gteyStart = new BasicDBObject("y", new BasicDBObject("$gte", minZzb));
        BasicDBObject matchYStart = new BasicDBObject("$match", gteyStart);
        optionList.add(matchYStart);
        BasicDBObject gteYEnd = new BasicDBObject("y", new BasicDBObject("$lte", maxZzb));
        BasicDBObject matchYEnd = new BasicDBObject("$match", gteYEnd);
        optionList.add(matchYEnd);

        if (beginTime != null && endTime != null) {
            BasicDBObject queryFasjStart = new BasicDBObject("fasj", new BasicDBObject("$gte", beginTime));
            BasicDBObject matchFasjStart = new BasicDBObject("$match", queryFasjStart);
            optionList.add(matchFasjStart);
            BasicDBObject queryFasjEnd = new BasicDBObject("fasj", new BasicDBObject("$lte", endTime));
            BasicDBObject matchFasjEnd = new BasicDBObject("$match", queryFasjEnd);
            optionList.add(matchFasjEnd);
        }
        if ("1".equals(flag)) {//一级分类
            BasicDBList values = new BasicDBList();
            values.add("101");//治安管理
            values.add("212");//刑事侵财
            values.add("105");//交通
            values.add("209");//公共安全
            BasicDBObject matchTypes = new BasicDBObject("aybh_1", new BasicDBObject("$in", values));
            BasicDBObject queryType = new BasicDBObject("$match", matchTypes);
            optionList.add(queryType);
            BasicDBObject group = new BasicDBObject("$group", new BasicDBObject("_id", "$aybh_1")
                    .append("num", new BasicDBObject("$sum", 1)));
            optionList.add(group);
        } else if ("2".equals(flag)) {//二级分类
            BasicDBList values = new BasicDBList();
            values.add("101030078");//盗窃
            values.add("101030068");//殴打他人
            values.add("101030083");//故意损毁财物
            values.add("101030079");//诈骗
            BasicDBObject matchTypes = new BasicDBObject("aybh", new BasicDBObject("$in", values));
            BasicDBObject queryType = new BasicDBObject("$match", matchTypes);
            optionList.add(queryType);
            BasicDBObject group = new BasicDBObject("$group", new BasicDBObject("_id", "$aybh")
                    .append("num", new BasicDBObject("$sum", 1)));
            optionList.add(group);
        } else if ("qita".equals(flag)) {
            List qitaLst = getChildByOneLevel("qita");
            BasicDBList values = new BasicDBList();
            for (int i = 0; i < qitaLst.size(); i++) {
                Map map = (Map) qitaLst.get(i);
                values.add(map.get("ajbh_2"));
            }
          *//*  BasicDBList values = new BasicDBList();
            values.add("102");//出入境边防
            values.add("103");//消防管理
            values.add("104");//网络安全
            values.add("106");//禁毒
            values.add("107");//其他
            values.add("208");//国家安全
            values.add("210");//破坏市场经济
            values.add("211");//侵犯人权民权
            values.add("213");//妨害社会秩序
            values.add("214");//危害国防利益
            values.add("215");//贪污贿赂罪
            values.add("216");//渎职罪
            values.add("217");//军人违反职责*//*
            BasicDBObject matchTypes = new BasicDBObject("aybh_1", new BasicDBObject("$in", values));
            BasicDBObject queryType = new BasicDBObject("$match", matchTypes);
            optionList.add(queryType);
            BasicDBObject group = new BasicDBObject("$group", new BasicDBObject("_id", "$aybh_1")
                    .append("num", new BasicDBObject("$sum", 1)));
            optionList.add(group);
        }
        MongoCursor<Document> iterator = collection.aggregate(optionList).iterator();
        List<Map<String, Object>> resLst = new ArrayList<>();
        while (iterator.hasNext()) {
            Document document = iterator.next();
            Map<String, Object> map = new HashMap<String, Object>();
            String type = document.getString("_id");
            long num = Long.valueOf(document.get("num").toString());
            map.put("code", type);
            map.put("count", num);
            if ("1".equals(flag)) {//一级分类
                String ajName = getAjNameByAjbh(type, "1");
                map.put("name", ajName);
            } else if ("2".equals(flag)) {//二级分类
                String ajName = getAjNameByAjbh(type, "2");
                map.put("name", ajName);
            } else if ("qita".equals(flag)) {
                map.put("name", "qita");
            }
            resLst.add(map);
        }
        return resLst;
    }*/

    /* 根据一级分类查询二级分类类型及数量
     * @param minHzb最小横坐标
     * @param maxHzb最大横坐标
     * @param minZzb最小纵坐标
     * @param maxZzb最大纵坐标
     * @param beginTime开始时间
     * @param endTime结束时间
     * @param firtype一级编码
     * */
    public List<Map<String, Object>> querySecTypeNumByFirType(double minHzb, double maxHzb, double minZzb, double maxZzb, Date beginTime, Date endTime, String firtype) {
        MongoCollection<Document> collection = MongoDBConnection.getajjqConnect().getCollection("ajxx");
        BasicDBObject query = new BasicDBObject();//条件
        List<BasicDBObject> optionList = new ArrayList<>();
        BasicDBObject gtexStart = new BasicDBObject("zbx", new BasicDBObject("$gte", minHzb));
        BasicDBObject matchXStart = new BasicDBObject("$match", gtexStart);
        optionList.add(matchXStart);
        BasicDBObject gteXEnd = new BasicDBObject("zbx", new BasicDBObject("$lte", maxHzb));
        BasicDBObject matchXEnd = new BasicDBObject("$match", gteXEnd);
        optionList.add(matchXEnd);

        BasicDBObject gteyStart = new BasicDBObject("zby", new BasicDBObject("$gte", minZzb));
        BasicDBObject matchYStart = new BasicDBObject("$match", gteyStart);
        optionList.add(matchYStart);
        BasicDBObject gteYEnd = new BasicDBObject("zby", new BasicDBObject("$lte", maxZzb));
        BasicDBObject matchYEnd = new BasicDBObject("$match", gteYEnd);
        optionList.add(matchYEnd);

        if (beginTime != null && endTime != null) {
            BasicDBObject queryFasjStart = new BasicDBObject("fasj", new BasicDBObject("$gte", beginTime));
            BasicDBObject matchFasjStart = new BasicDBObject("$match", queryFasjStart);
            optionList.add(matchFasjStart);
            BasicDBObject queryFasjEnd = new BasicDBObject("fasj", new BasicDBObject("$lte", endTime));
            BasicDBObject matchFasjEnd = new BasicDBObject("$match", queryFasjEnd);
            optionList.add(matchFasjEnd);
        }

        List oneLst = new ArrayList();
        oneLst.add("101");//治安管理
        oneLst.add("212");//刑事侵财
        oneLst.add("105");//交通
        oneLst.add("209");//公共安全

        if (StringUtils.isNotEmpty(firtype)) {
            if (oneLst.contains(firtype)) {
                BasicDBObject matchTypes = new BasicDBObject("aybh_1", firtype);
                BasicDBObject queryType = new BasicDBObject("$match", matchTypes);
                optionList.add(queryType);
                if ("101".equals(firtype)) {//治安管理
                    BasicDBObject group = new BasicDBObject("$group", new BasicDBObject("_id", "$aybh_2")
                            .append("num", new BasicDBObject("$sum", 1)));
                    optionList.add(group);
                } else {
                    BasicDBObject group = new BasicDBObject("$group", new BasicDBObject("_id", "$aybh")
                            .append("num", new BasicDBObject("$sum", 1)));
                    optionList.add(group);
                }
            }
            if ("qita".equals(firtype)) {//其他
                List qitaLst = getChildByOneLevel("qita");
                BasicDBList values = new BasicDBList();
                for (int i = 0; i < qitaLst.size(); i++) {
                    Map map = (Map) qitaLst.get(i);
                    values.add(map.get("ajbh_2"));
                }
                BasicDBObject matchTypes = new BasicDBObject("aybh_1", new BasicDBObject("$in", values));
                BasicDBObject queryType = new BasicDBObject("$match", matchTypes);
                optionList.add(queryType);
                BasicDBObject group = new BasicDBObject("$group", new BasicDBObject("_id", "$aybh_1")
                        .append("num", new BasicDBObject("$sum", 1)));
                optionList.add(group);
            }
        }
        MongoCursor<Document> iterator = collection.aggregate(optionList).iterator();
        List<Map<String, Object>> resLst = new ArrayList();
        while (iterator.hasNext()) {
            Document document = iterator.next();
            Map map = new HashMap();
            String type = document.getString("_id");
            long num = Long.valueOf(document.get("num").toString());
            String name = getAjNameByAjbh(type, "2");
            map.put("code", type);
            map.put("name", name);
            map.put("count", num);
            resLst.add(map);
        }
        return resLst;
    }


    /*查询一定范围一段时间内全部案件、刑事侵财、治安管理、盗窃、殴打他人等折线图的数据
     * @param minHzb最小横坐标
     * @param maxHzb最大横坐标
     * @param minZzb最小纵坐标
     * @param maxZzb最大纵坐标
     * @param beginTime开始时间
     * @param endTime结束时间
     * @param period 时间段间隔
     * */
    public Map<String, Object> queryLineChartData(double minHzb, double maxHzb, double minZzb, double maxZzb, Date beginTime, Date endTime, List period) {
        long start = System.currentTimeMillis();
        MongoCollection<Document> collection = MongoDBConnection.getajjqConnect().getCollection("ajxx");
        List<BasicDBObject> pipeline = new LinkedList<>();
        BasicDBObject[] matchCondition = new BasicDBObject[]{
                new BasicDBObject("fasj", new BasicDBObject("$gte", beginTime)),
                new BasicDBObject("fasj", new BasicDBObject("$lte", endTime)),
                new BasicDBObject("zbx", new BasicDBObject("$gte", minHzb)),
                new BasicDBObject("zbx", new BasicDBObject("$lte", maxHzb)),
                new BasicDBObject("zby", new BasicDBObject("$gte", minZzb)),
                new BasicDBObject("zby", new BasicDBObject("$lte", maxZzb))
        };
        BasicDBObject match = new BasicDBObject("$match", new BasicDBObject("$and", matchCondition));

        BasicDBObject fieldRemain1 = new BasicDBObject();

        BasicDBList subtract = new BasicDBList();
        subtract.add("$fasj");
        subtract.add(beginTime);

        BasicDBList divide = new BasicDBList();
        divide.add(new BasicDBObject("$subtract", subtract));
        divide.add(1000 * 3600 * 24);

        fieldRemain1.put("oneLevel", 1);
        fieldRemain1.put("fasj", new BasicDBObject("$floor", new BasicDBObject("$divide", divide)));
        BasicDBObject project1 = new BasicDBObject("$project", fieldRemain1);

        BasicDBObject facetCondition = new BasicDBObject();

        BasicDBObject bucket1 = new BasicDBObject();
        bucket1.put("groupBy", "$fasj");
        bucket1.put("boundaries", period);
        bucket1.put("default", null);
        bucket1.put("output", new BasicDBObject("count", new BasicDBObject("$sum", 1)));

        BasicDBObject totalProject = new BasicDBObject();
        BasicDBList divideLst = new BasicDBList();
        divideLst.add(new BasicDBObject("$floor", "$_id"));
        divideLst.add(period.get(1));
        totalProject.put("_id", new BasicDBObject("$divide", divideLst));
        totalProject.put("oneLevel", "0");
        totalProject.put("count", 1);
        BasicDBList totalLst = new BasicDBList();
        totalLst.add(new BasicDBObject("$bucket", bucket1));
        totalLst.add(new BasicDBObject("$project", totalProject));

        BasicDBObject bucket2 = new BasicDBObject();
        bucket2.put("groupBy", "$fasj");
        bucket2.put("boundaries", period);
        bucket2.put("default", null);
        BasicDBObject output = new BasicDBObject();
        output.put("count", new BasicDBObject("$sum", 1));
        output.put("oneLevel", new BasicDBObject("$push", "$oneLevel"));
        bucket2.put("output", output);

        BasicDBObject catalogProject1 = new BasicDBObject();
        BasicDBList dividecLst = new BasicDBList();
        dividecLst.add(new BasicDBObject("$floor", "$_id"));
        dividecLst.add(period.get(1));
        catalogProject1.put("_id", new BasicDBObject("$divide", dividecLst));
        catalogProject1.put("oneLevel", 1);

        BasicDBObject groupCondition = new BasicDBObject();
        BasicDBObject group_id = new BasicDBObject();
        group_id.put("fasj", "$_id");
        group_id.put("oneLevel", "$oneLevel");
        groupCondition.put("_id", group_id);
        groupCondition.put("count", new BasicDBObject("$sum", 1));

        BasicDBObject catalogProject2 = new BasicDBObject();
        catalogProject2.put("_id", 0);
        catalogProject2.put("fasj", "$_id.fasj");
        catalogProject2.put("oneLevel", "$_id.oneLevel");
        catalogProject2.put("count", 1);

        BasicDBList catalogLst = new BasicDBList();
        catalogLst.add(new BasicDBObject("$bucket", bucket2));
        catalogLst.add(new BasicDBObject("$project", catalogProject1));
        catalogLst.add(new BasicDBObject("$unwind", new BasicDBObject("path", "$oneLevel")));
        catalogLst.add(new BasicDBObject("$group", groupCondition));
        catalogLst.add(new BasicDBObject("$project", catalogProject2));

        facetCondition.put("total", totalLst);
        facetCondition.put("catalog", catalogLst);

        BasicDBObject facet = new BasicDBObject("$facet", facetCondition);

        BasicDBObject fieldRemain2 = new BasicDBObject();
        BasicDBList unionLst = new BasicDBList();
        unionLst.add("$total");
        unionLst.add("$catalog");
        fieldRemain2.put("total", new BasicDBObject("$setUnion", unionLst));
        BasicDBObject project2 = new BasicDBObject("$project", fieldRemain2);

        pipeline.add(match);
        pipeline.add(project1);
        pipeline.add(facet);
        pipeline.add(project2);

        MongoCursor<Document> documentMongoCursor = collection.aggregate(pipeline).allowDiskUse(true).iterator();
        Map<String, Object> reaMap = new HashMap<>();
        while (documentMongoCursor.hasNext()) {
            Document document = documentMongoCursor.next();
            List totaLst = (List) document.get("total");
            List zhian = new ArrayList();//治安管理
            List xingshi = new ArrayList();//刑事侵财
            List daoqie = new ArrayList();//盗窃
            List ouda = new ArrayList();//殴打他人
            List quanbu = new ArrayList();//全部案件
            for (int i = 0; i < totaLst.size(); i++) {
                Map map = (Map) totaLst.get(i);
                String oneLevel = null;
                if (null != map.get("oneLevel")) {
                    Map oneMap = new HashMap();
                    oneLevel = map.get("oneLevel").toString();
                    if ("101".equals(oneLevel)) {//治安管理
                        //oneMap.put("code", "101");
                        if (null != map.get("fasj")) {
                            oneMap.put("id", Math.round(Double.parseDouble(map.get("fasj").toString())));
                            oneMap.put("count", map.get("count"));
                            zhian.add(oneMap);
                        }
                    } else if ("212".equals(oneLevel)) {//刑事侵财
                        //oneMap.put("code", "212");
                        if (null != map.get("fasj")) {
                            oneMap.put("id", Math.round(Double.parseDouble(map.get("fasj").toString())));
                            oneMap.put("count", map.get("count"));
                            xingshi.add(oneMap);
                        }
                    } else if ("101030078".equals(oneLevel)) {//盗窃
                        //oneMap.put("code", "101030078");
                        if (null != map.get("fasj")) {
                            oneMap.put("id", Math.round(Double.parseDouble(map.get("fasj").toString())));
                            oneMap.put("count", map.get("count"));
                            daoqie.add(oneMap);
                        }
                    } else if ("101030068".equals(oneLevel)) {//殴打他人
                        //oneMap.put("code", "101030068");
                        if (null != map.get("fasj")) {
                            oneMap.put("id", Math.round(Double.parseDouble(map.get("fasj").toString())));
                            oneMap.put("count", map.get("count"));
                            ouda.add(oneMap);
                        }
                    } else if ("0".equals(oneLevel)) {//全部案件
                        //oneMap.put("code", "0");
                        if (null != map.get("_id")) {
                            oneMap.put("id", Math.round(Double.parseDouble(map.get("_id").toString())));
                            oneMap.put("count", map.get("count"));
                            quanbu.add(oneMap);
                        }
                    }
                }
            }
            reaMap.put("0", quanbu);//全部案件
            reaMap.put("101030068", ouda);//殴打他人
            reaMap.put("101030078", daoqie);//盗窃
            reaMap.put("212", xingshi);//刑事侵财
            reaMap.put("101", zhian);// 治安管理
        }
        long end = System.currentTimeMillis();
        System.out.println("CaseDao queryLineChartData need time: " + (end - start) / 1000d + "s");
        return reaMap;
    }
    /*public List<Map<String, Object>> queryLineChartData(double minHzb, double maxHzb, double minZzb, double maxZzb, Date beginTime, Date endTime, String flag) {
        MongoCollection<Document> collection = MongoDBConnection.getajjqConnect().getCollection("ajxx");
        BasicDBObject query = new BasicDBObject();//条件
        List<BasicDBObject> optionList = new ArrayList<>();
        BasicDBObject gtexStart = new BasicDBObject("zbx", new BasicDBObject("$gte", minHzb));
        BasicDBObject matchXStart = new BasicDBObject("$match", gtexStart);
        optionList.add(matchXStart);
        BasicDBObject gteXEnd = new BasicDBObject("zbx", new BasicDBObject("$lte", maxHzb));
        BasicDBObject matchXEnd = new BasicDBObject("$match", gteXEnd);
        optionList.add(matchXEnd);

        BasicDBObject gteyStart = new BasicDBObject("zby", new BasicDBObject("$gte", minZzb));
        BasicDBObject matchYStart = new BasicDBObject("$match", gteyStart);
        optionList.add(matchYStart);
        BasicDBObject gteYEnd = new BasicDBObject("zby", new BasicDBObject("$lte", maxZzb));
        BasicDBObject matchYEnd = new BasicDBObject("$match", gteYEnd);
        optionList.add(matchYEnd);

        if (beginTime != null && endTime != null) {
            //query.put("fasj", new BasicDBObject("$gte", beginTime).append("$lte", endTime));
            BasicDBObject queryFasjStart = new BasicDBObject("fasj", new BasicDBObject("$gte", beginTime));
            BasicDBObject matchFasjStart = new BasicDBObject("$match", queryFasjStart);
            optionList.add(matchFasjStart);
            BasicDBObject queryFasjEnd = new BasicDBObject("fasj", new BasicDBObject("$lte", endTime));
            BasicDBObject matchFasjEnd = new BasicDBObject("$match", queryFasjEnd);
            optionList.add(matchFasjEnd);
        }

        if ("1".equals(flag)) {//一级分类
            BasicDBList values = new BasicDBList();
            values.add("101");//治安管理
            values.add("212");//刑事侵财
            values.add("101030078");//盗窃
            values.add("101030068");//殴打他人
            BasicDBObject matchTypes = new BasicDBObject("oneLevel", new BasicDBObject("$in", values));
            BasicDBObject queryType = new BasicDBObject("$match", matchTypes);
            optionList.add(queryType);

            BasicDBObject group = new BasicDBObject("$group", new BasicDBObject("_id", "$oneLevel")
                    .append("num", new BasicDBObject("$sum", 1)));
            optionList.add(group);
        } else if ("0".equals(flag)) {//全部案件
            BasicDBObject group = new BasicDBObject("$group", new BasicDBObject("_id", null)
                    .append("num", new BasicDBObject("$sum", 1)));
            optionList.add(group);
        }

        MongoCursor<Document> iterator = collection.aggregate(optionList).iterator();
        List<Map<String, Object>> resLst = new ArrayList();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        while (iterator.hasNext()) {
            Document document = iterator.next();
            Map<String, Object> map = new HashMap<String, Object>();
            String type = document.getString("_id");
            long num = Long.valueOf(document.get("num").toString());
            if (type == null) {//总案件
                map.put("code", "0");
                map.put("name", "全部案件");
                map.put("count", num);
            } else if ("101".equals(type)) {//治安管理
                map.put("code", "101");
                map.put("name", "治安管理");
                map.put("count", num);
            } else if ("212".equals(type)) { //刑事侵财
                map.put("code", "212");
                map.put("name", "刑事侵财");
                map.put("count", num);
            } else if ("101030078".equals(type)) {//盗窃
                map.put("code", "101030078");
                map.put("name", "盗窃");
                map.put("count", num);
            } else if ("101030068".equals(type)) {//殴打他人
                map.put("code", "101030068");
                map.put("name", "殴打他人");
                map.put("count", num);
            }
            resLst.add(map);
        }
        return resLst;
    }*/

    /* 根据案件编号查询案件详情
     * @param ajbh案件编号
     * */
    public Map<String, Object> queryCaseDetail(String ajbh) {
        MongoCollection<Document> collection = MongoDBConnection.getajjqConnect().getCollection("ajxx");
        FindIterable<Document> findIterable = collection.find(new Document("ajbh", ajbh));
        MongoCursor<Document> cursor = findIterable.iterator();
        Map<String, Object> map = new HashMap<String, Object>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        while (cursor.hasNext()) {
            Document document = cursor.next();
            map.put("ajmc", document.get("ajmc"));//案由名称
            map.put("x", document.get("zbx"));
            map.put("y", document.get("zby"));
            map.put("ajzt_zw", document.get("ajzt_zw"));//案件状态
            map.put("ajlx_zw", document.get("ajlx_zw"));//案件类型
            map.put("aylx_mc", document.get("aylx_mc"));
            map.put("fasj", sdf.format(document.get("fasj")));
            map.put("zbr_xm", document.get("zbr_xm"));//主办人姓名
            map.put("province", document.get("province"));//省
            map.put("city", document.get("city"));//市
            map.put("district", document.get("district"));//区县
            map.put("city_code_zw", document.get("city_code_zw"));//案件信息-省
            map.put("bur_code_zw", document.get("bur_code_zw"));//案件信息-市
            map.put("sta_code_zw", document.get("sta_code_zw"));//案件信息-区县
        }
        return map;
    }


    /* 根据案件编号查询其案件名称
     * @param bh案件编号
     * @param level案件级别    1：一级      2：二级
     * */
    public String getAjNameByAjbh(String bh, String level) {
        MongoCollection<Document> collection = MongoDBConnection.getajjqConnect().getCollection("typeName");
        FindIterable<Document> findIterable = null;
        if ("1".equals(level)) {//一级
            findIterable = collection.find(new Document("ajbh_1", bh));
        } else if ("2".equals(level)) {//二级
            findIterable = collection.find(new Document("ajbh_2", bh));
        }
        MongoCursor<Document> cursor = findIterable.iterator();
        String ajmc = null;
        while (cursor.hasNext()) {
            Document document = cursor.next();
            if ("1".equals(level)) {//一级
                ajmc = document.getString("ajmcjc_1");
            } else if ("2".equals(level)) {
                ajmc = document.getString("ajmcjc_2");
            }
        }
        return ajmc;
    }

    /* 获取所有的一级分类编号
     * */
    public List getAllOneLevel() {
        MongoCollection<Document> collection = MongoDBConnection.getajjqConnect().getCollection("typeName");
        List<BasicDBObject> optionList = new ArrayList<>();
        BasicDBObject group = new BasicDBObject("$group", new BasicDBObject("_id", "$ajbh_1")
                .append("num", new BasicDBObject("$sum", 1)));
        FindIterable<Document> findIterable = collection.find();
        MongoCursor<Document> cursor = findIterable.iterator();
        List list = new ArrayList();
        while (cursor.hasNext()) {
            Document document = cursor.next();
            String type = document.getString("_id");
            list.add(type);
        }
        return list;
    }

    /* 根据一级案件编号查询所有二级案件类型
     * @param onebh一级编号
     * */
    public List getChildByOneLevel(String onebh) {
        MongoCollection<Document> collection = MongoDBConnection.getajjqConnect().getCollection("typeName");
        FindIterable<Document> findIterable = collection.find(new Document("ajbh_1", onebh));
        MongoCursor<Document> cursor = findIterable.iterator();
        List resLst = new ArrayList();
        while (cursor.hasNext()) {
            Document document = cursor.next();
            Map<String, Object> map = new HashMap<>();
            map.put("ajbh_2", document.get("ajbh_2"));
            map.put("ajmcjc_2", document.get("ajmcjc_2"));
            resLst.add(map);
        }
        return resLst;
    }

    /* 查询出所以派出所辖区的案件密度值
     * */
    public Map queryCaseAreaDensity(String startTime, String endTime) {
        long start = System.currentTimeMillis();
        MongoCollection<Document> collection = MongoDBConnection.getajjqConnect().getCollection("caseDensity");
        List<BasicDBObject> pipeline = new LinkedList<>();

        BasicDBObject fieldRemain1 = new BasicDBObject();
        fieldRemain1.put("pcsId", 1);
        fieldRemain1.put("cbdw_bh", 1);
        fieldRemain1.put("density", new BasicDBObject("$objectToArray", "$density"));
        BasicDBObject project1 = new BasicDBObject("$project", fieldRemain1);

        BasicDBObject fieldRemain2 = new BasicDBObject();
        BasicDBObject filterDensity = new BasicDBObject();
        BasicDBObject filterCondition = new BasicDBObject();
        filterDensity.put("$filter", filterCondition);
        filterCondition.put("input", "$density");

        filterCondition.put("as", "d");
        BasicDBList gvalues = new BasicDBList();
        gvalues.add("$$d.k");
        gvalues.add(startTime);
        BasicDBList lvalues = new BasicDBList();
        lvalues.add("$$d.k");
        lvalues.add(endTime);
        BasicDBObject[] timeCondition = new BasicDBObject[]{
                new BasicDBObject("$gte", gvalues),
                new BasicDBObject("$lte", lvalues)
        };

        filterCondition.put("cond", new BasicDBObject("$and", timeCondition));
        fieldRemain2.put("pcsId", 1);
        fieldRemain2.put("cbdw_bh", 1);
        fieldRemain2.put("density", filterDensity);
        BasicDBObject project2 = new BasicDBObject("$project", fieldRemain2);

        BasicDBObject groupCondition1 = new BasicDBObject();
        groupCondition1.put("_id", "$_id");
        groupCondition1.put("pcsId", new BasicDBObject("$first", "$pcsId"));
        groupCondition1.put("cbdw_bh", new BasicDBObject("$first", "$cbdw_bh"));
        groupCondition1.put("density", new BasicDBObject("$push", "$density.v"));
        BasicDBObject group1 = new BasicDBObject("$group", groupCondition1);
        BasicDBObject windCondition = new BasicDBObject();
        windCondition.put("path", "$density");
        windCondition.put("preserveNullAndEmptyArrays", false);
        BasicDBObject wind = new BasicDBObject("$unwind", windCondition);

        BasicDBObject fieldRemain3 = new BasicDBObject();
        fieldRemain3.put("pcsId", 1);
        fieldRemain3.put("cbdw_bh", 1);
        BasicDBObject reduceCondition = new BasicDBObject();
        reduceCondition.put("input", "$density");
        reduceCondition.put("initialValue", 0);
        BasicDBList addValues = new BasicDBList();
        addValues.add("$$value");
        addValues.add("$$this");
        reduceCondition.put("in", new BasicDBObject("$add", addValues));
        fieldRemain3.put("density", new BasicDBObject("$reduce", reduceCondition));
        BasicDBObject project3 = new BasicDBObject("$project", fieldRemain3);

        BasicDBObject match = new BasicDBObject("$match", new BasicDBObject("density", new BasicDBObject("$ne", 0)));

        BasicDBObject fieldRemain4 = new BasicDBObject();
        fieldRemain4.put("pcsId", 1);
        fieldRemain4.put("cbdw_bh", 1);
        fieldRemain4.put("density", new BasicDBObject("$log10", "$density"));
        BasicDBObject project4 = new BasicDBObject("$project", fieldRemain4);

        BasicDBObject sort = new BasicDBObject("$sort", new BasicDBObject("density", -1));

        BasicDBObject groupCondition2 = new BasicDBObject();
        groupCondition2.put("_id", "max");
        BasicDBObject array = new BasicDBObject();
        array.put("pcsId", "$pcsId");
        array.put("cbdw_bh", "$cbdw_bh");
        array.put("density", "$density");
        groupCondition2.put("arrayDensity", new BasicDBObject("$push", array));
        groupCondition2.put("max_density", new BasicDBObject("$max", "$density"));
        groupCondition2.put("min_density", new BasicDBObject("$min", "$density"));
        BasicDBObject group2 = new BasicDBObject("$group", groupCondition2);

        BasicDBObject fieldRemain5 = new BasicDBObject();
        fieldRemain5.put("arrayDensity", "$arrayDensity");
        fieldRemain5.put("max_density", "$max_density");
        fieldRemain5.put("min_density", "$min_density");
        fieldRemain5.put("absminDensity", new BasicDBObject("$abs", "$min_density"));
        BasicDBObject project5 = new BasicDBObject("$project", fieldRemain5);

        pipeline.add(project1);
        pipeline.add(project2);
        pipeline.add(group1);
        pipeline.add(wind);
        pipeline.add(project3);
        pipeline.add(match);
        pipeline.add(project4);
        pipeline.add(sort);
        pipeline.add(group2);
        pipeline.add(project5);

        MongoCursor<Document> documentMongoCursor = collection.aggregate(pipeline).allowDiskUse(true).iterator();
        //把结果集输出成list类型
        //List list = new LinkedList<>();
        Map resMap = new HashMap();
        while (documentMongoCursor.hasNext()) {
            Document document = documentMongoCursor.next();
            double absminDensity = 0;
            if (null != document.get("absminDensity")) {
                absminDensity = Math.ceil(Double.parseDouble(document.get("absminDensity").toString()));
            }
            List arrayDensity = (List) document.get("arrayDensity");
            double max_density = Double.parseDouble(document.get("max_density").toString());
            double min_density = Double.parseDouble(document.get("min_density").toString());

            for (int i = 0; i < arrayDensity.size(); i++) {
                Map map = (Map) arrayDensity.get(i);
                //Map<String, Object> newMap = new HashMap<String, Object>();
                String pcsId = null;
                String cbdw_bh = null;
                if (null != map.get("pcsId")) {
                    pcsId = map.get("pcsId").toString();
                }
                if (null != map.get("cbdw_bh")) {
                    cbdw_bh = map.get("cbdw_bh").toString();
                }
                double density = Double.parseDouble(map.get("density").toString()) + absminDensity;
                double newDensity = Math.round(density * (10 / (max_density - min_density)));
                //newMap.put("pcsId", pcsId);
                //newMap.put("cbdw_bh", cbdw_bh);
                resMap.put(pcsId, newDensity > 10 ? 10 : newDensity);
                //list.add(newMap);
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("caseDao queryCaseAreaDensity need time: " + (end - start) / 1000d + "s");
        //System.out.println("最大值: " + Collections.max(densityLst)+"--最小值："+Collections.min(densityLst));
        return resMap;
    }
}


