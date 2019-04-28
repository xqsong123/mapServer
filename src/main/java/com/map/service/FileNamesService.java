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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class FileNamesService extends Service {
    private Log log = LogFactory.getLog(FileNamesService.class);

    /**
     * 获取指定文件夹下的所有文件名接口
     */
    @Override
    public String doService(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String status = null;
        String message = "";
        StringBuilder sb = new StringBuilder(message);
        //读取properties配置文件
        String path = "/file.properties";
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        List<String> nameLst = new ArrayList<String>();
        try {
            linkedHashMap = FileTools.inputFile(path);
            String filePath = (String) linkedHashMap.get("file");
            String[] fileNames = FileTools.getFileName(filePath);
            log.info("fileNames-------:" + fileNames);
            System.out.println("fileNames-------:" + fileNames);
            for (String name : fileNames) {
                int flag = name.lastIndexOf(".");
                String fileName = name.substring(0, flag);
                nameLst.add(fileName);
            }
            status = "success";
            log.info("nameLst----:" + nameLst);
        } catch (Exception e) {
            status = "exception";
            sb.append(e);
            log.error("FileNamesService",e);
            e.printStackTrace();
        }
        JSONObject jObject = new JSONObject();
        jObject.put("status", status);
        jObject.put("message", sb.toString());
        jObject.put("data", nameLst);
        res.setContentType("application/json;charset=UTF-8");
        res.setHeader("Access-Control-Allow-Origin", "*");
        PrintWriter out = res.getWriter();
        out.write(jObject.toString());
        return "FileNamesService finish";
    }
}
