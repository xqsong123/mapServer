package com.map.service;

import com.alibaba.fastjson.JSONObject;
import com.map.svc.Service;
import com.map.utils.FileTools;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class StoreRoutesService extends Service {
    private Log log = LogFactory.getLog(StoreRoutesService.class);

    private String jsonStr;

    public StoreRoutesService(String jsonStr) {
        this.jsonStr = jsonStr;
    }

    @Override
    /**
     * 将json数据保存为一个json文件
     */
    public String doService(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String status = null;
        String message = "";
        StringBuilder sb = new StringBuilder(message);
        String result = "";
        FileTools fileTools = new FileTools();
        //读取properties配置文件
        String path = "/file.properties";
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        Map<String, Object> map = new HashMap<>();
        try {
            linkedHashMap = FileTools.inputFile(path);
            String filePath = (String) linkedHashMap.get("file");
            log.info(filePath);
            List<List<String>> value = new ArrayList<>();
            JSONObject jsonObject = JSONObject.parseObject(jsonStr);
            String fileName = null;//文件名
            String content = null;//文件内容
            String fileId = null;//文件id
            if (jsonObject.get("fileName") != null) {
                fileName = jsonObject.get("fileName").toString();
                log.info("fileName----:"+fileName);
            } else {
                sb.append(" fileName is null; ");
            }
            if (jsonObject.get("content") != null) {
                content = jsonObject.get("content").toString();
                log.info("content----:"+content);
            } else {
                sb.append(" content is null; ");
            }
            if (jsonObject.get("fileId") != null) {
                fileId = jsonObject.get("fileId").toString();
                log.info("fileId----:"+fileId);
            } else {
                sb.append(" fileId is null; ");
            }
            if (null != fileName || null != fileId) {
                map = FileTools.createJsonFile(fileId, content, filePath, fileName);
                if (Boolean.valueOf(map.get("flag").toString())) {
                    status = "success";
                    result = "安保路线保存成功！";
                } else {
                    status = "fail";
                    result = "安保路线保存失败！";
                }
            } else {
                status = "fail";
                result = "安保路线保存失败！";
            }
        } catch (Exception e) {
            status = "exception";
            sb.append(e);
            log.error("StoreRoutesService",e);
            e.printStackTrace();
        }
        JSONObject jObject = new JSONObject();
        jObject.put("status", status);
        jObject.put("message", sb.toString());
        jObject.put("data", result);
        res.setContentType("application/json;charset=UTF-8");
        res.setHeader("Access-Control-Allow-Origin","*");
        PrintWriter out = res.getWriter();
        out.write(jObject.toString());
        return "StoreRoutesService finish";
    }
   /* 将数据保存到excel中
   public String doService(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String status = null;
        String message = "";
        StringBuilder sb = new StringBuilder(message);
        FileTools fileTools = new FileTools();
        //读取properties配置文件
        String path = "/file.properties";
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        try {
            linkedHashMap = FileTools.inputFile(path);
            String filePath = (String) linkedHashMap.get("file");
            List<List<String>> value = new ArrayList<>();
            JSONObject jsonObject = JSONObject.parseObject(jsonStr);
            String name = "";
            int sheetIndex = 0;
            if (jsonObject.get("cases") != null) {
                value = (List<List<String>>) jsonObject.get("cases");//前端传过来的数据
            } else {
                sb.append(" cases is null; ");
            }
            if (jsonObject.get("name") != null && jsonObject.get("name") != "") {
                name = jsonObject.get("name").toString();
            } else {
                sb.append(" name is null; ");
            }
            if (jsonObject.get("sheetIndex") != null && jsonObject.get("sheetIndex") != "") {
                sheetIndex = Integer.parseInt(jsonObject.get("sheetIndex").toString());
            } else {
                sb.append(" sheetIndex is null; ");
            }
            String[] titles = {"id", "name", "value"};
            fileTools.creatExcel(value, titles, name, filePath);
            //fileTools.addToExcel(filePath, sheetIndex);
            status = "success";
        } catch (Exception e) {
            status = "exception";
            log.info("StoreRoutesService" + e);
            sb.append(e);
            e.printStackTrace();
        }
        JSONObject jObject = new JSONObject();
        jObject.put("status", status);
        jObject.put("message", sb.toString());
        jObject.put("data", "");
        res.setContentType("application/json;charset=UTF-8");
        PrintWriter out = res.getWriter();
        out.write(jObject.toString());
        return "StoreRoutesService finish";
    }*/

}
