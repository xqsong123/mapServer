package com.map.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.map.svc.Service;
import com.map.utils.GeoToolsUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * 将安保路线划分成更密的点集合的接口
 */
public class DivideDistanceService extends Service {
    private Log log = LogFactory.getLog(DivideDistanceService.class);

    private String jsonStr;

    public DivideDistanceService(String jsonStr) {
        this.jsonStr = jsonStr;
    }

    @Override
    public String doService(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String status = null;
        String message = "";
        StringBuilder sb = new StringBuilder(message);
        JSONObject p = JSONObject.parseObject(jsonStr);
        JSONArray array = (JSONArray) p.get("coordinates");//前端传过来的安保路线点数组
        log.info("DivideDistanceService前端传参：" + array);
        GeometryFactory geometryFactory = new GeometryFactory();
        List<Coordinate> pointsList = new ArrayList<Coordinate>();
        List<Coordinate> newPointsList = new ArrayList<Coordinate>();
        if (!array.isEmpty()) {
            Coordinate[] coords = new Coordinate[array.size()];
            List<Coordinate> coorLst = new ArrayList<Coordinate>();
            for (int j = 0; j < array.size(); j++) {
                JSONArray jsonarr = (JSONArray) array.get(j);
                double a = Double.parseDouble(jsonarr.get(0).toString());//经度
                double b = Double.parseDouble(jsonarr.get(1).toString());//纬度
                coorLst.add(new Coordinate(a, b));
            }
            coorLst.toArray(coords);
            log.info("coords--------:" + coords);
            System.out.println("coords--------:" + coords);
            //Coordinate[] coords = new Coordinate[]{new Coordinate(Double.parseDouble(array.get(0).toString()), Double.parseDouble(array.get(1).toString())), new Coordinate(Double.parseDouble(array.get(2).toString()), Double.parseDouble(array.get(3).toString()))};
            LineString l = geometryFactory.createLineString(coords);
            pointsList = GeoToolsUtils.divideDistance(l);
            if (pointsList.size() > 500) {
                for (int i = 0; i < pointsList.size(); i = i + (pointsList.size() / 500 + 1)) {
                    newPointsList.add(pointsList.get(i));
                }
            }
            log.info("newPointsList-----:" + newPointsList);
        } else {
            sb.append(" points is null; ");
        }
        status = "success";
        JSONObject jObject = new JSONObject();
        jObject.put("status", status);
        jObject.put("message", sb.toString());
        jObject.put("data", newPointsList);
        res.setContentType("application/json;charset=UTF-8");
        res.setHeader("Access-Control-Allow-Origin", "*");
        PrintWriter out = res.getWriter();
        out.write(jObject.toString());
        return "DivideDistanceService finish";

    }
}
