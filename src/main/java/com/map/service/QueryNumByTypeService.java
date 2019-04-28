package com.map.service;

import com.alibaba.fastjson.JSONObject;
import com.map.serviceImp.QueryNumByTypeServiceImpl;
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

public class QueryNumByTypeService extends Service {
    private Log log = LogFactory.getLog(QueryNumByTypeService.class);

    private String reqJsonStr;

    public QueryNumByTypeService(String reqJsonStr) {
        this.reqJsonStr = reqJsonStr;
    }

    /* 获取一定范围内各种类型的点集合接口
     * */
    @Override
    public String doService(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String status = null;
        String message = "";
        StringBuilder sb = new StringBuilder(message);
        JSONObject p = JSONObject.parseObject(reqJsonStr);
        log.info("QueryNumByTypeService请求参数：" + p);
        Map pointsMap = (Map) p.get("points");//前端传过来的多边形顶点
        Map sw = (Map) pointsMap.get("_sw");//西南方向
        Map ne = (Map) pointsMap.get("_ne");//东北方向
        double swLng = Double.valueOf(sw.get("lng").toString());//经度
        double swLat = Double.valueOf(sw.get("lat").toString());//纬度
        double neLng = Double.valueOf(ne.get("lng").toString());
        double neLat = Double.valueOf(ne.get("lat").toString());
        QueryNumByTypeServiceImpl queryPointsByTypeServiceImpl = new QueryNumByTypeServiceImpl();

        List resLst = new ArrayList();
        if (p.get("firtype") != null) {   //1:人口  2:单位   3:房屋
            String firtype = p.get("firtype").toString();
            if (p.get("sectype") != null) {
                if (firtype.equals("1")) {//人口     //sectype  1:总人口    2:常口   3:流口  4：重点    5：境外
                    resLst = queryPointsByTypeServiceImpl.getPopfirPoints(p.get("sectype").toString(), swLng, neLng, swLat, neLat);
                } else if (firtype.equals("2")) {//单位    //sectype   1:总单位    2:普通单位   3:特种单位  4：保护单位    5：九小场所
                    resLst = queryPointsByTypeServiceImpl.getDwfirPoints(p.get("sectype").toString(), swLng, neLng, swLat, neLat);
                } else if (firtype.equals("3")) {//房屋    //sectype    1:总房屋   2:自住   3:出租  4：空置
                    resLst = queryPointsByTypeServiceImpl.getFwPoints(p.get("sectype").toString(), swLng, neLng, swLat, neLat);
                }
            }

            if (p.get("thirtype") != null) {//小类标识
                if (firtype.equals("1")) {//人口
                    resLst = queryPointsByTypeServiceImpl.getPopsecPoints(p.get("thirtype").toString(), swLng, neLng, swLat, neLat);
                } else if (firtype.equals("2")) {//单位
                    if (p.get("flag") != null) {//区分是保护还是特种单位标识   1:重点单位    2：保护单位
                        resLst = queryPointsByTypeServiceImpl.getDwsecPoints(p.get("flag").toString(), p.get("thirtype").toString(), swLng, neLng, swLat, neLat);
                    } else {
                        sb.append("flag is null");
                    }
                }
            }
            log.info("QueryNumByTypeService请求数据" + resLst);
        } else {
            sb.append("firtype is null");
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status", status);
        jsonObject.put("message", sb.toString());
        jsonObject.put("data", resLst);
        res.setContentType("application/json;charset=UTF-8");
        res.setHeader("Access-Control-Allow-Origin", "*");
        PrintWriter out = res.getWriter();
        out.write(jsonObject.toString());
        return "QueryNumByTypeService finish";
    }

   /* @Override
    public String doService(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String status = null;
        String message = "";
        StringBuilder sb = new StringBuilder(message);
        JSONObject p = JSONObject.parseObject(reqJsonStr);
        Map pointsMap = (Map) p.get("points");//前端传过来的多边形顶点
        log.info("QueryPointsByTypeService请求参数：" + pointsMap);
        Map sw = (Map) pointsMap.get("_sw");//西南方向
        Map ne = (Map) pointsMap.get("_ne");//东北方向
        double swLng = (double) sw.get("lng");//经度
        double swLat = (double) sw.get("lat");//纬度
        double neLng = (double) ne.get("lng");
        double neLat = (double) ne.get("lat");
        QueryNumByTypeServiceImpl queryPointsByTypeServiceImpl = new QueryNumByTypeServiceImpl();
        List resLst = new ArrayList();
        try {
            if (p.get("firtype") != null && p.get("firtype") != "") {
                if (p.get("sectype") != null && p.get("sectype") != "") {
                    if (p.get("thirtype") != null && p.get("thirtype") != "") {//第三大类
                        resLst = queryPointsByTypeServiceImpl.QueryPointsByType(p.get("firtype").toString(), p.get("sectype").toString(), p.get("thirtype").toString(),swLng, neLng, swLat, neLat);
                    } else {//第二大类
                        resLst = queryPointsByTypeServiceImpl.QueryPointsByType(p.get("firtype").toString(), p.get("sectype").toString(), null,swLng, neLng, swLat, neLat);
                    }
                } else {//第一大类
                    resLst = queryPointsByTypeServiceImpl.QueryPointsByType(p.get("firtype").toString(), null, null,swLng, neLng, swLat, neLat);
                }
            } else {
                sb.append("firtype is null");
            }

        } catch (Exception e) {
            status = "exception";
            sb.append(e);
            log.error("QueryNumByTypeService", e);
            e.printStackTrace();
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status", status);
        jsonObject.put("message", sb.toString());
        jsonObject.put("data", resLst);
        res.setContentType("application/json;charset=UTF-8");
        res.setHeader("Access-Control-Allow-Origin", "*");
        PrintWriter out = res.getWriter();
        out.write(jsonObject.toString());
        return "QueryNumByTypeService finish";
    }*/

}
