package com.map.service;

import com.alibaba.fastjson.JSONObject;
import com.map.serviceImp.QueryThirTypesNumServiceImpl;
import com.map.svc.Service;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class QueryThirTypesNumService extends Service {
    private Log log = LogFactory.getLog(QueryThirTypesNumService.class);

    private String reqJsonStr;

    public QueryThirTypesNumService(String reqJsonStr) {
        this.reqJsonStr = reqJsonStr;
    }

    /* 点击重点人口饼图，查询右下角更细小的类型在当前屏幕中的数量
     * */
    @Override
    public String doService(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String status = null;
        String message = "";
        StringBuilder sb = new StringBuilder(message);
        JSONObject p = JSONObject.parseObject(reqJsonStr);
        log.info("QueryThirTypesNumService请求参数：" + p);
        Map pointsMap = (Map) p.get("points");//前端传过来的多边形顶点
        Map sw = (Map) pointsMap.get("_sw");//西南方向
        Map ne = (Map) pointsMap.get("_ne");//东北方向
        double swLng = Double.valueOf(sw.get("lng").toString());//经度
        double swLat = Double.valueOf(sw.get("lat").toString());//纬度
        double neLng = Double.valueOf(ne.get("lng").toString());
        double neLat = Double.valueOf(ne.get("lat").toString());
        QueryThirTypesNumServiceImpl queryRightTypesNumServiceImpl = new QueryThirTypesNumServiceImpl();
        Map<String, Object> map = new HashMap<String, Object>();
        if (p.get("type") != null) {//右侧重点人口饼图类型编码
            map = queryRightTypesNumServiceImpl.getPopThirNum(p.get("type").toString(), swLng, neLng, swLat, neLat);
            log.info("QueryThirTypesNumService返回数据:" + map);
        } else {
            sb.append("type is null");
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status", status);
        jsonObject.put("message", sb.toString());
        jsonObject.put("data", map);
        res.setContentType("application/json;charset=UTF-8");
        res.setHeader("Access-Control-Allow-Origin", "*");
        PrintWriter out = res.getWriter();
        out.write(jsonObject.toString());
        return "QueryThirTypesNumService finish";
    }
}
