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
import java.util.HashMap;
import java.util.Map;

/*展现实有房屋的详情接口*/
public class FwDetailService extends Service {
    private Log log = LogFactory.getLog(FwDetailService.class);

    private String reqJsonStr;

    public FwDetailService(String reqJsonStr) {
        this.reqJsonStr = reqJsonStr;
    }

    @Override
    public String doService(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String status = null;
        String message = "";
        StringBuilder sb = new StringBuilder(message);
        JSONObject p = JSONObject.parseObject(reqJsonStr);
        log.info("FwDetailService请求参数：" + p);
        AllFwServiceImpl allJzwServiceImpl = new AllFwServiceImpl();
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            if (p.get("dzbm") != null && p.get("dzbm") != "") {//房屋地址编码
                map = allJzwServiceImpl.queryInfoByFw(p.get("dzbm").toString());
                log.info("FwDetailService返回数据:" + map);
            } else {
                sb.append("dzbm is null");
            }

        } catch (Exception e) {
            status = "exception";
            sb.append(e);
            log.error("FwDetailService", e);
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
        return "FwDetailService finish";
    }
}
