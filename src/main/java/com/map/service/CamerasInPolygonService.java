package com.map.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.map.entity.Global;
import com.map.serviceImp.CameraNumServiceImpl;
import com.map.svc.Service;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.ParseException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 本类作为处理在一定范围（多边形或者圆形）内摄像头数据的接口
 */
public class CamerasInPolygonService extends Service {
    private Log log = LogFactory.getLog(CamerasInPolygonService.class);
    private String reqJsonStr;

    public CamerasInPolygonService(String reqJsonStr) {
        this.reqJsonStr = reqJsonStr;
    }

    @Override
    public String doService(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String status = null;
        String message = "";
        StringBuilder sb = new StringBuilder(message);
        JSONObject p = JSONObject.parseObject(reqJsonStr);
        String type = p.get("type").toString();
        CameraNumServiceImpl cameraNumServiceImpl = new CameraNumServiceImpl();
        List<double[]> cameraList = new ArrayList<double[]>();
        Polygon polygon = null;
        if (type.equals("polygon")) {//区域是多边形
            String polygonStr = "";
            Map pointsMap = (Map) p.get("points");//前端传过来的多边形顶点
            Map sw = (Map) pointsMap.get("_sw");//西南方向
            Map ne = (Map) pointsMap.get("_ne");//东北方向
            Object swLng = sw.get("lng");//经度
            Object swLat = sw.get("lat");//纬度
            Object neLng = ne.get("lng");
            Object neLat = ne.get("lat");
            if (!pointsMap.isEmpty()) {
                polygonStr = "POLYGON((" + swLng + " " + swLat + "," +
                        swLng + " " + neLat + "," +
                        neLng + " " + neLat + "," +
                        neLng + " " + swLat + "," +
                        swLng + " " + swLat + "))";
            } else {
                sb.append(" points is null; ");
            }
            try {
                polygon = Global.geoCreatorByLtc.createPolygonByWKT(polygonStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (type.equals("circle")) {//区域是圆形
            JSONArray array = (JSONArray) p.get("points");//前端传过来的多边形顶点
            double x = Double.parseDouble(array.get(0).toString());
            double y = Double.parseDouble(array.get(1).toString());
            double radius = Double.parseDouble(array.get(2).toString());
            polygon = Global.geoCreatorByLtc.createCircle(x, y, radius);
        }
        try {
            cameraList = cameraNumServiceImpl.getCameraInPolygon(polygon, Global.cameraNumLst);
            status = "success";
        } catch (Exception e) {
            status = "exception";
            sb.append(e);
            log.error("CamerasInPolygonService",e);
            e.printStackTrace();
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status", status);
        jsonObject.put("message", sb.toString());
        jsonObject.put("data", "");
        res.setContentType("application/json;charset=UTF-8");
        res.setHeader("Access-Control-Allow-Origin","*");
        PrintWriter out = res.getWriter();
        out.write(jsonObject.toString());
        return "CamerasInPolygonService finish";
    }

}
