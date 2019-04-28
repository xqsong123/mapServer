package com.map.service;

import com.alibaba.fastjson.JSONObject;
import com.map.entity.Global;
import com.map.serviceImp.CameraNumServiceImpl;
import com.map.svc.Service;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 本类作为处理在一定范围内摄像头数据
 */
public class CameraNumService extends Service {
    private Log log = LogFactory.getLog(CameraNumService.class);

    private String reqJsonStr;

    public CameraNumService(String reqJsonStr) {
        this.reqJsonStr = reqJsonStr;
    }

    @Override
    public String doService(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String status = null;
        String message = "";
        StringBuilder sb = new StringBuilder(message);
        JSONObject p = JSONObject.parseObject(reqJsonStr);
        //JSONArray array = (JSONArray) p.get("points");//前端传过来的多边形顶点
        String polygonStr = "";
        Map pointsMap = (Map) p.get("points");//前端传过来的多边形顶点
        log.info("CameraNumService请求参数："+pointsMap);
        Map sw = (Map) pointsMap.get("_sw");//西南方向
        Map ne = (Map) pointsMap.get("_ne");//东北方向
        Object swLng = sw.get("lng");//经度
        Object swLat = sw.get("lat");//纬度
        Object neLng = ne.get("lng");
        Object neLat = ne.get("lat");
      /*  if (array != null) {
            polygonStr = "POLYGON((" + array.get(3) + " " + array.get(0) + "," +
                    array.get(1) + " " + array.get(0) + "," +
                    array.get(1) + " " + array.get(2) + "," +
                    array.get(3) + " " + array.get(2) + "," +
                    array.get(3) + " " + array.get(0) + "))";
        } else {
            sb.append(" points is null; ");
        }*/
        if (!pointsMap.isEmpty()) {
            polygonStr = "POLYGON((" + swLng + " " + swLat + "," +
                    swLng + " " + neLat + "," +
                    neLng + " " + neLat + "," +
                    neLng + " " + swLat + "," +
                    swLng + " " + swLat + "))";
        } else {
            sb.append(" points is null; ");
        }
        CameraNumServiceImpl cameraNumServiceImpl = new CameraNumServiceImpl();
        List<double[]> cameraList = new ArrayList<double[]>();
        try {
            cameraList = cameraNumServiceImpl.getCameraNums(polygonStr, Global.cameraNumLst);
            log.info(cameraList);
            System.out.println("cameraList-----:" + cameraList);
            status = "success";
        } catch (Exception e) {
            status = "exception";
            sb.append(e);
            log.error("CameraNumService",e);
            e.printStackTrace();
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status", status);
        jsonObject.put("message", sb.toString());
        jsonObject.put("data", cameraList);
        res.setContentType("application/json;charset=UTF-8");
        res.setHeader("Access-Control-Allow-Origin","*");
        PrintWriter out = res.getWriter();
        out.write(jsonObject.toString());
        return "CameraNumService finish";
    }

}
