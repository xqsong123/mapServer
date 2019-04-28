package com.map.repository;

import com.map.utils.MongoDBConnection;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoSocketReadException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.*;

public class ChartDataDao {
    private Log log = LogFactory.getLog(ChartDataDao.class);

    /* 获取一定范围内右侧统计图中人口柱状图的数据
     * @param minHzb最小横坐标
     * @param maxHzb最大横坐标
     * @param minZzb最小纵坐标
     * @param maxZzb最大纵坐标
     * */
    public Map<String, Object> getPopChartData(String mapLevel, double minHzb, double maxHzb, double minZzb, double maxZzb) {
        MongoCollection<Document> collection = null;
        if (mapLevel.equals("7") || mapLevel.equals("8")) {//7-8级  30万
            collection = MongoDBConnection.getConnect2().getCollection("popQuantity_30");
        } else if (mapLevel.equals("9") || mapLevel.equals("10")) {//9-10级   50万
            collection = MongoDBConnection.getConnect2().getCollection("popQuantity_50");
        } else if (mapLevel.equals("11") || mapLevel.equals("12")) {//11-12级   150万
            collection = MongoDBConnection.getConnect2().getCollection("popQuantity_150");
        } else if (mapLevel.equals("13") || mapLevel.equals("14") || mapLevel.equals("15")) {//13-15级    300万
            collection = MongoDBConnection.getConnect2().getCollection("popQuantity_300");
        } else if (mapLevel.equals("16") || mapLevel.equals("17") || mapLevel.equals("18") || mapLevel.equals("19") || mapLevel.equals("20")) {//18-20级    全部
            collection = MongoDBConnection.getConnect2().getCollection("popQuantity");
        }

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

        BasicDBObject group = new BasicDBObject("$group", new BasicDBObject("_id", "$type")
                .append("totalNum", new BasicDBObject("$sum","$num")));
//        DBObject groupFields = new BasicDBObject();
//        groupFields = new BasicDBObject("_id", "$type");
//        groupFields.put("totalNum", new BasicDBObject("$sum", "$num"));
//        DBObject group = new BasicDBObject("$group", groupFields);
        optionList.add(group);

        //柱状图数据
        Map<String, Object> popBarMap = new HashMap<String, Object>();

        Iterator<Document> iterator = collection.aggregate(optionList).iterator();
        while (iterator.hasNext()){
            Document document = iterator.next();
            String firtype = document.getString("_id");
            long totalNum = Long.valueOf(document.get("totalNum").toString());
            if(StringUtils.isEmpty(firtype)){
                continue;
            }
            if (firtype.equals("1")) {//总人口
                popBarMap.put("totalPop", totalNum);
            } else if (firtype.equals("2")) {//常口
                popBarMap.put("ckpop", totalNum);
            } else if (firtype.equals("3")) {//流口
                popBarMap.put("lkpop", totalNum);
            } else if (firtype.equals("4")) {//重点
                popBarMap.put("zdpop", totalNum);
            } else if (firtype.equals("5")) {//境外
                popBarMap.put("jwpop", totalNum);
            }
        }
//        if(iterator != null){
//            ((MongoCursor<Document>) iterator).close();
//        }
//        try {
//            ((MongoCursor<Document>) iterator).close();
//        } catch (Exception e){
//            log.info("CharDataDao.getPopChartData.line 90 游标关闭异常");
//        }

        return popBarMap;
    }

    /* 获取一定范围内右侧统计图中单位柱状图的数据
     * @param minHzb最小横坐标
     * @param maxHzb最大横坐标
     * @param minZzb最小纵坐标
     * @param maxZzb最大纵坐标
     * */
    public Map<String, Object> getDwChartData(String mapLevel, double minHzb, double maxHzb, double minZzb, double maxZzb) {
        MongoCollection<Document> collection = null;
        if (mapLevel.equals("7") || mapLevel.equals("8")) {//7-8级  30万
            collection = MongoDBConnection.getConnect2().getCollection("dwQuantity_30");
        } else if (mapLevel.equals("9") || mapLevel.equals("10")) {//9-10级   50万
            collection = MongoDBConnection.getConnect2().getCollection("dwQuantity_50");
        } else if (mapLevel.equals("11") || mapLevel.equals("12")) {//11-12级   150万
            collection = MongoDBConnection.getConnect2().getCollection("dwQuantity_150");
        } else {//13-20级    全部
            collection = MongoDBConnection.getConnect2().getCollection("dwQuantity");
        }

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

        BasicDBObject group = new BasicDBObject("$group", new BasicDBObject("_id", "$type")
                .append("totalNum", new BasicDBObject("$sum","$num")));
//        DBObject groupFields = new BasicDBObject();
//        groupFields = new BasicDBObject("_id", "$type");
//        groupFields.put("totalNum", new BasicDBObject("$sum", "$num"));
//        DBObject group = new BasicDBObject("$group", groupFields);
        optionList.add(group);

        //柱状图数据
        Map<String, Object> popBarMap = new HashMap<String, Object>();
        Iterator<Document> iterator =  collection.aggregate(optionList).iterator();
        while (iterator.hasNext()){
            Document document = iterator.next();
            String firtype = document.getString("_id");
            long totalNum = Long.valueOf(document.get("totalNum").toString());
            if(StringUtils.isEmpty(firtype)){
                continue;
            }
            if (firtype.equals("1")) {//总单位
                popBarMap.put("totalDw", totalNum);
            } else if (firtype.equals("2")) {//普通单位
                popBarMap.put("ptdw", totalNum);
            } else if (firtype.equals("3")) {//特种单位
                log.info("有特种单位啊啊");
                popBarMap.put("tzdw", totalNum);
            } else if (firtype.equals("5")) {//九小场所
                popBarMap.put("jxcs", totalNum);
            } else if (firtype.equals("4")) {//保护单位
                log.info("有保护单位啊啊");
                popBarMap.put("bhdw", totalNum);
            }
        }
//        try {
//            ((MongoCursor<Document>) iterator).close();
//        } catch (Exception e){
//            log.info("CharDataDao.getDwChartData.line 164 游标关闭异常");
//        }

        return popBarMap;
    }

    /* 获取一定范围内右侧统计图中房屋的数据
     * @param minHzb最小横坐标
     * @param maxHzb最大横坐标
     * @param minZzb最小纵坐标
     * @param maxZzb最大纵坐标
     * */
    public Map<String, Object> getFwChartData(String mapLevel, double minHzb, double maxHzb, double minZzb, double maxZzb) {
        MongoCollection<Document> collection = null;
        if (mapLevel.equals("7") || mapLevel.equals("8")) {//7-8级  30万
            collection = MongoDBConnection.getConnect2().getCollection("fwQuantity_30");
        } else if (mapLevel.equals("9") || mapLevel.equals("10")) {//9-10级   50万
            collection = MongoDBConnection.getConnect2().getCollection("fwQuantity_50");
        } else if (mapLevel.equals("11") || mapLevel.equals("12")) {//11-12级   150万
            collection = MongoDBConnection.getConnect2().getCollection("fwQuantity_150");
        } else if (mapLevel.equals("13") || mapLevel.equals("14") || mapLevel.equals("15")) {//13-15级    300万
            collection = MongoDBConnection.getConnect2().getCollection("fwQuantity_300");
        } else if (mapLevel.equals("16") || mapLevel.equals("17") || mapLevel.equals("18") || mapLevel.equals("19") || mapLevel.equals("20")) {//16-17级     1000万
            collection = MongoDBConnection.getConnect2().getCollection("fwQuantity");
        }
       /* else if (mapLevel.equals("18") || mapLevel.equals("19") || mapLevel.equals("20")) {//18-20级    全部
            collection = MongoDBConnection.getConnect2().getCollection("fwQuantity");
        }*/

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


        BasicDBObject group = new BasicDBObject("$group", new BasicDBObject("_id", "$type")
            .append("totalNum", new BasicDBObject("$sum","$num")));
//        groupFields = new BasicDBObject("_id", "$type");
//        groupFields.put("totalNum", new BasicDBObject("$sum", "$num"));
//        DBObject group = new BasicDBObject("$group", groupFields);
        optionList.add(group);

        Iterator<Document> iterator = null;
        Map<String, Object> popBarMap = new HashMap<String, Object>();
//        try {
             iterator = collection.aggregate(optionList).iterator();
//        }catch (MongoSocketReadException e){
//
//        }
        while (iterator.hasNext()){
            Document document = iterator.next();
            String fwtype = document.getString("_id");
            long totalNum = Double.valueOf(document.get("totalNum").toString()).longValue();
            if(StringUtils.isEmpty(fwtype)){
                continue;
            }
            if (fwtype.equals("1")) {//总房屋
                popBarMap.put("totalFw", totalNum);
            } else if (fwtype.equals("2")) {//自住
                popBarMap.put("zzfw", totalNum);
            } else if (fwtype.equals("3")) {//出租
                popBarMap.put("czfw", totalNum);
            } else if (fwtype.equals("4")) {//空置
                popBarMap.put("kzfw", totalNum);
            }
        }
//        try {
//            ((MongoCursor<Document>) iterator).close();
//        } catch (Exception e){
//            log.info("CharDataDao.getFwChartData.line 237 游标关闭异常");
//        }
        return popBarMap;
    }

    /* 获取一定范围内右侧的重点人口饼图数据
     * @param minHzb最小横坐标
     * @param maxHzb最大横坐标
     * @param minZzb最小纵坐标
     * @param maxZzb最大纵坐标
     * */
    public Map<String, Object> getPopPieData(double minHzb, double maxHzb, double minZzb, double maxZzb) {
        MongoCollection<Document> collection = MongoDBConnection.getConnect2().getCollection("popThirTypeQuantity");
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

        BasicDBObject groupFields = new BasicDBObject().append("$group", new BasicDBObject("_id", "$type")
            .append("totalPopNum", new BasicDBObject("$sum", "$num")));
        optionList.add(groupFields);
//        groupFields = new BasicDBObject("_id",  "$type"));
//        groupFields.put("totalPopNum", new BasicDBObject("$sum", "$num"));
//        DBObject group = new BasicDBObject("$group", groupFields);
//        optionList.add(group);

        Iterator<Document> iterator =  collection.aggregate(optionList).iterator();
        Map<String, Object> map = new HashMap<>();
        while (iterator.hasNext()){
            Document document = iterator.next();
            String type = document.getString("_id");
            long totalPopNum = Long.valueOf(document.get("totalPopNum").toString());
            if(StringUtils.isEmpty(type)){
                continue;
            }
            if (type.equals("304000000000")) {//网安
                map.put("wangan", totalPopNum);
            } else if ("405000000000".equals(type)) {//经侦
                map.put("jingzhen", totalPopNum);
            } else if ("203000000000".equals(type)) {//刑警
                map.put("xingjing", totalPopNum);
            } else if ("102000000000".equals(type)) {//户政
                map.put("huzhen", totalPopNum);
            } else if ("501000000000".equals(type)) {//禁毒
                map.put("jindu", totalPopNum);
            } else if ("001000000000".equals(type)) {//情报
                map.put("qingbao", totalPopNum);
            } else if ("601000000000".equals(type)) {//国保
                map.put("guobao", totalPopNum);
            } else if ("701000000000".equals(type)) {//反邪教
                map.put("fanxiejiao", totalPopNum);
            } else if ("801000000000".equals(type)) {//反恐
                map.put("fankong", totalPopNum);
            } else if ("901000000000".equals(type)) {//交警
                map.put("jiaojing", totalPopNum);
            } else if ("120800000000".equals(type)) {//泽雨
                map.put("zeyu", totalPopNum);
            } else if ("051101050200".equals(type)) {//盗窃
                map.put("daoqie", totalPopNum);
            } else if ("051502020205".equals(type)) {//破坏燃爆设备
                map.put("pohuairanbaoshebei", totalPopNum);
            } else if ("050102050100".equals(type)) {//抢劫案
                map.put("qiangjie", totalPopNum);
            } else if ("051601040103".equals(type)) {//故意伤害案
                map.put("guyishanghai", totalPopNum);
            } else if ("040100000000".equals(type)) {//吸毒人员
                map.put("xidurenyuan", totalPopNum);
            } else if ("030000000000".equals(type)) {//在逃人员
                map.put("zaitaorenyuan", totalPopNum);
            } else if ("040200000000".equals(type)) {//制贩毒人员
                map.put("fandu", totalPopNum);
            } else if ("051601040109".equals(type)) {//非法拘禁
                map.put("feifajujin", totalPopNum);
            } else if ("050103040105".equals(type)) {//强奸
                map.put("qiangjian", totalPopNum);
            } /*else if (type.equals("051104050800")) {//敲诈勒索
                map.put("qiaozhalesuo", totalPopNum);
            } else if (type.equals("051102050300")) {//诈骗案
                map.put("zhapian", totalPopNum);
            } else if (type.equals("051502020203")) {//破坏电力设备
                map.put("pohuaidianlishebei", totalPopNum);
            } else if (type.equals("050104040110")) {//绑架
                map.put("bangjia", totalPopNum);
            }*/ else if ("040100000000".equals(type)) {//吸毒
                map.put("xidu", totalPopNum);
            } /*else if (type.equals("020502000100")) {//天津私募股权基金类
                map.put("tianjinsimuguquan", totalPopNum);
            } else if (type.equals("051201060136")) {//聚众斗殴案
                map.put("juzhongdouou", totalPopNum);
            }*/
        }
//        try {
//            ((MongoCursor<Document>) iterator).close();
//        } catch (Exception e){
//            log.info("CharDataDao.getPopPieData.line 341 游标关闭异常");
//        }
        return map;
    }

    /* 获取一定范围内右侧的单位饼图数据
     * @param type   1：特种单位    2：保护单位
     * @param minHzb最小横坐标
     * @param maxHzb最大横坐标
     * @param minZzb最小纵坐标
     * @param maxZzb最大纵坐标
     * */
    public Map<String, Object> getDwPieData(String type, double minHzb, double maxHzb, double minZzb, double maxZzb) {
        log.info("进入方法：getDwPieData，type: " + type + ", xmin: " + minHzb + ", xmax: " + maxHzb
                + ", ymin: " + minZzb + ", ymax: " + maxZzb);
        MongoCollection<Document> collection = MongoDBConnection.getConnect2().getCollection("dwThirTypeQuantity");
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

        BasicDBObject queryfirType = new BasicDBObject("type", type);
        BasicDBObject matchFirtype = new BasicDBObject("$match", queryfirType);
        optionList.add(matchFirtype);

        BasicDBObject groupFields = new BasicDBObject().append("$group", new BasicDBObject()
                .append("_id", "$sectype").append("totalDwNum", new BasicDBObject("$sum", "$num")));
//        groupFields = new BasicDBObject("_id", "$sectype");
//        groupFields.put("totalDwNum", new BasicDBObject("$sum", "$num"));
//        DBObject group = new BasicDBObject("$group", groupFields);
        optionList.add(groupFields);

        Iterator<Document> iterator = collection.aggregate(optionList).iterator();
        Map<String, Object> map = new HashMap<String, Object>();
        while (iterator.hasNext()) {
            Document document = iterator.next();
            String firtype = document.getString("_id");
            long totalDwNum = Long.valueOf(document.get("totalDwNum").toString());
            if (StringUtils.isEmpty(firtype)) {
                continue;
            }
            if (type.equals("1")) {//特种单位
               /* if (firtype.equals("240")) {//娱乐服务
                    map.put("yule", totalDwNum);
                } else if (firtype.equals("216")) {//旧货
                    map.put("jiuhuo", totalDwNum);
                } else*/
                if (firtype.equals("219")) {//汽车租赁
                    map.put("qichezulin", totalDwNum);
                } else if (firtype.equals("217")) {//金银加工
                    map.put("jinyinjiagong", totalDwNum);
                } else if (firtype.equals("220")) {//印刷
                    map.put("yinshua", totalDwNum);
                } else if (firtype.equals("211")) {//旅馆
                    map.put("lvguan", totalDwNum);
                } else if (firtype.equals("212")) {//典当
                    map.put("diandang", totalDwNum);
                } else if (firtype.equals("213")) {//公章
                    map.put("gongzhang", totalDwNum);
                } else if (firtype.equals("215")) {//开锁
                    map.put("kaisuo", totalDwNum);
                } else if (firtype.equals("214")) {//废旧金属收购
                    map.put("jiujinshushougou", totalDwNum);
                } else if (firtype.equals("218")) {//机动车拆装
                    map.put("chaizhuang", totalDwNum);
                }/* else if (firtype.equals("221")) {//机动车修理
                    map.put("xiuli", totalDwNum);
                } */ else if (firtype.equals("280")) {//上网场所
                    map.put("shangwang", totalDwNum);
                } /*else if (firtype.equals("291")) {//保安
                    map.put("baoan", totalDwNum);
                } */ else if (firtype.equals("292")) {//管制工具
                    map.put("guanzhidaoju", totalDwNum);
                } else if (firtype.equals("230")) {//危爆行业
                    map.put("weibao", totalDwNum);
                }
            } else if (type.equals("2")) {//保护单位
                if (firtype.equals("251")) {//新闻
                    map.put("xinwen", totalDwNum);
                } else if (firtype.equals("259")) {//学校（教育单位）
                    map.put("jiaoyu", totalDwNum);
                } else if (firtype.equals("269")) {//交通枢纽
                    map.put("jiaotongshuniu", totalDwNum);
                } else if (firtype.equals("268")) {//加油站
                    map.put("jiayouzhan", totalDwNum);
                } else if (firtype.equals("252")) {//国防科研
                    map.put("keyan", totalDwNum);
                } else if (firtype.equals("271")) {//党政机关
                    map.put("dangzhenjiguan", totalDwNum);
                } else if (firtype.equals("253")) {//电信
                    map.put("dianxin", totalDwNum);
                } else if (firtype.equals("254")) {//物流
                    map.put("wuliu", totalDwNum);
                } else if (firtype.equals("255")) {//银行
                    map.put("yinhang", totalDwNum);
                } else if (firtype.equals("256")) {//能源
                    map.put("nengyuan", totalDwNum);
                } else if (firtype.equals("257")) {//物资储备
                    map.put("wuzichubei", totalDwNum);
                }

            }
        }
//        try {
//            ((MongoCursor<Document>) iterator).close();
//        } catch (Exception e){
//            log.info("CharDataDao.getPopPieData.line 341 游标关闭异常");
//        }
        return map;
    }

    /*查询RecordsCount表中的实有人口、实有房屋、实有单位总记录数*/
    public List queryRecordsCount() {
        MongoCollection<Document> collection = MongoDBConnection.getConnect2().getCollection("recordsCount");
        FindIterable<Document> findIterable = collection.find();
        MongoCursor cursor = findIterable.iterator();
        List list = new ArrayList<>();
        while (cursor.hasNext()) {
            list.add(cursor.next());
        }
        return list;
    }
}
