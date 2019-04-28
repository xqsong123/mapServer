import com.map.entity.Global;
import com.map.utils.FileTools;
import com.map.utils.GeoCreatorByLtc;
import com.map.utils.GeoToolsUtils;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

import java.util.*;

public class Testhah {
    public static void main(String[] args) {
        /*Map<Object,Object> map = new HashMap<Object,Object>();
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
        String populationPath = (String) linkedHashMap.get("poiPath");
        //List<String> populationLst = Arrays.asList(new String[]{"ADMINNAME", "ADDRESS", "residence", "age", "sex"});
        List<Point> list = geoTools.getPoints(populationPath, "GBK",null );//读取数据

        Point p = GeometryFactory.createPointFromInternalCoord(new Coordinate(123, 45), null);
        Map m = new HashMap();
        m.put("name", "123");
        m.put("asd", 24);
        m.put("akjdsfh", 56);
        m.put("dsgf",536);
        p.setUserData(m);

        System.out.println("前可以获得最大内存是："+Runtime.getRuntime().maxMemory());
        System.out.println("前已经分配到的内存大小是："+Runtime.getRuntime().totalMemory());
        System.out.println("前所分配内存的剩余大小是："+Runtime.getRuntime().freeMemory());*/

        List<Point> plst = new ArrayList<>();
        //GeoCreatorByLtc gc = new GeoCreatorByLtc();
        for (int i =0;i<1000000;i++){
            /*String innerUuid = UUID.randomUUID().toString();
            map.put(innerUuid, list);*/
            Point p = Global.geoCreatorByLtc.createPoint(21, 54, null);
            Map m = new HashMap();
            m.put("name", "123");
            m.put("asd", 24);
            m.put("akjdsfh", 56);
            m.put("dsgf",536);
            p.setUserData(m);
            plst.add(p);
        }
        System.out.println(plst.size());

        System.out.println("后可以获得最大内存是："+Runtime.getRuntime().maxMemory());
        System.out.println("后已经分配到的内存大小是："+Runtime.getRuntime().totalMemory());
        System.out.println("后所分配内存的剩余大小是："+Runtime.getRuntime().freeMemory());


    }
}
