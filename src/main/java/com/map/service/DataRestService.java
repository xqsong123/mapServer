package com.map.service;

import com.alibaba.fastjson.JSONObject;
import com.map.entity.Global;
import com.map.serviceImp.DataRestServiceImpl;
import com.map.svc.Service;
import com.map.utils.FileTools;
import com.map.utils.GeoToolsUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

public class DataRestService extends Service {

    private Log log = LogFactory.getLog(DataRestService.class);

    private String reqJsonStr;

    public DataRestService(String reqJsonStr) {
        this.reqJsonStr = reqJsonStr;
    }

    @Override
    public String doService(HttpServletRequest req, HttpServletResponse res) throws IOException {
        //get请求
        /*
        String query = req.getQueryString();
        if(!StringUtils.isBlank(query)){
            //查看具体要初始化的数据，query: dataRest=road
            String detail = query.split("=")[1];
            //读取properties配置文件
            String path = "/file.properties";
            LinkedHashMap linkedHashMap = new LinkedHashMap();
            try {
                linkedHashMap = FileTools.inputFile(path);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(detail.equals("road")){
                String rootPath = (String) linkedHashMap.get("road");
                Global.roadMap.clear();
                DataRestServiceImpl.resetRoadData(rootPath);
            }
        }*/
        //post请求
        //读取properties配置文件
        String path = "/file.properties";
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        try {
            linkedHashMap = FileTools.inputFile(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        GeoToolsUtils geoTools = new GeoToolsUtils();
        JSONObject jsonObj = JSONObject.parseObject(reqJsonStr);
        if (null != jsonObj) {
            if (null != jsonObj.get("dataRest")) {
                if (jsonObj.get("dataRest").toString().equals("road")) {
                    String rootPath = (String) linkedHashMap.get("road");
                    Global.roadMap.clear();
                    DataRestServiceImpl.resetRoadData(rootPath);
                } else if (jsonObj.get("dataRest").toString().equals("population")) {
                    String populationPath = (String) linkedHashMap.get("population");
                    Global.popLst.clear();
                    List<String> populationLst = Arrays.asList(new String[]{"ADMINNAME", "ADDRESS", "residence", "age", "sex"});
                    Global.popLst = geoTools.getPoints(populationPath, "GBK", populationLst);
                } else if (jsonObj.get("dataRest").toString().equals("case")) {
                    String casePath = (String) linkedHashMap.get("case");
                    Global.caseLst.clear();
                    List<String> caseLst = Arrays.asList(new String[]{"ADMINNAME", "ADDRESS", "level"});
                    Global.caseLst = geoTools.getPoints(casePath, "GBK", caseLst);
                } else if (jsonObj.get("dataRest").toString().equals("boundary")) {
                    String boundaryPath = (String) linkedHashMap.get("boundary");
                    Global.cityLst.clear();
                    List<String> cityLst = Arrays.asList(new String[]{"city_id", "city_cid", "cname", "cename"});
                    try {
                        Global.cityLst = geoTools.readSHP(boundaryPath, "GBK", cityLst, null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else if (jsonObj.get("dataRest").toString().equals("policeDaily")) {
                    String policeDailyPath = (String) linkedHashMap.get("policeDaily");
                    Global.policeDailyLst.clear();
                    List<String> dailyLst = Arrays.asList(new String[]{"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "MJRC"});
                    Global.policeDailyLst = geoTools.getPoints(policeDailyPath, "GBK", dailyLst);
                } else if (jsonObj.get("dataRest").toString().equals("cameraPath")) {
                    String cameraPath = (String) linkedHashMap.get("cameraPath");
                    Global.cameraNumLst.clear();
                    Global.cameraNumLst = geoTools.getPoints(cameraPath, "GBK", null);
                } else if (jsonObj.get("dataRest").toString().equals("alarmPath")) {
                    String alarmPath = (String) linkedHashMap.get("alarmPath");
                    Global.alarmDataLst.clear();
                    List<String> alarmLst = Arrays.asList(new String[]{"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "BJSJ"});
                    Global.alarmDataLst = geoTools.getPoints(alarmPath, "GBK", alarmLst);
                }

            }

        }

        return "Data Reset Success";
    }

}
