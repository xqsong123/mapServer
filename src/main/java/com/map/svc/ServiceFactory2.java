package com.map.svc;

import com.map.service.FileUploadService;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class ServiceFactory2 {
    private static Log log = LogFactory.getLog(ServiceFactory.class);

    public static Service getService(HttpServletRequest req) {
        DiskFileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        //设置上传单个文件的大小的最大值，目前是设置为1024*1024字节，也就是1MB
        //upload.setFileSizeMax(1024*1024*10);
        //设置上传文件总量的最大值，最大值=同时上传的多个文件的大小的最大值的和，目前设置为10MB
        //upload.setSizeMax(1024*1024*30);
        //设置工厂的缓冲区的大小，当上传的文件大小超过缓冲区的大小时，就会生成一个临时文件存放到指定的临时目录当中。
        factory.setSizeThreshold(1024 * 1024);
        List items = null;
        try {
            items = upload.parseRequest(req);
        } catch (FileUploadException e) {
            e.printStackTrace();
        }
        return new FileUploadService(items);
    }
}
