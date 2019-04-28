package com.map.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.map.serviceImp.AreaDensityServiceImpl;
import com.map.svc.Service;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AreaDensityService extends Service {

    private Log log = LogFactory.getLog(AreaDensityService.class);

    private String jsonStr;

    public AreaDensityService(String jsonStr) {
        this.jsonStr = jsonStr;
    }

    /* 密度图接口
     * */
    @Override
    public String doService(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String status = null;
        String message = "";
        StringBuilder sb = new StringBuilder(message);
        JSONObject p = JSONObject.parseObject(jsonStr);
        log.info("AreaDensityService请求参数：" + p);
        JSONArray array = (JSONArray) p.get("xqbms");
        AreaDensityServiceImpl areaDensityServiceImpl = new AreaDensityServiceImpl();
        List bmLst = new ArrayList();
        if (bmLst != null && bmLst.size() != 0) {
            for (int i = 0; i < array.size(); i++) {
                bmLst.add(array.get(i).toString());
            }
        }
        Map<String, Object> map = new HashMap<>();
        try {
            if (p.get("firtype") != null && p.get("firtype") != "") {//1:人口
                if (p.get("secType") != null && p.get("secType") != "") {//1:总人口密度  2：流口密度  3：重点人口密度
                    map = areaDensityServiceImpl.queryAreaDensity(bmLst, p.get("firtype").toString(), p.get("secType").toString());
                }
            } else {
                sb.append(" firtype is null; ");
            }
            log.info("AreaDensityService返回数据:" + map);
        } catch (Exception e) {
            status = "exception";
            sb.append(e);
            log.error("AreaDensityService", e);
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
        return "AreaDensityService finish";
    }
}
