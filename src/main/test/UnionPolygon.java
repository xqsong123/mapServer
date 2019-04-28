import com.map.entity.Global;
import com.map.utils.FileTools;
import com.map.utils.GeoToolsUtils;
import org.junit.Test;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;

import java.util.*;

public class UnionPolygon {
    private GeoToolsUtils geoTools = new GeoToolsUtils();

    @Test
    public void testPologon() throws Exception {
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
        Geometry allunion = polyonList.get(0);
        for (int i = 1; i < polyonList.size(); i++) {
            allunion = allunion.union(polyonList.get(i));
            System.out.println("allunion------" + allunion);
        }
        Envelope envelope = new Envelope(113.761550,124.308425,34.101297,38.392003);
        envelope.equals(allunion);
        System.out.println(envelope.equals(allunion));
    }
}
