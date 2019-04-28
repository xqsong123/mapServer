package com.map.service;

import com.alibaba.fastjson.JSONObject;
import com.map.serviceImp.AllFwServiceImpl;
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

public class AllFwService extends Service {
    private Log log = LogFactory.getLog(AllFwService.class);

    private String jsonStr;

    public AllFwService(String jsonStr) {
        this.jsonStr = jsonStr;
    }

    /*获取所有的房屋接口*/
    @Override
    public String doService(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String status = null;
        String message = "";
        StringBuilder sb = new StringBuilder(message);
        JSONObject p = JSONObject.parseObject(jsonStr);
        log.info("AllFwService请求参数：" + p);
        Map pointsMap = (Map) p.get("points");//前端传过来的多边形顶点
        Map sw = (Map) pointsMap.get("_sw");//西南方向
        Map ne = (Map) pointsMap.get("_ne");//东北方向
        double swLng = Double.valueOf(sw.get("lng").toString());//经度
        double swLat = Double.valueOf(sw.get("lat").toString());//纬度
        double neLng = Double.valueOf(ne.get("lng").toString());
        double neLat = Double.valueOf(ne.get("lat").toString());
        AllFwServiceImpl allFwServiceImpl = new AllFwServiceImpl();
        List list = new ArrayList();
        try {
            if (p.get("type") != null && p.get("type") != "") {   //1:自用   2：出租   3：闲置
                list = allFwServiceImpl.getAllFw(p.get("type").toString(), swLng, neLng, swLat, neLat);
            } else {
                list = allFwServiceImpl.getAllFw(null, swLng, neLng, swLat, neLat);
            }
            log.info("AllFwService返回数据:" + list);
        } catch (Exception e) {
            status = "exception";
            sb.append(e);
            log.error("AllFwService", e);
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
        return "AllFwService finish";
    }

}
