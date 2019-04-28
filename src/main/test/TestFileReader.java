import com.alibaba.fastjson.JSONObject;
import com.map.entity.Global;
import com.map.entity.Lonlat;
import com.map.svc.ServiceInitializer;
import com.map.utils.FileTools;
import com.map.utils.GeoCreatorByLtc;
import com.map.utils.GeoToolsUtils;
import org.junit.Test;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class TestFileReader {

    private FileTools fileTools = new FileTools();

    private GeoToolsUtils geoTools = new GeoToolsUtils();

    private GeoCreatorByLtc geoCreatorByLtc = GeoCreatorByLtc.getInstance();

    @Test
    public void testFile() throws Exception {
        GeoToolsUtils geoTools = new GeoToolsUtils();
        //String path = "E:\\shandong_1029\\shandong\\1009\\SD_POI_LEVEL\\SD_POI_LEVEL7_1009.shp";
        String path = "D://人口案件数据//案件//case.shp";
        // geoTools.readSHP(path, "GBK", null, null);
        geoTools.readSHP(path, "GBK", null, null);
    }

    @Test
    public void testJson() {
        List<Point> l = new ArrayList<>();
        Point p = geoCreatorByLtc.createPoint(12, 23, null);
        l.add(p);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("a", l);
        System.out.println("success");
    }

    @Test
    public void testSaveJsonFile() {
        //FileTools.createJsonFile("1","{\"name\":\"n1\",\"name1\":\"n1\",\"name2\":\"n1\"}", "d:/", "hah");
        //System.out.println("生成文件完成！");


    }

    @Test
    public void testReadBoundary() throws Exception {
        //读取properties配置文件
        String path = "/file.properties";
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        try {
            linkedHashMap = FileTools.inputFile(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String boundaryPath = (String) linkedHashMap.get("boundary");
        List<String> populationLst = Arrays.asList(new String[]{"city_id", "city_cid", "cname", "cename"});
        List<Geometry> polyonList = geoTools.readSHP(boundaryPath, "GBK", populationLst, null);
        System.out.print("list--------:" + polyonList);

        Map<String, Object> resultMap = new HashMap<String, Object>();
        for (int i = 0; i < polyonList.size(); i++) {
            Geometry obj = polyonList.get(i);
            System.out.println("obj------" + obj);
            Point point = geoCreatorByLtc.createPoint(119.35, 36.676, null);
            if (obj.contains(point)) {
                Map map = (Map) obj.getUserData();
                for (Object key : map.keySet()) {
                    System.out.println("cname-----:" + map.get("cname").toString() + "------" + "cename---------:" + map.get("cename").toString());
                    resultMap.put(map.get("city_id").toString(), map.get("cname").toString());
                }
            }
            System.out.println("resultMap-------------:" + resultMap);
        }
    }

    @Test
    public void testCaculate() {
//根据两点间的经纬度计算距离，单位：km
        String s = algorithm(38.81869622602193, 121.52926123038611, 34.91336775296051, 115.11873876946055);
        System.out.println("s-------:" + s);
    }

    private static double rad(double d) {
        return d * Math.PI / 180.00; //角度转换成弧度
    }

    /*
     * 根据经纬度计算两点之间的距离（单位米）
     * */
    public static String algorithm(double longitude1, double latitude1, double longitude2, double latitude2) {
        double Lat1 = rad(latitude1); // 纬度
        double Lat2 = rad(latitude2);
        double a = Lat1 - Lat2;//两点纬度之差
        double b = rad(longitude1) - rad(longitude2); //经度之差
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(Lat1) * Math.cos(Lat2) * Math.pow(Math.sin(b / 2), 2)));//计算两点距离的公式
        s = s * 6378137.0;//弧长乘地球半径（半径为米）
        s = Math.round(s * 10000d) / 10000d;//精确距离的数值
        s = s / 1000;//将单位转换为km，如果想得到以米为单位的数据 就不用除以1000
        //四舍五入 保留一位小数
        DecimalFormat df = new DecimalFormat("#.0");
        double area = (2 * (Math.cos(Math.asin(a / s)) * s - b) + 2 * b) * a / 2;
        return df.format(area);
    }

    @Test
    public void testTime() throws java.text.ParseException {
        String start = "2018-03";
        String end = "2019-09";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        String str1 = start.substring(0, 7);
        String str2 = end.substring(0, 7);
        Calendar bef = Calendar.getInstance();
        Calendar aft = Calendar.getInstance();
        bef.setTime(sdf.parse(str1));
        aft.setTime(sdf.parse(str2));
        int result = aft.get(Calendar.MONTH) - bef.get(Calendar.MONTH);
        int month = (aft.get(Calendar.YEAR) - bef.get(Calendar.YEAR)) * 12;
        System.out.println("result-------:" + Math.abs(month + result));

    }



    @Test
    public void testQuchong() {
        Set taskLst = new HashSet<>();
        Lonlat lonlat1 = new Lonlat();
        lonlat1.setLon(119.77676431065521);
        lonlat1.setLat(36.961852650201266);
        Lonlat lonlat2 = new Lonlat();
        lonlat2.setLon(119.77676431065521);
        lonlat2.setLat(36.961852650201266);
        Lonlat lonlat3 = new Lonlat();
        lonlat3.setLon(119.77676431065521);
        lonlat3.setLat(36.961852650201266);
        taskLst.add(lonlat1);
        taskLst.add(lonlat2);
        taskLst.add(lonlat3);
        System.out.println("taskLst------------:"+taskLst+"    size:-------"+taskLst.size());
    }

    @Test
    public void testHaha(){
        Map<Object,Object> map = new HashMap<Object,Object>();
        //读取properties配置文件
        String path = "/file.properties";
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        try {
            linkedHashMap = FileTools.inputFile(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        GeoToolsUtils geoTools = new GeoToolsUtils();
        //将人口数据存入缓存
        String populationPath = (String) linkedHashMap.get("population");
        List<String> populationLst = Arrays.asList(new String[]{"ADMINNAME", "ADDRESS", "residence", "age", "sex"});

        for (int i =0;i<100;i++){
            String innerUuid = UUID.randomUUID().toString();
            List<Point> list = geoTools.getPoints(populationPath, "GBK", populationLst);
            map.put(innerUuid, list);
        }
        long maxMemory = Runtime.getRuntime().maxMemory();
        long totalMemory = Runtime.getRuntime().totalMemory();
        long freeMemory = Runtime.getRuntime().freeMemory();
        long usableMemory = maxMemory + totalMemory + freeMemory;
        System.out.println("可以获得最大内存是："+maxMemory);
        System.out.println("已经分配到的内存大小是："+totalMemory);
        System.out.println("所分配内存的剩余大小是："+usableMemory);
    }


}
