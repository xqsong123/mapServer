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

/*人口详情*/
public class PersonDetailService extends Service {
    private Log log = LogFactory.getLog(PersonDetailService.class);

    private String jsonStr;

    public PersonDetailService(String jsonStr) {
        this.jsonStr = jsonStr;
    }

    /* 人口详情接口
     * */
    @Override
    public String doService(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String status = null;
        String message = "";
        StringBuilder sb = new StringBuilder(message);
        JSONObject p = JSONObject.parseObject(jsonStr);
        log.info("PersonDetailService请求参数：" + p);
        Map<String, Object> map = new HashMap<String, Object>();
        JzwDetailServiceImpl jzwDetailServiceImpl = new JzwDetailServiceImpl();
        try {
            if (p.get("rkbm") != null && p.get("rkbm") != "") {//人口编码
                map = jzwDetailServiceImpl.queryPersonInfoByRkbm(p.get("rkbm").toString());
            } else {
                sb.append("rkbm is null");
            }
            log.info("PersonDetailService返回数据:" + map);
        } catch (Exception e) {
            status = "exception";
            sb.append(e);
            log.error("PersonDetailService", e);
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
        return "PersonDetailService finish";

    }
}
