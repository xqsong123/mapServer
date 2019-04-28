package com.map.service;

import com.alibaba.fastjson.JSONObject;
import com.map.serviceImp.DynamicPopServiceImpl;
import com.map.svc.Service;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PopService extends Service {
    private Log log = LogFactory.getLog(PopService.class);

    private String reqJsonStr;

    public PopService(String reqJsonStr) {
        this.reqJsonStr = reqJsonStr;
    }

    /*热力图接口*/
    @Override
    public String doService(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String status = null;
        String message = "";
        StringBuilder sb = new StringBuilder(message);
        JSONObject p = JSONObject.parseObject(reqJsonStr);
        log.info("PopService请求参数：" + p);
        Map pointsMap = (Map) p.get("points");//前端传过来的多边形顶点
        double level = Math.round(p.getDoubleValue("level"));
        Map sw = (Map) pointsMap.get("_sw");//西南方向
        Map ne = (Map) pointsMap.get("_ne");//东北方向
        double swLng = Double.valueOf(sw.get("lng").toString());//经度
        double swLat = Double.valueOf(sw.get("lat").toString());//纬度
        double neLng = Double.valueOf(ne.get("lng").toString());
        double neLat = Double.valueOf(ne.get("lat").toString());
        //Date beginTime = (Date) p.get("beginTime");
        //Date endTime = (Date) p.get("endTime");
        DynamicPopServiceImpl dynamicPopServiceImpl = new DynamicPopServiceImpl();
        List list = new ArrayList();
        try {
            if (p.get("firtype") != null && p.get("firtype") != "") {
                if (p.get("firtype").toString().equals("Y")) {//重点人口热力图
                    list = dynamicPopServiceImpl.queryByTime(swLng, neLng, swLat, neLat, null, null, null, "Y", null, level);
                } else {//常口/流口/境外  热力图
                    list = dynamicPopServiceImpl.queryByTime(swLng, neLng, swLat, neLat, null, null, p.get("firtype").toString(), null, null, level);//11：常口   12：流动  20：境外
                }
            } else if (p.get("sectype") != null && p.get("sectype") != "") {
                list = dynamicPopServiceImpl.queryByTime(swLng, neLng, swLat, neLat, null, null, null, null, p.get("sectype").toString(), level);

            } else {//总人口热力图
                list = dynamicPopServiceImpl.queryByTime(swLng, neLng, swLat, neLat, null, null, null, null, null, level);
            }
        } catch (Exception e) {
            status = "exception";
            sb.append(e);
            log.error("PopService", e);
            e.printStackTrace();
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status", status);
        jsonObject.put("message", sb.toString());
        jsonObject.put("data", list);
        res.setContentType("application/json;charset=UTF-8");
        res.setHeader("Access-Control-Allow-Origin", "*");
        PrintWriter out = res.getWriter();
        out.write(jsonObject.toString());
        return "PopService finish";
    }
}
