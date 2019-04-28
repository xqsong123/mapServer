package com.map.service;

import com.alibaba.fastjson.JSONObject;
import com.map.svc.Service;
import com.map.utils.FileTools;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;


public class QueryRoutesService extends Service {
    private Log log = LogFactory.getLog(QueryRoutesService.class);

    private String jsonStr;

    public QueryRoutesService(String jsonStr) {
        this.jsonStr = jsonStr;
    }

    @Override
    /**
     * 从保存的json文件中获取信息
     */
    public String doService(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String status = null;
        String message = "";
        StringBuilder sb = new StringBuilder(message);
        JSONObject p = JSONObject.parseObject(jsonStr);
        String filePath = null;
        String jsonStr = null;
        //String path = null;
        JSONObject content = null;
        //读取properties配置文件
        String path = "/file.properties";
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        try {
            if (p.get("fileName") != null) {
                linkedHashMap = FileTools.inputFile(path);
                String path1 = (String) linkedHashMap.get("file");
                //path = "/home/byy/file/anbaoluxian";
                filePath = path1 + File.separator+ p.get("fileName").toString()+".json";
                log.info(filePath);
                jsonStr = FileTools.readJsonData(filePath);
                log.info(jsonStr);
                content = JSONObject.parseObject(jsonStr);
                log.info(content);
                status = "success";
            } else {
                sb.append("fileName is null");
            }

        } catch (Exception e) {
            status = "exception";
            sb.append(e);
            log.error("QueryRoutesService",e);
            e.printStackTrace();
        }
        log.info("查询安保路线数据ok");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status", status);
        jsonObject.put("message", sb.toString());
        jsonObject.put("data", content);
        res.setContentType("application/json;charset=UTF-8");
        res.setHeader("Access-Control-Allow-Origin", "*");
        PrintWriter out = res.getWriter();
        out.write(jsonObject.toString());
        return "QueryRoutesService finish";
    }
   /* 从excel中获得保存的数据信息
   public String doService(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String status = null;
        String message = "";
        StringBuilder sb = new StringBuilder(message);
        FileTools fileTools = new FileTools();
        //读取properties配置文件
        String path = "/file.properties";
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        List<List<String>> caseList = new ArrayList<>();
        try {
            linkedHashMap = FileTools.inputFile(path);
            String filePath = (String) linkedHashMap.get("file");
            JSONObject p = JSONObject.parseObject(jsonStr);
            int sheetIndex = Integer.parseInt(p.get("sheetIndex").toString());
            //读取excel中信息
            caseList = fileTools.readExcel(filePath, sheetIndex);
            caseList.remove(0);
            status = "success";
        } catch (Exception e) {
            status = "exception";
            log.info("QueryRoutesService" + e);
            sb.append(e);
            e.printStackTrace();
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status", status);
        jsonObject.put("message", sb.toString());
        jsonObject.put("data", caseList);
        res.setContentType("application/json;charset=UTF-8");
        PrintWriter out = res.getWriter();
        out.write(jsonObject.toString());
        return "QueryRoutesService finish";
    }*/

}
