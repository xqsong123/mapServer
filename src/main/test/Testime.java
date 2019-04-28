import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;

import java.io.File;
import java.nio.charset.Charset;
import java.util.*;

import static com.map.service.RoutePlanningService.vividToLocationGeometry;

public class Testime {
    public static void main(String[] args){
        List<Geometry> list = new LinkedList<>();
        List<Geometry> geometryList = new LinkedList<>();
        String path = "D:\\SD_POI\\SD_POI.shp";
        File file = new File(path);
        try {
            ShapefileDataStoreFactory shapefileDataStoreFactory = new ShapefileDataStoreFactory();
            ShapefileDataStore shapefileDataStore = (ShapefileDataStore) shapefileDataStoreFactory.createDataStore(file.toURI().toURL());
            shapefileDataStore.setCharset(Charset.forName("GBK"));
            SimpleFeatureSource simpleFeatureSource = shapefileDataStore.getFeatureSource();
            SimpleFeatureCollection simpleFeatureCollection = simpleFeatureSource.getFeatures();
            SimpleFeatureIterator simpleFeatureIterator = simpleFeatureCollection.features();
            while (simpleFeatureIterator.hasNext()) {
                SimpleFeature simpleFeature = simpleFeatureIterator.next();
                com.vividsolutions.jts.geom.Geometry geometry = (com.vividsolutions.jts.geom.Geometry) simpleFeature.getDefaultGeometry();
                Map<String, Object> userData = new HashMap<>();
                Collection<Property> propertyCollection = simpleFeature.getProperties();
                Iterator<Property> propertyIterator = propertyCollection.iterator();
                while (propertyIterator.hasNext()) {
                    Property property = propertyIterator.next();
                    String name = property.getName().toString();
                    if (!"the_geom".equals(name)) {
                        userData.put(name, simpleFeature.getAttribute(name));
                    }
                }
                geometry.setUserData(userData);
                geometryList.add(vividToLocationGeometry(geometry));
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        System.out.println(geometryList.size());
        for (int i = 0 ; i < 120; i++){
            list.addAll(geometryList);
        }
        System.out.println("list的长度大小：-----"+list.size());
        int count = 0;
        List<Geometry> result = new LinkedList<>();
        long start = System.currentTimeMillis();
        Envelope envelope = new Envelope(116.97805, 117.17805, 36.5779, 36.7779);
        for (Geometry geometry : list) {
            count ++;
            if (envelope.contains(geometry.getInteriorPoint().getCoordinate()) || envelope.intersects(geometry.getInteriorPoint().getCoordinate())){
                result.add(geometry);
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("循环：" + count + "次");
        System.out.println("耗时：" + (end - start)/1000d + "秒");
    }
}
