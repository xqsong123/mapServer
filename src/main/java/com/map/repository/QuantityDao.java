package com.map.repository;

import com.map.utils.Constants;
import com.map.utils.MongoDBConnection;
import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.Document;

import java.util.*;

public class QuantityDao {
    private Log log = LogFactory.getLog(QuantityDao.class);

    /* 获取总人口/常口/流口/重点/境外等人口类型的点集合
     * @param  type  类型编码
     * @param minHzb 最小横坐标
     * @param maxHzb 最大横坐标
     * @param minZzb 最小纵坐标
     * @param maxZzb 最大纵坐标
     * */
    public List<Document> queryPopfirPoints(String type, double minHzb, double maxHzb, double minZzb, double maxZzb) {
        if("4".equals(type)){
            MongoCollection collection = MongoDBConnection.getConnect2().getCollection("popThirTypeQuantity");
            BasicDBObject query = new BasicDBObject("x", new BasicDBObject("$gte",minHzb).append("$lte", maxHzb))
                    .append("y", new BasicDBObject("$gte",minZzb).append("$lte", maxZzb))
                    .append("type", new BasicDBObject("$in", Constants.ZDRYLB));
            BasicDBObject project = new BasicDBObject("_id",0).append("jzwbm", 1)
                    .append("type", 1)
                    .append("num", 1)
                    .append("x", 1)
                    .append("y", 1);
            MongoCursor<Document> iterator = collection.find(query).projection(project).iterator();
            List<Document> poplist = new LinkedList<>();
            while (iterator.hasNext()){
                poplist.add(iterator.next());
            }
            return poplist;
        }

        MongoCollection collection = MongoDBConnection.getConnect2().getCollection("popQuantity");
        BasicDBObject query = new BasicDBObject();//条件
        query.put("x", new BasicDBObject("$gte", minHzb).append("$lte", maxHzb));
        query.put("y", new BasicDBObject("$gte", minZzb).append("$lte", maxZzb));
        if (type != null && type != "") {
            query.put("type", type);
        }
        BasicDBObject key = new BasicDBObject();//指定需要显示列
        key.put("_id", 0);
        key.put("jzwbm", 1);
        key.put("type", 1);
        key.put("num", 1);
        key.put("x", 1);
        key.put("y", 1);
        MongoCursor<Document> iterator = collection.find(query).projection(key).iterator();
        //把结果集输出成list类型

        List<Document> poplist = new LinkedList<>();
        while (iterator.hasNext()){
            poplist.add(iterator.next());
        }
        //System.out.println("size--------:" + poplist.size());
        return poplist;
    }

    /* 获取总房屋/自住/出租/空置等房屋类型的点集合
     * @param  type   类型编码
     * @param minHzb 最小横坐标
     * @param maxHzb 最大横坐标
     * @param minZzb 最小纵坐标
     * @param maxZzb 最大纵坐标
     * */
    public List<Document> queryFwPoints(String type, double minHzb, double maxHzb, double minZzb, double maxZzb) {
        MongoCollection<Document> collection = MongoDBConnection.getConnect2().getCollection("fwQuantity");
        BasicDBObject query = new BasicDBObject();//条件
        query.put("x", new BasicDBObject("$gte", minHzb).append("$lte", maxHzb));
        query.put("y", new BasicDBObject("$gte", minZzb).append("$lte", maxZzb));
        if (type != null && type != "") {
            query.put("type", type);
        }
        BasicDBObject key = new BasicDBObject();//指定需要显示列
        key.put("_id", 0);
        key.put("jzwbm", 1);
        key.put("type", 1);
        key.put("num", 1);
        key.put("x", 1);
        key.put("y", 1);
        MongoCursor<Document> iterator = collection.find(query).projection(key).iterator();
        //把结果集输出成list类型

        List<Document> fwList = new LinkedList<>();
        while (iterator.hasNext()){
            fwList.add(iterator.next());
        }
        //System.out.println("size--------:" + fwList.size());
        return fwList;
    }

    /* 获取总单位/普通单位/特种单位/保护单位/九小场所等单位类型的点集合
     * @param  type    类型编码
     * @param minHzb 最小横坐标
     * @param maxHzb 最大横坐标
     * @param minZzb 最小纵坐标
     * @param maxZzb 最大纵坐标
     * */
    public List<Document> queryDwfirPoints(String type, double minHzb, double maxHzb, double minZzb, double maxZzb) {
        MongoCollection<Document> collection = MongoDBConnection.getConnect2().getCollection("dwQuantity");
        BasicDBObject query = new BasicDBObject();//条件
        query.put("x", new BasicDBObject("$gte", minHzb).append("$lte", maxHzb));
        query.put("y", new BasicDBObject("$gte", minZzb).append("$lte", maxZzb));
        if (type != null && type != "") {
            query.put("type", type);
        }
        BasicDBObject key = new BasicDBObject();//指定需要显示列
        key.put("_id", 0);
        key.put("jzwbm", 1);
        key.put("type", 1);
        key.put("num", 1);
        key.put("x", 1);
        key.put("y", 1);
        MongoCursor<Document> iterator = collection.find(query).projection(key).iterator();
        //把结果集输出成list类型
        List<Document> dwList = new LinkedList<>();
        while (iterator.hasNext()){
            dwList.add(iterator.next());
        }
        //System.out.println("size--------:" + dwList.size());
        return dwList;
    }

    /* 获取网安、经侦、刑警、户政、禁毒、情报、国保、反邪教、反恐、交警、泽雨、网安A级、网安B级、网安C级、假币、传销、贿赂、涉稳等重点人口类型的点集合
     * @param  type    类型编码
     * @param minHzb 最小横坐标
     * @param maxHzb 最大横坐标
     * @param minZzb 最小纵坐标
     * @param maxZzb 最大纵坐标
     * */
    public List<Document> queryPopsecPoints(String type, double minHzb, double maxHzb, double minZzb, double maxZzb) {
        MongoCollection<Document> collection = MongoDBConnection.getConnect2().getCollection("popThirTypeQuantity");
        BasicDBObject query = new BasicDBObject();//条件
        query.put("x", new BasicDBObject("$gte", minHzb).append("$lte", maxHzb));
        query.put("y", new BasicDBObject("$gte", minZzb).append("$lte", maxZzb));
        if (type != null && type != "") {
            query.put("type", type);
        }
      /*  if(types != null && types.size() != 0){
            query.put("type", new BasicDBObject("$in", types));
        }*/
        BasicDBObject key = new BasicDBObject();//指定需要显示列
        key.put("_id", 0);
        key.put("jzwbm", 1);
        key.put("type", 1);
        key.put("num", 1);
        key.put("x", 1);
        key.put("y", 1);
        MongoCursor<Document> iterator = collection.find(query).projection(key).iterator();
        //把结果集输出成list类型
        List<Document> popsList = new LinkedList<>();
        while (iterator.hasNext()){
            popsList.add(iterator.next());
        }
        //System.out.println("size--------:" + popsList.size());
        return popsList;
    }

    /* 获取娱乐服务、旧货、汽车租赁、金银加工、印刷、旅馆、典当、公章、开锁、废旧金属收购、机动车拆装、机动车修理、新闻、学校（教育单位）、交通枢纽、加油站
       等重点人口类型的点集合
     * @param  firtype     1:重点单位    2：保护单位
     *  @param  sectype
     *  @param minHzb 最小横坐标
     * @param maxHzb 最大横坐标
     * @param minZzb 最小纵坐标
     * @param maxZzb 最大纵坐标
     * */
    public List<Document> queryDwsecPoints(String firtype, String sectype, double minHzb, double maxHzb, double minZzb, double maxZzb) {
        MongoCollection<Document> collection = MongoDBConnection.getConnect2().getCollection("dwThirTypeQuantity");
        BasicDBObject query = new BasicDBObject();//条件
        query.put("x", new BasicDBObject("$gte", minHzb).append("$lte", maxHzb));
        query.put("y", new BasicDBObject("$gte", minZzb).append("$lte", maxZzb));
        if (firtype != null && firtype != "") {
            query.put("type", firtype);
        }
        if (sectype != null && sectype != "") {
            query.put("sectype", sectype);
        }
        BasicDBObject key = new BasicDBObject();//指定需要显示列
        key.put("_id", 0);
        key.put("jzwbm", 1);
        key.put("type", 1);
        key.put("sectype", 1);
        key.put("num", 1);
        key.put("x", 1);
        key.put("y", 1);
        MongoCursor<Document> iterator = collection.find(query).projection(key).iterator();
        //把结果集输出成list类型
        List<Document> popsList = new LinkedList<>();
        while (iterator.hasNext()){
            popsList.add(iterator.next());
        }
        //System.out.println("size--------:" + popsList.size());
        return popsList;
    }


    /* 点击饼图获取更细小一级的重点人口各种类型数量在当前屏幕中的数量
     * typeLst 小类的类型编码List
     * @param minHzb最小横坐标
     * @param maxHzb最大横坐标
     * @param minZzb最小纵坐标
     * @param maxZzb最大纵坐标
     * */
    public Map<String, Object> queryPopTypesNum(List typeLst, double minHzb, double maxHzb, double minZzb, double maxZzb) {
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

        if (typeLst != null && typeLst.size() != 0) {
            BasicDBList values = new BasicDBList();
            for (int i = 0; i < typeLst.size(); i++) {
                values.add(typeLst.get(i));
            }
            BasicDBObject matchTypes = new BasicDBObject("type", new BasicDBObject("$in", values));
            BasicDBObject query = new BasicDBObject("$match", matchTypes);
            optionList.add(query);
        }

        BasicDBObject group = new BasicDBObject("$group",new BasicDBObject("_id", "$type")
                .append("zdPopNum", new BasicDBObject("$sum", "$num")));
//        groupFields = new BasicDBObject("_id", new Document("type", "$type"));
//        groupFields.put("zdPopNum", new BasicDBObject("$sum", "$num"));
//        DBObject group = new BasicDBObject("$group", groupFields);
        optionList.add(group);

        MongoCursor<Document> iterator = collection.aggregate(optionList).iterator();
        Map<String, Object> map = new HashMap<String, Object>();
        while (iterator.hasNext()){
            Document document = iterator.next();
            String type = document.getString("_id");
            long zdPopNum = Long.valueOf(document.get("zdPopNum").toString());
            if (StringUtils.isEmpty(type)){
                continue;
            }
            if (type.equals("340100000000")) {//网安A级
                map.put("wanganA", zdPopNum);
            } else if (type.equals("340200000000")) {//网安B级
                map.put("wanganB", zdPopNum);
            } else if (type.equals("340300000000")) {//网安C级
                map.put("wanganC", zdPopNum);
            } else if (type.equals("450100000000")) {//假币
                map.put("jiabi", zdPopNum);
            } else if (type.equals("450200000000")) {//传销
                map.put("chuanxiao", zdPopNum);
            } else if (type.equals("450300000000")) {//贿赂
                map.put("huilu", zdPopNum);
            } else if (type.equals("450800000000")) {//涉稳
                map.put("shewen", zdPopNum);
            } else if (type.equals("450500000000")) {//集资
                map.put("jizi", zdPopNum);
            } else if (type.equals("450600000000")) {//洗钱
                map.put("xiqian", zdPopNum);
            } else if (type.equals("450700000000")) {//知识产权
                map.put("zhishichanquan", zdPopNum);
            } else if (type.equals("450400000000")) {//银行卡
                map.put("yinhangka", zdPopNum);
            } else if (type.equals("230300000000")) {//重点场所涉案
                map.put("zhongdianchangsuoshean", zdPopNum);
            } else if (type.equals("230200000000")) {//黑恶犯罪涉案
                map.put("heiefanzuishean", zdPopNum);
            } else if (type.equals("230100000000")) {//涉黑打击处理
                map.put("sheheidajichuli", zdPopNum);
            } else if (type.equals("120300000000")) {//潜在风险
                map.put("qianzaifengxian", zdPopNum);
            } else if (type.equals("120400000000")) {//刑释未满五年
                map.put("xingshiweimanwunian", zdPopNum);
            } else if (type.equals("120500000000")) {//吸毒
                map.put("xidu", zdPopNum);
            } else if (type.equals("120100000000")) {//危害国家安全
                map.put("weihaiguojiaanquan", zdPopNum);
            } else if (type.equals("120700000000")) {//流动重点人员
                map.put("liudongzhongdianrenyuan", zdPopNum);
            } else if (type.equals("120200000000")) {//刑事犯罪嫌疑
                map.put("xingshifanzuixianyi", zdPopNum);
            } else if (type.equals("120600000000")) {//其他刑事人员
                map.put("qitaxingshirenyuan", zdPopNum);
            } else if (type.equals("510100000000")) {//目标案件嫌疑
                map.put("mubiaoanjianxianyi", zdPopNum);
            } else if (type.equals("510200000000")) {//一般案件嫌疑
                map.put("yibananjianxianyi", zdPopNum);
            } else if (type.equals("510300000000")) {//其他嫌疑人员
                map.put("qitaxianyi", zdPopNum);
            } else if (type.equals("080000000000")) {//管控对象
                map.put("guankongduixiang", zdPopNum);
            } else if (type.equals("090000000000")) {//关注对象
                map.put("guanzhuduixiang", zdPopNum);
            } else if (type.equals("011000000000")) {//知悉对象
                map.put("zhixiduixiang", zdPopNum);
            } else if (type.equals("050000000000")) {//前科人员
                map.put("qiankerenyuan", zdPopNum);
            } else if (type.equals("999999999999")) {//其他重点人员
                map.put("qitazhongdianrenyuan", zdPopNum);
            } else if (type.equals("040000000000")) {//涉毒人员
                map.put("shedurenyuan", zdPopNum);
            } else if (type.equals("070000000000")) {//重点上访人员
                map.put("zhongdianshangfangrenyuan", zdPopNum);
            } else if (type.equals("010000000000")) {//涉恐人员
                map.put("shekongrenyuan", zdPopNum);
            } else if (type.equals("020000000000")) {//涉稳人员
                map.put("shewenrenyuan", zdPopNum);
            } else if (type.equals("030000000000")) {//在逃人员
                map.put("zaitaorenyuan", zdPopNum);
            } else if (type.equals("060000000000")) {//精神病人
                map.put("jingshenbingren", zdPopNum);
            } else if (type.equals("710300000000")) {//有害气功组织
                map.put("youhaiqigongzuzhi", zdPopNum);
            } else if (type.equals("710200000000")) {//法轮功人员
                map.put("falungongrenyuan", zdPopNum);
            } else if (type.equals("710100000000")) {//邪教组织人员
                map.put("xiejiaozuzhirenyuan", zdPopNum);
            } else if (type.equals("810100000000")) {//列控人
                map.put("liekongren", zdPopNum);
            } else if (type.equals("810200000000")) {//重点人
                map.put("zhongdianren", zdPopNum);
            } else if (type.equals("810500000000")) {//准重点人
                map.put("zhunzhongdianren", zdPopNum);
            } else if (type.equals("810400000000")) {//基础群体
                map.put("jichuqunti", zdPopNum);
            } else if (type.equals("910200000000")) {//关注人员
                map.put("guanzhurenyuan", zdPopNum);
            } else if (type.equals("910100000000")) {//管控人员
                map.put("guankongrenyuan", zdPopNum);
            } else if (type.equals("120900000001")) {//持卡人
                map.put("chikaren", zdPopNum);
            } else if (type.equals("121200000001")) {//投资人
                map.put("touziren", zdPopNum);
            } else if (type.equals("121300000001")) {//关系人
                map.put("guanxiren", zdPopNum);
            } else if (type.equals("121100000001")) {//员工
                map.put("yuangong", zdPopNum);
            } else if (type.equals("121000000002")) {//供货人
                map.put("gonghuoren", zdPopNum);
            }else if (type.equals("121400000001")) {//其它
                map.put("qita", zdPopNum);
            }
        }

        return map;
    }

    /* 点击饼图获取更细小一级的特种单位或者保护单位中各种类型数量在当前屏幕中的数量
     * flag 标识   1:特种单位    2：保护单位
     * typeLst 小类的类型编码List
     * @param minHzb最小横坐标
     * @param maxHzb最大横坐标
     * @param minZzb最小纵坐标
     * @param maxZzb最大纵坐标
     * */
    /*public Map<String, Object> queryDwTypesNum(String flag,List typeLst, double minHzb, double maxHzb, double minZzb, double maxZzb) {
        DBCollection collection = MongoDBConnection.getConnect2().getCollection("dwThirTypeQuantity");
        List<DBObject> optionList = new ArrayList<>();
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

        BasicDBObject query = new BasicDBObject("type", flag);
        BasicDBObject matchDw = new BasicDBObject("$match", query);
        optionList.add(matchDw);

        if (typeLst != null && typeLst.size() != 0) {
            BasicDBList values = new BasicDBList();
            for (int i = 0; i < typeLst.size(); i++) {
                values.add(typeLst.get(i));
            }
            BasicDBObject matchTypes = new BasicDBObject("$in", values);
            BasicDBObject querySecType = new BasicDBObject("secType", matchTypes);
            optionList.add(querySecType);
        }

        DBObject groupFields = new BasicDBObject();
        groupFields = new BasicDBObject("_id", new Document("secType", "$secType"));
        groupFields.put("dwNum", new BasicDBObject("$sum", "$num"));
        DBObject group = new BasicDBObject("$group", groupFields);
        optionList.add(group);

        List<DBObject> popTypesLst = (List<DBObject>) collection.aggregate(optionList).results();
        Map<String, Object> map = new HashMap<String, Object>();
        for (int i = 0; i < popTypesLst.size(); i++) {
            Map<Object, Object> objectMap = (Map<Object, Object>) popTypesLst.get(i).get("_id");
            String secType = objectMap.get("secType").toString();
            long dwNum = Long.valueOf(popTypesLst.get(i).get("dwNum").toString());
            map.put(secType,dwNum);
        }
        return map;
    }*/


}
