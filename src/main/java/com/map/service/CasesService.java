package com.map.service;

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
import java.util.Date;
import java.util.List;
import java.util.Map;

public class CasesService extends Service {
    private Log log = LogFactory.getLog(CasesService.class);
    private String jsonStr;

    public CasesService(String jsonStr) {
        this.jsonStr = jsonStr;
    }

    /*各种类型案件点位图（饼图点击）*/
    @Override
    public String doService(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String status = null;
        String message = "";
        StringBuilder sb = new StringBuilder(message);
        JSONObject p = JSONObject.parseObject(jsonStr);
        log.info("CasesService请求参数：" + p);
        Map pointsMap = (Map) p.get("points");//前端传过来的多边形顶点
        Map sw = (Map) pointsMap.get("_sw");//西南方向
        Map ne = (Map) pointsMap.get("_ne");//东北方向
        double swLng = Double.valueOf(sw.get("lng").toString());//经度
        double swLat = Double.valueOf(sw.get("lat").toString());//纬度
        double neLng = Double.valueOf(ne.get("lng").toString());
        double neLat = Double.valueOf(ne.get("lat").toString());
        Date beginTime = null;
        Date endTime = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        /*单个的开始时间结束时间*/
        if (null != p.get("beginTime")) {
            try {
                beginTime = sdf.parse(p.get("beginTime").toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            sb.append("beginTime is null");
        }
        if (null != p.get("endTime")) {
            try {
                endTime = sdf.parse(p.get("endTime").toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }

        } else {
            sb.append("endTime is null");
        }
        String caseFirType = null;//一级案件类型编号
        if (null != p.get("caseFirType")) {//一级案件类型编码
            caseFirType = p.get("caseFirType").toString();
        } else {
            sb.append("caseFirType is null");
        }
        String caseSecType = null;//二级案件类型编号
        if (null != p.get("caseSecType")) {//二级案件类型编码
            caseSecType = p.get("caseSecType").toString();
        }
        CaseServiceImpl caseServiceImpl = new CaseServiceImpl();
        List<Map<String, Object>> caseLst = caseServiceImpl.getCaseTypeCases(swLng, neLng, swLat, neLat, beginTime, endTime, caseFirType, caseSecType);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status", status);
        jsonObject.put("message", sb.toString());
        jsonObject.put("data", caseLst);

        res.setContentType("application/json;charset=UTF-8");
        res.setHeader("Access-Control-Allow-Origin", "*");
        PrintWriter out = res.getWriter();
        out.write(jsonObject.toString());
        return "CasesService finish";
    }

}
