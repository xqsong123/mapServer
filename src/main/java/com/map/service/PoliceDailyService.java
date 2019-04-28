package com.map.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.map.entity.Global;
import com.map.serviceImp.PoliceDailyServiceImpl;
import com.map.svc.Service;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * 本类作为处理多边形范围内民警日常的接口
 */
public class PoliceDailyService extends Service {

    private Log log = LogFactory.getLog(PoliceDailyService.class);

    private String reqJsonStr;

    public PoliceDailyService(String reqJsonStr) {
        this.reqJsonStr = reqJsonStr;
    }

    @Override
    public String doService(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String status = null;
        String message = "";
        StringBuilder sb = new StringBuilder(message);
        JSONObject p = JSONObject.parseObject(reqJsonStr);
        //JSONArray array = (JSONArray) p.get("points");//前端传过来的多边形顶点
        Map pointsMap = (Map) p.get("points");//前端传过来的多边形顶点
        log.info("PoliceDailyService请求参数：" + pointsMap);
        Map sw = (Map) pointsMap.get("_sw");//西南方向
        Map ne = (Map) pointsMap.get("_ne");//东北方向
        Object swLng = sw.get("lng");//经度
        Object swLat = sw.get("lat");//纬度
        Object neLng = ne.get("lng");
        Object neLat = ne.get("lat");
        String type = null;

        String polygonStr = "";
        /*if (array != null) {
            polygonStr = "POLYGON((" + array.get(3) + " " + array.get(0) + "," +
                    array.get(1) + " " + array.get(0) + "," +
                    array.get(1) + " " + array.get(2) + "," +
                    array.get(3) + " " + array.get(2) + "," +
                    array.get(3) + " " + array.get(0) + "))";
        } else {
            sb.append(" points is null; ");
        }*/
        if (!pointsMap.isEmpty()) {
            polygonStr = "POLYGON((" + swLng + " " + swLat + "," +
                    swLng + " " + neLat + "," +
                    neLng + " " + neLat + "," +
                    neLng + " " + swLat + "," +
                    swLng + " " + swLat + "))";
        } else {
            sb.append(" points is null; ");
        }
        PoliceDailyServiceImpl policeDailyServiceImpl = new PoliceDailyServiceImpl();
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Object> resultMap = new HashMap<String, Object>();
        try {
            map = policeDailyServiceImpl.getPoliceDailys(polygonStr, Global.policeDailyLst);
            JSONArray array = (JSONArray) p.get("type");
            log.info("type----:" + array);
            if (array != null) {
                for (int i = 0; i < array.size(); i++) {
                    if (array.get(i).toString().equals("taskLst")) {//待办任务
                        resultMap.put("taskLst", map.get("taskLst"));
                    } else if (array.get(i).toString().equals("cluesLst")) {//情报线索
                        resultMap.put("cluesLst", map.get("cluesLst"));
                    } else if (array.get(i).toString().equals("casesLst")) {//将到期案件
                        resultMap.put("casesLst", map.get("casesLst"));
                    } else if (array.get(i).toString().equals("residenceLst")) {//居住证到期
                        resultMap.put("residenceLst", map.get("residenceLst"));
                    } else if (array.get(i).toString().equals("immigrationLst")) {//常口迁入
                        resultMap.put("immigrationLst", map.get("immigrationLst"));
                    } else if (array.get(i).toString().equals("helpLst")) {//群众求助
                        resultMap.put("helpLst", map.get("helpLst"));
                    } else if (array.get(i).toString().equals("publicPreLst")) {//治安防范
                        resultMap.put("publicPreLst", map.get("publicPreLst"));
                    }
                }
            } else {
                resultMap.put("taskNum", map.get("taskNum"));
                resultMap.put("cluesNum", map.get("cluesNum"));
                resultMap.put("casesNum", map.get("casesNum"));
                resultMap.put("residenceNum", map.get("residenceNum"));
                resultMap.put("immigrationNum", map.get("immigrationNum"));
                resultMap.put("helpNum", map.get("helpNum"));
                resultMap.put("publicPreNum", map.get("publicPreNum"));
            }
            log.info("resultMap-----:" + resultMap);
            status = "success";
        } catch (Exception e) {
            status = "exception";
            sb.append(e);
            log.error("PoliceDailyService",e);
            e.printStackTrace();
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status", status);
        jsonObject.put("message", sb.toString());
        jsonObject.put("data", resultMap);
        res.setContentType("application/json;charset=UTF-8");
        res.setHeader("Access-Control-Allow-Origin", "*");
        PrintWriter out = res.getWriter();
        out.write(jsonObject.toString());
        return "PoliceDailyService finish";
    }

}
