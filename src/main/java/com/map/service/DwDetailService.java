package com.map.service;

import com.alibaba.fastjson.JSONObject;
import com.map.serviceImp.DwServiceImpl;
import com.map.svc.Service;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/*单位列中  点击单位展现的单位详情接口*/
public class DwDetailService extends Service {
    private Log log = LogFactory.getLog(DwDetailService.class);

    private String reqJsonStr;

    public DwDetailService(String reqJsonStr) {
        this.reqJsonStr = reqJsonStr;
    }

    @Override
    public String doService(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String status = null;
        String message = "";
        StringBuilder sb = new StringBuilder(message);
        JSONObject p = JSONObject.parseObject(reqJsonStr);
        log.info("DwDetailService请求参数：" + p);
        DwServiceImpl dwServiceImpl = new DwServiceImpl();
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            if (p.get("zagldwbm") != null && p.get("zagldwbm") != "") {//单位地址编码
                map = dwServiceImpl.queryDwDetail(p.get("zagldwbm").toString());
                log.info("DwDetailService返回数据:" + map);
            } else {
                sb.append("dwdzbm is null");
            }

        } catch (Exception e) {
            status = "exception";
            sb.append(e);
            log.error("DwDetailService", e);
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
        return "DwDetailService finish";
    }

}
