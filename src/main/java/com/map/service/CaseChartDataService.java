package com.map.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.map.serviceImp.CaseServiceImpl;
import com.map.serviceImp.ChartDataServiceImpl;
import com.map.svc.Service;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class CaseChartDataService extends Service {
    private Log log = LogFactory.getLog(ChartDataService.class);

    private String jsonStr;

    public CaseChartDataService(String jsonStr) {
        this.jsonStr = jsonStr;
    }

    /* 右侧统计图tab案件中的数据接口
     * */
    @Override
    public String doService(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String status = null;
        String message = "";
        StringBuilder sb = new StringBuilder(message);
        JSONObject p = JSONObject.parseObject(jsonStr);
        log.info("CaseChartDataService请求参数：" + p);
        Map pointsMap = (Map) p.get("points");//前端传过来的多边形顶点
        Map sw = (Map) pointsMap.get("_sw");//西南方向
        Map ne = (Map) pointsMap.get("_ne");//东北方向
        double swLng = Double.valueOf(sw.get("lng").toString());//经度
        double swLat = Double.valueOf(sw.get("lat").toString());//纬度
        double neLng = Double.valueOf(ne.get("lng").toString());
        double neLat = Double.valueOf(ne.get("lat").toString());
        Date beginTime = null;
        Date endTime = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        /*时间栏中的时间*/
        if (null != p.get("beginTime") && null != p.get("endTime")) {
            try {
                beginTime = simpleDateFormat.parse(p.get("beginTime").toString());
                endTime = simpleDateFormat.parse(p.get("endTime").toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            sb.append("beginTime or endTime is null");
        }

        CaseServiceImpl caseServiceImpl = new CaseServiceImpl();
        Map<String, Object> resMap = new HashMap<>();

        ChartDataServiceImpl chartDataServiceImpl = new ChartDataServiceImpl();
        double area = 0;
        double totalCaseDensity = 0;
        int totalCase = 0;
        try {
            List casePieLst = caseServiceImpl.getPieChartData(swLng, neLng, swLat, neLat, beginTime, endTime);
            resMap.put("casePieData", casePieLst);
            int totalCaseNum = 0;
            for (int i = 0; i < casePieLst.size(); i++) {
                if (null != casePieLst.get(i)) {
                    Map map = (Map) casePieLst.get(i);
                    if (null != map.get("count")) {
                        totalCaseNum = totalCaseNum + Integer.parseInt(map.get("count").toString());
                    }
                }
            }
            area = chartDataServiceImpl.getArea(swLng, neLng, swLat, neLat);
            totalCaseDensity = totalCaseNum / area / 110 / 110;//总案件密度
            totalCase = 1 + (int) Math.log10(totalCaseDensity + 5);
            /*多个的开始时间结束时间（折线图中的时间段）*/
            JSONArray array = (JSONArray) p.get("dates");
            // List totalLineData = new ArrayList();
            long start = System.currentTimeMillis();
            List timePeriod = new ArrayList();
            timePeriod.add(0);
            Map firDate = (Map) array.get(0);
            Map lastDate = (Map) array.get(array.size() - 1);
            for (int i = 0; i < array.size(); i++) {
                Date beginDate = null;
                Date endDate = null;
                long daysBetween = 0;
                Map oneDate = (Map) array.get(i);
                if (null != oneDate.get("start") && null != oneDate.get("end")) {
                    try {
                        beginDate = simpleDateFormat.parse(oneDate.get("start").toString());
                        endDate = simpleDateFormat.parse(oneDate.get("end").toString());
                        daysBetween = (endDate.getTime() - simpleDateFormat.parse(firDate.get("start").toString()).getTime() + 1000000) / (60 * 60 * 24 * 1000);
                        timePeriod.add(daysBetween);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                /*单个小时间段之内的数据*/
                //Map xLineData = caseServiceImpl.getLineChartData(swLng, neLng, swLat, neLat, beginDate, endDate);
                //totalLineData.add(xLineData);
            }
            Date startDate = simpleDateFormat.parse(firDate.get("start").toString());
            Date endDate = simpleDateFormat.parse(lastDate.get("end").toString());
            System.out.println("timePeriod--:" + timePeriod);
            Map<String, Object> map = caseServiceImpl.getLineChartData(swLng, neLng, swLat, neLat, startDate, endDate, timePeriod);
            long end = System.currentTimeMillis();
            System.out.println("CaseChartDataService lineChartData need time: " + (end - start) / 1000d + "s");
            resMap.put("caseLineData", map);
            status = "success";
        } catch (Exception e) {
            status = "exception";
            sb.append(e);
            log.error("CaseChartDataService", e);
            e.printStackTrace();
        }
        Map densityMap = new HashMap();
        densityMap.put("totalCaseDensity", totalCase > 10 ? 10 : totalCase);
        resMap.put("caseDensityData", densityMap);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status", status);
        jsonObject.put("message", sb.toString());
        jsonObject.put("data", resMap);

        res.setContentType("application/json;charset=UTF-8");
        res.setHeader("Access-Control-Allow-Origin", "*");
        PrintWriter out = res.getWriter();
        out.write(jsonObject.toString());
        return "CaseChartDataService finish";
    }
}
