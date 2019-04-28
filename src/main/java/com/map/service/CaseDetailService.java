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
import java.util.HashMap;
import java.util.Map;

public class CaseDetailService extends Service {
    private Log log = LogFactory.getLog(CaseDetailService.class);

    private String reqJsonStr;

    public CaseDetailService(String reqJsonStr) {
        this.reqJsonStr = reqJsonStr;
    }

    /*案件详情*/
    @Override
    public String doService(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String status = null;
        String message = "";
        StringBuilder sb = new StringBuilder(message);
        JSONObject p = JSONObject.parseObject(reqJsonStr);
        log.info("CaseDetailService请求参数：" + p);
        Map<String, Object> map = new HashMap<>();
        if (p.get("ajbh") != null && p.get("ajbh") != "") {//案件编码
            CaseServiceImpl caseServiceImpl = new CaseServiceImpl();
            map = caseServiceImpl.queryCaseDetail(p.get("ajbh").toString());
        } else {
            sb.append("ajbh is null");
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status", status);
        jsonObject.put("message", sb.toString());
        jsonObject.put("data", map);
        res.setContentType("application/json;charset=UTF-8");
        res.setHeader("Access-Control-Allow-Origin", "*");
        PrintWriter out = res.getWriter();
        out.write(jsonObject.toString());
        return "CaseDetailService finish";
    }
}
