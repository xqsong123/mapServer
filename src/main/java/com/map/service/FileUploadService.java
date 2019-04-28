package com.map.service;

import com.alibaba.fastjson.JSONObject;
import com.map.serviceImp.FileUploadServiceImpl;
import com.map.svc.Service;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

/**
 * 本类作为处理文件及图片上传的接口
 */
public class FileUploadService extends Service {

    private Log log = LogFactory.getLog(FileUploadService.class);

    private List items;

    public FileUploadService(List items) {
        this.items = items;
    }

    @Override
    public String doService(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String uploadFilePath = req.getSession().getServletContext().getRealPath("/file");//获取文件的绝对路径
        //String uploadFilePath = "/data/webapps/files/mapServer/images";
        FileUploadServiceImpl fileUploadServiceImpl = new FileUploadServiceImpl();
        Map<String, Object> map = fileUploadServiceImpl.UploadFile(items, uploadFilePath);
        System.out.println("map----------:"+map);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status", map.get("successMsg"));
        jsonObject.put("message", map.get("errorMsg"));
        jsonObject.put("data", map.get("filesPath"));
        res.setContentType("application/json;charset=UTF-8");
        res.setHeader("Access-Control-Allow-Origin","*");
        PrintWriter out = res.getWriter();
        out.write(jsonObject.toString());
        return "FileUploadService finish";
    }
}
