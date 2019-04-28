package com.map.service;

import com.alibaba.fastjson.JSONObject;
import com.map.serviceImp.DwServiceImpl;
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

public class DwService extends Service {
    private Log log = LogFactory.getLog(DwService.class);

    private String jsonStr;

    public DwService(String jsonStr) {
        this.jsonStr = jsonStr;
    }

    /*获取一定范围内所有的单位的散点图接口*/
    @Override
    public String doService(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String status = null;
        String message = "";
        StringBuilder sb = new StringBuilder(message);
        JSONObject p = JSONObject.parseObject(jsonStr);
        log.info("DwService请求参数：" + p);
        Map pointsMap = (Map) p.get("points");//前端传过来的多边形顶点
        Map sw = (Map) pointsMap.get("_sw");//西南方向
        Map ne = (Map) pointsMap.get("_ne");//东北方向
        double swLng = Double.valueOf(sw.get("lng").toString());//经度
        double swLat = Double.valueOf(sw.get("lat").toString());//纬度
        double neLng = Double.valueOf(ne.get("lng").toString());
        double neLat = Double.valueOf(ne.get("lat").toString());

        DwServiceImpl dwServiceImpl = new DwServiceImpl();
        List list = new ArrayList();
        try {
            if (p.get("firtype") != null && p.get("firtype") != "") {
                if (p.get("sectype") != null && p.get("sectype") != "") {//饼图数据
                    list = dwServiceImpl.queryAllDw(p.get("firtype").toString(), p.get("sectype").toString(), swLng, neLng, swLat, neLat);
                } else {//柱状图
                    list = dwServiceImpl.queryAllDw(p.get("firtype").toString(), null, swLng, neLng, swLat, neLat);
                }
            } else {//总单位
                list = dwServiceImpl.queryAllDw(null, null, swLng, neLng, swLat, neLat);
            }

            log.info("DwService返回数据:" + list);
        } catch (Exception e) {
            status = "exception";
            sb.append(e);
            log.error("DwService", e);
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
        return "DwService finish";
    }

}
