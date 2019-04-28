package com.map.serviceImp;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class FileUploadServiceImpl {
    private Log log = LogFactory.getLog(FileUploadServiceImpl.class);

    //上传文件
    public Map<String, Object> UploadFile(List items, String uploadFilePath) throws IOException {
        String filename = null;
        List<String> filesPath = new ArrayList<>();
        Map<String, Object> info = new HashMap<>();
        Iterator iter = items.iterator();
        String[] pics = {"png", "jpg", "jpeg", "bmp"};
        String successMsg = "";
        StringBuilder sb = new StringBuilder(successMsg);
        String errorMsg = "";
        while (iter.hasNext()) {
            FileItem item = (FileItem) iter.next();
            //item.isFormField()为false即为上传文件，如果为true则是普通类型
            if (!item.isFormField()) {
                //唯一标识数字
                UUID uuid = UUID.randomUUID();
                if (item.getName() == null || item.getName().trim().equals("")) {
                    continue;
                }
                int pos = item.getName().lastIndexOf("."); // 取文件的格式
                if (!Arrays.asList(pics).contains(item.getName().substring(pos + 1))) {
                    errorMsg += "Name-----:" + item.getFieldName() + "---" + "FileName----:" + item.getName() + "文件上传失败，文件格式不对！";

                } else {
                    filename = uuid + item.getName().substring(pos);
                    File f = new File(uploadFilePath);
                    if (!f.exists()) {
                        f.mkdir();//生成目录
                    }
                    String imgsrc = f + "\\" + filename;
                    String imgsrc1 = "http://192.168.8.111:8080/mapServer/file" + "/" + filename;
                    filesPath.add(imgsrc1);
                    // 复制文件
                    InputStream is = item.getInputStream();
                    FileOutputStream fos = new FileOutputStream(imgsrc);
                    byte b[] = new byte[1024 * 1024];
                    int length = 0;
                    while (-1 != (length = is.read(b))) {
                        fos.write(b, 0, length);
                    }
                    sb.append(item.getName() + "上传成功！\n");
                    fos.flush();
                    fos.close();
                }

            } else {
                String value = item.getString();
                value = new String(value.getBytes("ISO-8859-1"), "UTF-8");
            }
        }
        info.put("errorMsg", errorMsg);
        info.put("successMsg", successMsg);
        info.put("filesPath", filesPath);
        return info;
    }
}
