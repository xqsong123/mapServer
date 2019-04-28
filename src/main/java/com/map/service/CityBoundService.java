package com.map.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.map.serviceImp.CityBoundServiceImpl;
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
 * 本类作为处理切换城市的接口
 */
public class CityBoundService extends Service {

    private Log log = LogFactory.getLog(ChartDataService.class);
    private String jsonStr;

    public CityBoundService(String jsonStr) {
        this.jsonStr = jsonStr;
    }

    @Override
    public String doService(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String status = null;
        String message = "";
        StringBuilder sb = new StringBuilder(message);
        JSONObject p = JSONObject.parseObject(jsonStr);
        JSONArray array = (JSONArray) p.get("points"); //前端传过来的中心点坐标
        double x = 0d;
        double y = 0d;
        if (array != null) {
            x = Double.parseDouble(array.get(0).toString());
            y = Double.parseDouble(array.get(1).toString());
        } else {
            sb.append(" points is null; ");
        }
        CityBoundServiceImpl cityBoundServiceImpl = new CityBoundServiceImpl();
        Map<String, Object> resultMap = new HashMap<>();
        try {
            resultMap = cityBoundServiceImpl.getCityName(x, y);
            System.out.println("resultMap---------:" + resultMap);
            status = "success";
        } catch (Exception e) {
            status = "exception";
            sb.append(e);
            log.error("CityBoundService" , e);
            e.printStackTrace();
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status", status);
        jsonObject.put("message", sb.toString());
        jsonObject.put("data", resultMap);
        res.setContentType("application/json;charset=UTF-8");
        res.setHeader("Access-Control-Allow-Origin","*");
        PrintWriter out = res.getWriter();
        out.write(jsonObject.toString());
        return "CityBoundService finish";
    }

}
