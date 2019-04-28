package com.map.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.map.serviceImp.CaseServiceImpl;
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

public class DynamicCaseService extends Service {
    private Log log = LogFactory.getLog(DynamicCaseService.class);

    private String jsonStr;

    public DynamicCaseService(String jsonStr) {
        this.jsonStr = jsonStr;
    }

    /*查询一定经纬度范围和时间范围内的各种类型案件（热力图）*/
    @Override
    public String doService(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String status = null;
        String message = "";
        StringBuilder sb = new StringBuilder(message);
        JSONObject p = JSONObject.parseObject(jsonStr);
        //log.info("DynamicCaseService请求参数：" + p);
        Map pointsMap = (Map) p.get("points");//前端传过来的两个顶点
        double swLng = 0;
        double swLat = 0;
        double neLng = 0;
        double neLat = 0;
        if (null != pointsMap && pointsMap.size() > 0) {
            Map sw = (Map) pointsMap.get("_sw");//西南方向
            if (null != sw && sw.size() > 0) {
                if (null != sw.get("lng")) {
                    swLng = Double.valueOf(sw.get("lng").toString());//西南经度
                } else {
                    sb.append("sw lng is null");
                }
                if (null != sw.get("lat")) {
                    swLat = Double.valueOf(sw.get("lat").toString());//西南纬度
                } else {
                    sb.append("sw lat is null");
                }

            } else {
                sb.append("_sw is null");
            }
            Map ne = (Map) pointsMap.get("_ne");//东北方向
            if (null != ne && ne.size() > 0) {
                if (null != ne.get("lng")) {
                    neLng = Double.valueOf(ne.get("lng").toString());//东北经度
                } else {
                    sb.append("ne lng is null");
                }
                if (null != ne.get("lat")) {
                    neLat = Double.valueOf(ne.get("lat").toString());//东北纬度
                } else {
                    sb.append("ne lat is null");
                }
            } else {
                sb.append("ne is null");
            }
        } else {
            sb.append("points is null");
        }
        String caseType = null;//0:全部案件、212:刑事侵财、101:治安管理、101030078:盗窃、101030068:殴打他人
        if (null != p.get("caseType")) {//案件类型编码
            caseType = p.get("caseType").toString();
        } else {
            sb.append("caseType is null");
        }
        CaseServiceImpl caseServiceImpl = new CaseServiceImpl();
        Date beginTime = null;
        Date endTime = null;

        /*多个的开始时间结束时间*/
        Map resMap = new HashMap();
        JSONArray array = (JSONArray) p.get("dates");
        for (int i = 0; i < array.size(); i++) {
            Map oneDate = (Map) array.get(i);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            if (null != oneDate.get("start")) {
                try {
                    beginTime = simpleDateFormat.parse(oneDate.get("start").toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            if (null != oneDate.get("end")) {
                try {
                    endTime = simpleDateFormat.parse(oneDate.get("end").toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

           /* String start = "";
            String end = "";
            start = start+oneDate.get(0)+oneDate.get(1)+oneDate.get(2);
            end = end + oneDate.get(3)+oneDate.get(4)+oneDate.get(6);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                beginTime = simpleDateFormat.parse(start);
                endTime = simpleDateFormat.parse(end);
            } catch (ParseException e) {
                e.printStackTrace();
            }*/

            List<Map<String, Object>> caseLst = caseServiceImpl.getDynamicCases(swLng, neLng, swLat, neLat, beginTime, endTime,caseType);
            resMap.put(oneDate.get("start").toString(), caseLst);
        }

        /*单个的开始时间结束时间*/
     /*   if (null != p.get("beginTime")) {
            beginTime = (Date) p.get("beginTime");
        } else {
            sb.append("beginTime is null");
        }
        if (null != p.get("endTime")) {
            endTime = (Date) p.get("endTime");
        } else {
            sb.append("endTime is null");
        }*/

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status", status);
        jsonObject.put("message", sb.toString());
        jsonObject.put("data", resMap);

        res.setContentType("application/json;charset=UTF-8");
        res.setHeader("Access-Control-Allow-Origin", "*");
        PrintWriter out = res.getWriter();
        out.write(jsonObject.toString());
        return "DynamicCaseService finish";
    }

}
