package com.map.service;

import com.alibaba.fastjson.JSONObject;
import com.map.serviceImp.JzwDetailServiceImpl;
import com.map.svc.Service;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/*人口列中  点击建筑物展现的详情接口*/
public class JzwDetailService extends Service {

    private Log log = LogFactory.getLog(JzwDetailService.class);

    private String reqJsonStr;

    public JzwDetailService(String reqJsonStr) {
        this.reqJsonStr = reqJsonStr;
    }

    /*根据建筑物编码查询建筑物的详情接口
     * */
    @Override
    public String doService(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String status = null;
        String message = "";
        StringBuilder sb = new StringBuilder(message);
        JSONObject p = JSONObject.parseObject(reqJsonStr);
        log.info("JzwDetailService请求参数：" + p);
        JzwDetailServiceImpl jzwDetailServiceImpl = new JzwDetailServiceImpl();
        Map map = new HashMap();
        try {
            if (p.get("jzwbm") != null && p.get("jzwbm") != "") {//建筑物编码
                map = jzwDetailServiceImpl.queryByJzwbm(p.get("jzwbm").toString());
            } else {
                sb.append("jzwbm is null");
            }
            log.info("JzwDetailService返回数据:" + map);
        } catch (Exception e) {
            status = "exception";
            sb.append(e);
            log.error("JzwDetailService", e);
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
        return "JzwDetailService finish";
    }

}
