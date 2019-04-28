package com.map.service;

import com.alibaba.fastjson.JSONObject;
import com.map.repository.CaseDao;
import com.map.serviceImp.CaseServiceImpl;
import com.map.svc.Service;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

public class CaseAreaDensityService extends Service {
    private Log log = LogFactory.getLog(CaseAreaDensityService.class);

    private String jsonStr;

    public CaseAreaDensityService(String jsonStr) {
        this.jsonStr = jsonStr;
    }

    /* 案件密度图接口
     * */
    @Override
    public String doService(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String status = null;
        String message = "";
        StringBuilder sb = new StringBuilder(message);
        JSONObject p = JSONObject.parseObject(jsonStr);
        log.info("CaseAreaDensityService请求参数：" + p);
        String beginTime = null;
        String endTime = null;
        if (null != p.get("beginTime")) {
            beginTime = p.get("beginTime").toString().substring(0, 7);
        }
        if (null != p.get("endTime")) {
            endTime = p.get("endTime").toString().substring(0, 7);
        }
        CaseServiceImpl caseServiceImpl = new CaseServiceImpl();
        Map map = caseServiceImpl.getCaseAreaDensity(beginTime, endTime);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status", status);
        jsonObject.put("message", sb.toString());
        jsonObject.put("data", map);

        res.setContentType("application/json;charset=UTF-8");
        res.setHeader("Access-Control-Allow-Origin", "*");
        PrintWriter out = res.getWriter();
        out.write(jsonObject.toString());
        return "CaseAreaDensityService finish";

    }
}
