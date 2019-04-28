package com.map.serviceImp;

import com.map.repository.CaseDao;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.Document;

import java.text.SimpleDateFormat;
import java.util.*;

public class CaseServiceImpl {
    private Log log = LogFactory.getLog(CaseServiceImpl.class);

    /* 获取一定范围内查询的案件
     * @param minHzb最小横坐标
     * @param maxHzb最大横坐标
     * @param minZzb最小纵坐标
     * @param maxZzb最大纵坐标
     * @param beginTime开始时间
     * @param endTime结束时间
     * @param caseType案件类型编码
     * */
    public List<Map<String, Object>> getDynamicCases(double minHzb, double maxHzb, double minZzb, double maxZzb, Date beginTime, Date endTime, String caseType) {
        CaseDao caseDao = new CaseDao();
        List<Map<String, Object>> caseLst = new ArrayList<>();
        //0:全部案件、212:刑事侵财、101:治安管理、101030078:盗窃、101030068:殴打他人
        if ("0".equals(caseType)) {
            caseLst = caseDao.queryAllCases(minHzb, maxHzb, minZzb, maxZzb, beginTime, endTime);
        } else if ("212".equals(caseType) || "101".equals(caseType) || "101030078".equals(caseType) || "101030068".equals(caseType)) {
            caseLst = caseDao.queryFirTypeCases(minHzb, maxHzb, minZzb, maxZzb, beginTime, endTime, caseType);
        }
        return caseLst;
        //log.info("CaseServiceImpl getAllCases:" + caseLst);
    /*    List<double[]> resLst = new ArrayList<double[]>();
        for (int i = 0; i < caseLst.size(); i++) {
            Map map = (Map) caseLst.get(i);
            double x = 0;//经度
            double y = 0;//纬度
            if (map.get("zbx") != null) {
                x = Double.parseDouble(map.get("zbx").toString());
            }
            if (map.get("zby") != null) {
                y = Double.parseDouble(map.get("zby").toString());
            }
            double[] xy = new double[]{x, y};
            resLst.add(xy);
        }*/

    }

    /* 获取一定经纬度范围和一段时间内的各种类型案件点位图
     * @param minHzb最小横坐标
     * @param maxHzb最大横坐标
     * @param minZzb最小纵坐标
     * @param maxZzb最大纵坐标
     * @param beginTime开始时间
     * @param endTime结束时间
     * @param caseFirType一级案件类型编码
     * @param caseSecType二级案件类型编码
     * */
    public List<Map<String, Object>> getCaseTypeCases(double minHzb, double maxHzb, double minZzb, double maxZzb, Date beginTime, Date endTime, String caseFirType, String caseSecType) {
        CaseDao caseDao = new CaseDao();
        List<Map<String, Object>> caseTypeLst = new ArrayList<>();
        if (StringUtils.isNotEmpty(caseFirType) && "0".equals(caseFirType) && caseSecType == null) {//全部案件
            caseTypeLst = caseDao.queryAllCases(minHzb, maxHzb, minZzb, maxZzb, beginTime, endTime);
        }
        if (StringUtils.isNotEmpty(caseFirType) && caseSecType == null) {//一级案件
            caseTypeLst = caseDao.queryFirTypeCases(minHzb, maxHzb, minZzb, maxZzb, beginTime, endTime, caseFirType);
        }
        if (StringUtils.isNotEmpty(caseFirType) && StringUtils.isNotEmpty(caseSecType)) {//二级案件
            caseTypeLst = caseDao.querySecTypeCases(minHzb, maxHzb, minZzb, maxZzb, beginTime, endTime, caseFirType, caseSecType);
        }
        return caseTypeLst;
    }

    /* 根据一级分类查询二级分类类型及数量
     * @param minHzb最小横坐标
     * @param maxHzb最大横坐标
     * @param minZzb最小纵坐标
     * @param maxZzb最大纵坐标
     * @param beginTime开始时间
     * @param endTime结束时间
     * @param firtype一级编码
     * */
    public Map<String, Object> getSecTypeNumByFirType(double minHzb, double maxHzb, double minZzb, double maxZzb, Date beginTime, Date endTime, String firtype) {
        CaseDao caseDao = new CaseDao();
        Map<String, Object> resMap = new HashMap<>();
        List<Map<String, Object>> resLst = caseDao.querySecTypeNumByFirType(minHzb, maxHzb, minZzb, maxZzb, beginTime, endTime, firtype);
        List secTypeLst = caseDao.getChildByOneLevel(firtype);
        resMap.put("typeNum", resLst);
        resMap.put("allTypes", secTypeLst);
        return resMap;
    }

    /*查询一定范围一段时间内饼图的数据
     * @param minHzb最小横坐标
     * @param maxHzb最大横坐标
     * @param minZzb最小纵坐标
     * @param maxZzb最大纵坐标
     * @param beginTime开始时间
     * @param endTime结束时间
     * */
    public List<Map<String, Object>> getPieChartData(double minHzb, double maxHzb, double minZzb, double maxZzb, Date beginTime, Date endTime) {
        long start = System.currentTimeMillis();
        CaseDao caseDao = new CaseDao();
        //List<Map<String, Object>> resLst = new ArrayList<>();
        List<Map<String, Object>> oneLst = caseDao.queryPieChartData(minHzb, maxHzb, minZzb, maxZzb, beginTime, endTime);
        long end = System.currentTimeMillis();
        System.out.println("CaseServiceImpl getPieChartData need time: " + (end - start) / 1000d + "s");
        return oneLst;
    }
    /*public List<Map<String, Object>> getPieChartData(double minHzb, double maxHzb, double minZzb, double maxZzb, Date beginTime, Date endTime) {
        CaseDao caseDao = new CaseDao();
        List<Map<String, Object>> resLst = new ArrayList<>();
        List<Map<String, Object>> oneLst = caseDao.queryPieChartData(minHzb, maxHzb, minZzb, maxZzb, beginTime, endTime, "1");//ajxx中的一级分类
        List<Map<String, Object>> twoLst = caseDao.queryPieChartData(minHzb, maxHzb, minZzb, maxZzb, beginTime, endTime, "2");//ajxx中的二级分类
        List<Map<String, Object>> qitaLst = caseDao.queryPieChartData(minHzb, maxHzb, minZzb, maxZzb, beginTime, endTime, "qita");//其他
        long count = 0;
        for (int i = 0; i < qitaLst.size(); i++) {
            if (null != qitaLst.get(i)) {
                Map map = (Map) qitaLst.get(i);
                if (null != map.get("name")) {
                    if ("qita".equals(map.get("name").toString())) {
                        if (null != map.get("count")) {
                            count = count + Long.valueOf(map.get("count").toString());
                        }
                    }
                }
            }
        }
        Map<String, Object> qitaMap = new HashMap<>();
        qitaMap.put("code", "qita");
        qitaMap.put("name", "qita");
        qitaMap.put("count", count);
        resLst.add(qitaMap);
        for (int i = 0; i < oneLst.size(); i++) {
            if (null != oneLst.get(i)) {
                Map map = (Map) oneLst.get(i);
                resLst.add(map);
            }
        }
        for (int i = 0; i < twoLst.size(); i++) {
            if (null != twoLst.get(i)) {
                Map map = (Map) twoLst.get(i);
                resLst.add(map);
            }
        }
        return resLst;
    }*/

    /*查询一定范围一段时间内全部案件、刑事侵财、治安管理、盗窃、殴打他人等折线图的数据
     * @param minHzb最小横坐标
     * @param maxHzb最大横坐标
     * @param minZzb最小纵坐标
     * @param maxZzb最大纵坐标
     * @param beginTime开始时间
     * @param endTime结束时间
     * */
    public Map<String, Object> getLineChartData(double minHzb, double maxHzb, double minZzb, double maxZzb, Date beginTime, Date endTime, List period) {
        CaseDao caseDao = new CaseDao();
        Map<String, Object> map = caseDao.queryLineChartData(minHzb, maxHzb, minZzb, maxZzb, beginTime, endTime, period);
        return map;
    }
   /* public Map<String, Object> getLineChartData(double minHzb, double maxHzb, double minZzb, double maxZzb, Date beginTime, Date endTime) {
        long start = System.currentTimeMillis();
        CaseDao caseDao = new CaseDao();
        Map<String, Object> resMap = new HashMap<>();
        List<Map<String, Object>> allLst = new ArrayList<>();
        List<Map<String, Object>> allCaseLst = caseDao.queryLineChartData(minHzb, maxHzb, minZzb, maxZzb, beginTime, endTime, "0");//全部案件
        List<Map<String, Object>> oneCaseLst = caseDao.queryLineChartData(minHzb, maxHzb, minZzb, maxZzb, beginTime, endTime, "1");//一级分类
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        for (int i = 0; i < allCaseLst.size(); i++) {
            if (null != allCaseLst.get(i)) {
                Map map = (Map) allCaseLst.get(i);
                allLst.add(map);
            }
        }
        for (int i = 0; i < oneCaseLst.size(); i++) {
            if (null != oneCaseLst.get(i)) {
                Map map = (Map) oneCaseLst.get(i);
                allLst.add(map);
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("CaseServiceImpl getLineChartData need time: " + (end - start) / 1000d + "s");
        resMap.put(simpleDateFormat.format(endTime), allLst);
        return resMap;
    }*/
  /*  public Map<String, Object> getLineChartData(double minHzb, double maxHzb, double minZzb, double maxZzb, Date beginTime, Date endTime) {
        CaseDao caseDao = new CaseDao();
        Map<String, Object> resMap = new HashMap<>();
        List<Map<String, Object>> allLst = new ArrayList<>();
        List<Map<String, Object>> allCaseLst = caseDao.queryLineChartData(minHzb, maxHzb, minZzb, maxZzb, beginTime, endTime, "0");//全部案件
        List<Map<String, Object>> oneCaseLst = caseDao.queryLineChartData(minHzb, maxHzb, minZzb, maxZzb, beginTime, endTime, "1");//一级分类
        List<Map<String, Object>> twoCaseLst = caseDao.queryLineChartData(minHzb, maxHzb, minZzb, maxZzb, beginTime, endTime, "2");//二级分类
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        for (int i = 0; i < allCaseLst.size(); i++) {
            if (null != allCaseLst.get(i)) {
                Map map = (Map) allCaseLst.get(i);
                allLst.add(map);
            }
        }
        for (int i = 0; i < oneCaseLst.size(); i++) {
            if (null != oneCaseLst.get(i)) {
                Map map = (Map) oneCaseLst.get(i);
                allLst.add(map);
            }
        }
        for (int i = 0; i < twoCaseLst.size(); i++) {
            if (null != twoCaseLst.get(i)) {
                Map map = (Map) twoCaseLst.get(i);
                allLst.add(map);
            }
        }
        resMap.put(simpleDateFormat.format(endTime), allLst);
        return resMap;
    }*/

    /*右侧案件tab页中所有数据
     * @param minHzb最小横坐标
     * @param maxHzb最大横坐标
     * @param minZzb最小纵坐标
     * @param maxZzb最大纵坐标
     * @param beginTime开始时间
     * @param endTime结束时间
     * */
    /*public List getCaseChartData(double minHzb, double maxHzb, double minZzb, double maxZzb, Date beginTime, Date endTime) throws Exception {
        CaseDao caseDao = new CaseDao();
        List resLst = new ArrayList();
        List<Map<String, Object>> lineLst = new ArrayList<>();
        List<Map<String, Object>> allCaseLst = caseDao.queryLineChartData(minHzb, maxHzb, minZzb, maxZzb, beginTime, endTime, "0");//全部案件
        List<Map<String, Object>> oneCaseLst = caseDao.queryLineChartData(minHzb, maxHzb, minZzb, maxZzb, beginTime, endTime, "1");//一级分类
        List<Map<String, Object>> twoCaseLst = caseDao.queryLineChartData(minHzb, maxHzb, minZzb, maxZzb, beginTime, endTime, "2");//二级分类
        long totalCaseNum = 0;
        for (int i = 0; i < allCaseLst.size(); i++) {
            if (null != allCaseLst.get(i)) {
                Map map = (Map) allCaseLst.get(i);
                if (null != map.get("code")) {
                    if ("0".equals(map.get("code").toString())) {
                        if(null != map.get("count")){
                            totalCaseNum = Long.valueOf(map.get("count").toString());
                        }
                    }
                }
                lineLst.add(map);
            }
        }
        for (int i = 0; i < oneCaseLst.size(); i++) {
            if (null != oneCaseLst.get(i)) {
                Map map = (Map) oneCaseLst.get(i);
                lineLst.add(map);
            }
        }
        for (int i = 0; i < twoCaseLst.size(); i++) {
            if (null != twoCaseLst.get(i)) {
                Map map = (Map) twoCaseLst.get(i);
                lineLst.add(map);
            }
        }
        List<Map<String, Object>> pieLst = new ArrayList<>();
        List<Map<String, Object>> oneLst = caseDao.queryPieChartData(minHzb, maxHzb, minZzb, maxZzb, beginTime, endTime, "1");//一级分类
        List<Map<String, Object>> twoLst = caseDao.queryPieChartData(minHzb, maxHzb, minZzb, maxZzb, beginTime, endTime, "2");//二级分类
        List<Map<String, Object>> qitaLst = caseDao.queryPieChartData(minHzb, maxHzb, minZzb, maxZzb, beginTime, endTime, "qita");//其他
        long count = 0;
        for (int i = 0; i < qitaLst.size(); i++) {
            if (null != qitaLst.get(i)) {
                Map map = (Map) qitaLst.get(i);
                if (null != map.get("name")) {
                    if ("qita".equals(map.get("name").toString())) {
                        if (null != map.get("count")) {
                            count = count + Long.valueOf(map.get("count").toString());
                        }
                    }
                }
            }
        }
        Map<String, Object> qitaMap = new HashMap<>();
        qitaMap.put("code", "qita");
        qitaMap.put("name", "qita");
        qitaMap.put("count", count);
        pieLst.add(qitaMap);
        for (int i = 0; i < oneLst.size(); i++) {
            if (null != oneLst.get(i)) {
                Map map = (Map) oneLst.get(i);
                pieLst.add(map);
            }
        }
        for (int i = 0; i < twoLst.size(); i++) {
            if (null != twoLst.get(i)) {
                Map map = (Map) twoLst.get(i);
                pieLst.add(map);
            }
        }
        ChartDataServiceImpl chartDataServiceImpl = new ChartDataServiceImpl();
        double area = chartDataServiceImpl.getArea(minHzb, maxHzb, minZzb, maxZzb);
        double totalCaseDensity = totalCaseNum / area;//总案件密度
        resLst.add(lineLst);
        resLst.add(pieLst);
        return resLst;
    }*/

    /* 根据案件编号查询案件详情
     * @param ajbh案件编号
     * */
    public Map<String, Object> queryCaseDetail(String ajbh) {
        CaseDao caseDao = new CaseDao();
        Map<String, Object> map = caseDao.queryCaseDetail(ajbh);
        return map;
    }

    /* 查询出所以派出所辖区的案件密度值
     * */
    public Map getCaseAreaDensity(String startTime, String endTime) {
        CaseDao caseDao = new CaseDao();
        //List resLst = new ArrayList();
        Map resMap = new HashMap();
        if (null != startTime && null != endTime) {
            resMap = caseDao.queryCaseAreaDensity(startTime, endTime);
        }
        return resMap;
    }
}
