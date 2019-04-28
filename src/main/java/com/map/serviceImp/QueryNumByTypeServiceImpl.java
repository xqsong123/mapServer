package com.map.serviceImp;

import com.map.repository.QuantityDao;
import com.mongodb.DBObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryNumByTypeServiceImpl {
    private Log log = LogFactory.getLog(QueryNumByTypeServiceImpl.class);


    /* 获取总人口/常口/流口/重点/境外等人口类型的数量
     * @param  type  1:总人口    2:常口   3:流口  4：重点    5：境外
     * @param minHzb 最小横坐标
     * @param maxHzb 最大横坐标
     * @param minZzb 最小纵坐标
     * @param maxZzb 最大纵坐标
     * */
    public List<DBObject> getPopfirPoints(String type, double minHzb, double maxHzb, double minZzb, double maxZzb) {
        QuantityDao quantityDao = new QuantityDao();
        List<Document> poplist = quantityDao.queryPopfirPoints(type, minHzb, maxHzb, minZzb, maxZzb);
        List resLst = new ArrayList();
        for (int i = 0; i < poplist.size(); i++) {
            Map<String, Object> oneMap = (Map<String, Object>) poplist.get(i);
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("x", oneMap.get("x"));
            map.put("y", oneMap.get("y"));
            map.put("num", oneMap.get("num"));
            //map.put("type", oneMap.get("type"));
            map.put("jzwbm", oneMap.get("jzwbm"));
            resLst.add(map);
        }
        return resLst;
    }

    /* 获取总房屋/自住/出租/空置等房屋类型的数量
     * @param  type   类型编码
     * @param minHzb 最小横坐标
     * @param maxHzb 最大横坐标
     * @param minZzb 最小纵坐标
     * @param maxZzb 最大纵坐标
     * */
    public List<DBObject> getFwPoints(String type, double minHzb, double maxHzb, double minZzb, double maxZzb) {
        QuantityDao quantityDao = new QuantityDao();
        List<Document> fwlist = quantityDao.queryFwPoints(type, minHzb, maxHzb, minZzb, maxZzb);
        log.info(fwlist);
        List resLst = new ArrayList();
        for (int i = 0; i < fwlist.size(); i++) {
            Map<String, Object> oneMap = (Map<String, Object>) fwlist.get(i);
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("x", oneMap.get("x"));
            map.put("y", oneMap.get("y"));
            // map.put("type", oneMap.get("type"));
            map.put("jzwbm", oneMap.get("jzwbm"));
            map.put("num", oneMap.get("num"));
            resLst.add(map);
        }
        return resLst;
    }

    /* 获取总单位/普通单位/特种单位/保护单位/九小场所等单位类型的数量
     * @param  type    类型编码
     * @param minHzb 最小横坐标
     * @param maxHzb 最大横坐标
     * @param minZzb 最小纵坐标
     * @param maxZzb 最大纵坐标
     * */
    public List<DBObject> getDwfirPoints(String type, double minHzb, double maxHzb, double minZzb, double maxZzb) {
        QuantityDao quantityDao = new QuantityDao();
        List<Document> dwlist = quantityDao.queryDwfirPoints(type, minHzb, maxHzb, minZzb, maxZzb);
        log.info(dwlist);
        List resLst = new ArrayList();
        for (int i = 0; i < dwlist.size(); i++) {
            Map<String, Object> oneMap = (Map<String, Object>) dwlist.get(i);
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("y", oneMap.get("y"));
            map.put("x", oneMap.get("x"));
            map.put("num", oneMap.get("num"));
            //map.put("type", oneMap.get("type"));
            map.put("jzwbm", oneMap.get("jzwbm"));
            resLst.add(map);
        }
        return resLst;
    }

    /* 获取网安、经侦、刑警、户政、禁毒、情报、国保、反邪教、反恐、交警、泽雨、网安A级、网安B级、网安C级、假币、传销、贿赂、涉稳等重点人口类型的数量
     * @param  type    类型编码
     * @param minHzb 最小横坐标
     * @param maxHzb 最大横坐标
     * @param minZzb 最小纵坐标
     * @param maxZzb 最大纵坐标
     * */
    public List<DBObject> getPopsecPoints(String type, double minHzb, double maxHzb, double minZzb, double maxZzb) {
        QuantityDao quantityDao = new QuantityDao();
        List<Document> popseclist = quantityDao.queryPopsecPoints(type, minHzb, maxHzb, minZzb, maxZzb);
        log.info(popseclist);
        List resLst = new ArrayList();
        for (int i = 0; i < popseclist.size(); i++) {
            Map<String, Object> oneMap = (Map<String, Object>) popseclist.get(i);
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("jzwbm", oneMap.get("jzwbm"));
            map.put("x", oneMap.get("x"));
            map.put("y", oneMap.get("y"));
            map.put("type", oneMap.get("type"));
            map.put("num", oneMap.get("num"));
            resLst.add(map);
        }
        return resLst;
    }

    /* 获取娱乐服务、旧货、汽车租赁、金银加工、印刷、旅馆、典当、公章、开锁、废旧金属收购、机动车拆装、机动车修理、新闻、学校（教育单位）、交通枢纽、加油站
       等重点人口类型的数量
     * @param  firtype     1:重点单位    2：保护单位
     *  @param  sectype
     *  @param minHzb 最小横坐标
     * @param maxHzb 最大横坐标
     * @param minZzb 最小纵坐标
     * @param maxZzb 最大纵坐标
     * */
    public List<DBObject> getDwsecPoints(String firtype, String sectype, double minHzb, double maxHzb, double minZzb, double maxZzb) {
        QuantityDao quantityDao = new QuantityDao();
        List<Document> dwseclist = quantityDao.queryDwsecPoints(firtype, sectype, minHzb, maxHzb, minZzb, maxZzb);
        log.info(dwseclist);
        List resLst = new ArrayList();
        for (int i = 0; i < dwseclist.size(); i++) {
            Map<String, Object> oneMap = (Map<String, Object>) dwseclist.get(i);

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("jzwbm", oneMap.get("jzwbm"));
            map.put("x", oneMap.get("x"));
            map.put("y", oneMap.get("y"));
            map.put("num", oneMap.get("num"));
            map.put("type", oneMap.get("type"));
            map.put("secType", oneMap.get("secType"));

            resLst.add(map);
        }
        return resLst;
    }

    /* 根据类型获取点集合
     * @param firtype
     * @param sectype
     * @param thirtype
     * */
  /*  public List QueryPointsByType(String firtype, String sectype, String thirtype,double minHzb, double maxHzb, double minZzb, double maxZzb) {
        QuantityDao quantityDao = new QuantityDao();
        List<DBObject> list = quantityDao.queryPointsByType(firtype, sectype, thirtype,minHzb,maxHzb,minZzb,maxZzb);
        List ResLst = new ArrayList();
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> map = (Map<String, Object>) list.get(i);
            String jzwbm = (String) map.get("jzwbm");
            String firtype1 = (String) map.get("firtype");
            String sectype1 = (String) map.get("sectype");
            String thirtype1 = (String) map.get("thirtype");
            long firNum = (Long) map.get("firNum");
            long secNum = (Long) map.get("secNum");
            long thrNum = (Long) map.get("thrNum");
            double x = (double) map.get("x");
            double y = (double) map.get("y");
            Map<String, Object> map1 = new HashMap<String, Object>();
            map1.put("x", x);
            map1.put("y", y);
            map1.put("firNum", firNum);
            map1.put("secNum", secNum);
            map1.put("thrNum", thrNum);
            map1.put("jzwbm", jzwbm);
            ResLst.add(map1);
        }
        return ResLst;
    }*/
}
