package com.map.service;

import com.alibaba.fastjson.JSONObject;
import com.map.serviceImp.ChartDataServiceImpl;
import com.map.svc.Service;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class ChartDataService extends Service {

    private Log log = LogFactory.getLog(ChartDataService.class);

    private String jsonStr;

    public ChartDataService(String jsonStr) {
        this.jsonStr = jsonStr;
    }


    /* 获取右侧统计图数据接口
     * */
    @Override
    public String doService(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String status = null;
        String message = "";
        StringBuilder sb = new StringBuilder(message);
        JSONObject p = JSONObject.parseObject(jsonStr);
        log.info("ChartDataService请求参数：" + p);
        Map pointsMap = (Map) p.get("points");//前端传过来的多边形顶点
        Map sw = (Map) pointsMap.get("_sw");//西南方向
        Map ne = (Map) pointsMap.get("_ne");//东北方向
        double swLng = Double.valueOf(sw.get("lng").toString()) ;//经度
        double swLat = Double.valueOf(sw.get("lat").toString()) ;//纬度
        double neLng = Double.valueOf(ne.get("lng").toString()) ;
        double neLat = Double.valueOf(ne.get("lat").toString()) ;
        String mapLevel = null;
        if (p.get("mapLevel") != null) {//地图级别数
//            int a = (int)Math.floor(p.getDoubleValue("mapLevel"));
            mapLevel = p.getDoubleValue("mapLevel") + "";
//            mapLevel = p.get("mapLevel").toString();
        } else {
            sb.append("mapLevel is null");
        }

        Map<String, Object> map = new HashMap<String, Object>();
        try {
            if (p.get("flag") != null) {//1:人口   2：单位   3：房屋
                ChartDataServiceImpl chartDataServiceImpl = new ChartDataServiceImpl();
                map = chartDataServiceImpl.getAllChartData(p.get("flag").toString(), mapLevel, swLng, neLng, swLat, neLat);
                log.info("ChartDataService返回数据:" + map);
            } else {
                sb.append(" flag is null; ");
            }
            status = "success";
        } catch (Exception e) {
            status = "exception";
            sb.append(e);
            log.error("ChartDataService", e);
            e.printStackTrace();
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status", status);
        jsonObject.put("message", sb.toString());
        jsonObject.put("data", map);

        res.setContentType("application/json;charset=UTF-8");
        res.setHeader("Access-Control-Allow-Origin", "*");
        PrintWriter out = res.getWriter();
        out.write(jsonObject.toString());
        return "ChartDataService finish";
    }

   /* @Override
    public String doService(HttpServletRequest req, HttpServletResponse res) throws IOException {

        //JSONArray array = (JSONArray) p.get("points"); //前端传过来的多边形顶点
        Map pointsMap = (Map) p.get("points");//前端传过来的多边形顶点
        log.info("ChartDataService请求参数：" + pointsMap);
        Map sw = (Map) pointsMap.get("_sw");//西南方向
        Map ne = (Map) pointsMap.get("_ne");//东北方向
        Object swLng = sw.get("lng");//经度
        Object swLat = sw.get("lat");//纬度
        Object neLng = ne.get("lng");
        Object neLat = ne.get("lat");
        String polygonStr = "";
        *//*if (array != null) {
            polygonStr = "POLYGON((" + array.get(3) + " " + array.get(0) + "," +
                    array.get(1) + " " + array.get(0) + "," +
                    array.get(1) + " " + array.get(2) + "," +
                    array.get(3) + " " + array.get(2) + "," +
                    array.get(3) + " " + array.get(0) + "))";
        } else {
            sb.append("  points is null; ");
        }*//*
        if (!pointsMap.isEmpty()) {
            polygonStr = "POLYGON((" + swLng + " " + swLat + "," +
                    swLng + " " + neLat + "," +
                    neLng + " " + neLat + "," +
                    neLng + " " + swLat + "," +
                    swLng + " " + swLat + "))";
        } else {
            sb.append(" points is null; ");
        }

        PopulationServiceImpl populationServiceImpl = new PopulationServiceImpl();
        Common common = new Common();
        try {
            if (p.get("type") != null) {
                String type = p.get("type").toString();
                log.info("type------:" + type);
                common = populationServiceImpl.getPointList(polygonStr, swLng.toString(), swLat.toString(), neLng.toString(), neLat.toString(), type);//在多边形范围内的人口案件等数据
                log.info(common);
            } else {
                sb.append(" type is null; ");
            }
            System.out.println("common----------:" + common);
            status = "success";
        } catch (Exception e) {
            status = "exception";
            sb.append(e);
            log.error("ChartDataService" ,e);
            e.printStackTrace();
        }
        //String jsonStr = JSONObject.toJSONString(common);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status", status);
        jsonObject.put("message", sb.toString());
        //jsonObject.put("detail", "");
        jsonObject.put("data", common);

        res.setContentType("application/json;charset=UTF-8");
        res.setHeader("Access-Control-Allow-Origin", "*");
        PrintWriter out = res.getWriter();
        out.write(jsonObject.toString());
        return "ChartDataService finish";
    }
*/

}
