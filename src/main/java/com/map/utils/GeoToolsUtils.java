package com.map.utils;

import com.map.entity.Global;
import com.map.entity.ShpGeometry;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geotools.data.DataStore;
import org.geotools.data.FeatureSource;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
import org.geotools.swing.JMapFrame;
import org.geotools.swing.data.JFileDataStoreChooser;
import org.locationtech.jts.algorithm.distance.DistanceToPoint;
import org.locationtech.jts.algorithm.distance.PointPairDistance;
import org.locationtech.jts.geom.*;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.filter.Filter;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.*;
import java.util.*;

public class GeoToolsUtils {

    private static Log log = LogFactory.getLog(GeoToolsUtils.class);

    static Connection connection = null;
    static DataStore pgDatastore = null;
    @SuppressWarnings("rawtypes")
    static FeatureSource fSource = null;
    static Statement statement = null;
    static GeometryFactory geometryFactory = new GeometryFactory();

    /**
     * 1.连接postgrepsql数据库
     *
     * @param ip
     * @param port
     * @param user
     * @param password
     * @param database
     * @return
     * @throws Exception
     */
    private static boolean connDataBase(String ip, Integer port, String user, String password, String database)
            throws Exception {

        // "jdbc:postgresql://192.168.1.104:5432/test"
        // user=postgres
        // password=bluethink134

        // 拼接url
        String url = "jdbc:postgresql://" + ip + ":" + port + "/" + database;
        Class.forName("org.postgresql.Driver"); // 一定要注意和上面的MySQL语法不同
        connection = DriverManager.getConnection(url, user, password);
        if (connection != null) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 利用GeoTools工具包，打开一张shapfile文件，并显示
     *
     * @throws Exception
     */
    public static void openShpFile() throws Exception {

        // 1.数据源选择 shp扩展类型的
        File file = JFileDataStoreChooser.showOpenFile("shp", null);
        if (file == null) {
            return;
        }

        // 2.得到打开的文件的数据源
        FileDataStore store = FileDataStoreFinder.getDataStore(file);

        // 3.设置数据源的编码，防止中文乱码
        ((ShapefileDataStore) store).setCharset(Charset.forName("UTF-8"));

        /**
         * 使用FeatureSource管理要素数据 使用Style（SLD）管理样式 使用Layer管理显示
         * 使用MapContent管理所有地图相关信息
         */

        // 4.以java对象的方式访问地理信息
        // 简单地理要素
        SimpleFeatureSource featureSource = store.getFeatureSource();

        // 5.创建映射内容，并将我们的shapfile添加进去
        MapContent mapContent = new MapContent();

        // 6.设置容器的标题
        mapContent.setTitle("Appleyk's GeoTools");

        // 7.创建简单样式
        Style style = SLD.createSimpleStyle(featureSource.getSchema());

        // 8.显示【shapfile地理信息+样式】
        Layer layer = new FeatureLayer(featureSource, style);

        // 9.将显示添加进map容器
        mapContent.addLayer(layer);

        // 10.窗体打开，高大尚的操作开始
        JMapFrame.showMap(mapContent);

    }

    /**
     * 获取shp文件中的属性,list中第一个一般是几何属性
     */
    public List<String> getShpProps(String path) {

        log.info("获取shp文件中的属性字段");

        if (StringUtils.isBlank(path)) {
            log.info("shp文件路径为空");
            return null;
        }
        List<String> propLst = new ArrayList<>();

        ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();
        ShapefileDataStore sds = null;
        try {
            sds = (ShapefileDataStore) dataStoreFactory.createDataStore(new File(path).toURI().toURL());
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<AttributeDescriptor> attrList = null;
        try {
            attrList = sds.getFeatureSource().getSchema()
                    .getAttributeDescriptors();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (attrList != null && attrList.size() > 0) {
            for (int i = 1; i < attrList.size(); i++) {
                propLst.add(attrList.get(i).getName().toString());
            }
        }
        /*String name = attrList.get(1).getName().toString();
        Class cl = attrList.get(1).getType().getBinding();*/
        return propLst;

    }

    /**
     * 读取shp文件
     *
     * @param path，文件路径
     * @param unicode，编码格式
     * @param propLst，存储shp文件属性的列表
     * @return
     * @throws Exception
     */
  /*  public List<Geometry> readSHP(String path, String unicode, List<String> propLst) throws Exception {

        if(StringUtils.isBlank(path)){
            log.info("读取shp文件路径为空");
            return null;
        }
        List<Geometry> gLst = new ArrayList<>();
        ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();
        try {
            ShapefileDataStore sds = (ShapefileDataStore)dataStoreFactory.createDataStore(new File(path).toURI().toURL());
            if(unicode.equals(null)){
                sds.setCharset(Charset.forName("GBK"));
            }else{
                sds.setCharset(Charset.forName(unicode));
            }
            SimpleFeatureSource featureSource = sds.getFeatureSource();
            SimpleFeatureIterator itertor = featureSource.getFeatures().features();

            while(itertor.hasNext()) {
                SimpleFeature feature = itertor.next();
                List<Object> list = feature.getAttributes();
                if(list != null && list.size() > 0){
                    Geometry geo = (Geometry) list.get(0);
                    if(propLst != null && propLst.size() > 0){
                        Map attrMap = new HashMap();
                        for(int i = 0; i < propLst.size(); i++){
                            attrMap.put(propLst.get(i), list.get(i+1));
                        }
                        geo.setUserData(attrMap);
                    }
                    gLst.add(geo);
                }

            }
            itertor.close();
            sds.dispose();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return gLst;
    }*/
    public List<Geometry> readSHP(String path, String unicode, List<String> propLst, String filterText) throws Exception {

        if (StringUtils.isBlank(path)) {
            log.info("读取shp文件路径为空");
            return null;
        }
        log.info("读取文件：" + path);
        List<Geometry> gLst = new ArrayList<>();
        ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();
        try {
            ShapefileDataStore sds = (ShapefileDataStore) dataStoreFactory.createDataStore(new File(path).toURI().toURL());
            if (unicode == null) {
                sds.setCharset(Charset.forName("GBK"));
            } else {
                sds.setCharset(Charset.forName(unicode));
            }
            SimpleFeatureSource featureSource = sds.getFeatureSource();
            SimpleFeatureIterator itertor = null;
            Filter filter = null;
            if (!StringUtils.isBlank(filterText)) {
                //filterText 例子："BBOX(the_geom, 117, 25, 120, 40)"，"NAME == '济南市'"
                filter = CQL.toFilter(filterText);
                SimpleFeatureCollection features = featureSource.getFeatures(filter);
                itertor = features.features();
            } else {
                itertor = featureSource.getFeatures().features();
            }

            List<AttributeDescriptor> attrList = featureSource.getSchema()
                    .getAttributeDescriptors();
            String type = attrList.get(0).getType().getName().toString();

            while (itertor.hasNext()) {
                SimpleFeature feature = itertor.next();
                List<Object> list = feature.getAttributes();
                if (list != null && list.size() > 0) {
                    if (list.get(0) != null) {
                        String geoString = list.get(0).toString();
                        Geometry geo = null;
                        if (type.equals("Point")) {
                            geo = Global.geoCreatorByLtc.createPointByWKT(geoString, null);
                        }
                        if (type.equals("MultiPoint")) {
                            geo = Global.geoCreatorByLtc.createMulPointByWKT(geoString);
                        }
                        if (type.equals("LineString")) {
                            geo = Global.geoCreatorByLtc.createLineByWKT(geoString);
                        }
                        if (type.equals("MultiLineString")) {
                            geo = Global.geoCreatorByLtc.createMLineByWKT(geoString);
                        }
                        if (type.equals("Polygon")) {
                            geo = Global.geoCreatorByLtc.createPolygonByWKT(geoString);
                        }
                        if (type.equals("MultiPolygon")) {
                            geo = Global.geoCreatorByLtc.createMulPolygonByWKT(geoString);
                        }

                        if (geo != null && propLst != null && propLst.size() > 0) {
                            Map attrMap = new HashMap();
                            for (int i = 0; i < propLst.size(); i++) {
                                attrMap.put(propLst.get(i), list.get(i + 1));
                            }
                            geo.setUserData(attrMap);
                        }
                        gLst.add(geo);
                    }
                }
            }
            itertor.close();
            sds.dispose();


        } catch (Exception e) {
            e.printStackTrace();
        }
        return gLst;
    }

  /*  public List<Point> getPoints(String path, String unicode, List<String> propLst){

        List<Point> pLst = new ArrayList<>();
        try {
            List<Geometry> gLst = this.readSHP(path, unicode, propLst);
            if(gLst != null && gLst.size() > 0){
                for(Geometry geometry : gLst){
                    Point p = (Point) geometry;
                    pLst.add(p);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pLst;
    }*/

    public List<Point> getPoints(String path, String unicode, List<String> propLst) {

        List<Point> pLst = new ArrayList<>();
        try {
            List<Geometry> gLst = this.readSHP(path, unicode, propLst, null);
            if (gLst != null && gLst.size() > 0) {
                for (Geometry geometry : gLst) {
                    Point p = (Point) geometry;
                    pLst.add(p);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pLst;
    }

    /**
     * 获取几何图形与矩形的交集的交集
     *
     * @return
     */
    public List<Geometry> getInsertGeometry(List<Geometry> geometryList, Polygon p) {

        log.info("获取几何图形与矩形的交集的交集");
        if (geometryList == null || geometryList.size() == 0 || p == null) {
            return null;
        }
        List<Geometry> geoLst = new ArrayList<>();
        for (Geometry geometry : geometryList) {
            Geometry g = geometry.intersection(p);
            Map userData = (Map) geometry.getUserData();
            if (!g.isEmpty()) {
                g.setUserData(userData);
                geoLst.add(g);
            }
        }
        return geoLst;
    }

    /**
     * 存储shp文件的数据 == 文件内容映射成Java实体类存储在postgresql数据库中
     *
     * @param geo
     * @return
     * @throws Exception
     */
    public static boolean shpSave(ShpGeometry geo) throws Exception {

        boolean result = false;
        String sql = "insert into geotable (osm_id,code,fclass,name,type,geom) values('" + geo.getOsm_id() + "','"
                + geo.getCode() + "','" + geo.getFclass() + "','" + geo.getName() + "','" + geo.getType() + "',"
                + "st_geomfromewkt('" + geo.getGeom().toString() + "'))";

        PreparedStatement pstmt;
        pstmt = connection.prepareStatement(sql);

        // geometry = st_geomfromewkt(text WKT) ==
        // 对应postgresql中的几何WKT文本描述转换为几何数据

        System.out.println(sql);
        int i = pstmt.executeUpdate();
        if (i > 0) {
            result = true;
        }

        pstmt.close();
        return result;
    }

    /**
     * 获取POSTGIS中所有的地理图层
     *
     * @throws Exception
     */
    public static void getAllLayers() throws Exception {
        String[] typeName = pgDatastore.getTypeNames();
        for (int i = 0; i < typeName.length; i++) {
            System.out.println((i + 1) + ":" + typeName[i]);
        }
    }

    /**
     * 针对某个地理图层[相当于table表名字]，进行地理信息的读取
     *
     * @param Schema
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public static void postGisReading(String Schema) throws Exception {

        fSource = pgDatastore.getFeatureSource(Schema);
        // 1.一个用于处理FeatureCollection的实用工具类。提供一个获取FeatureCollection实例的机制
        FeatureCollection<SimpleFeatureType, SimpleFeature> result = fSource.getFeatures();

        // 2.计算本图层中所有特征的数量
        System.out.println("特征totalCount = " + result.size());

        // 3.迭代特征
        FeatureIterator<SimpleFeature> iterator = result.features();

        // 4.迭代特征 只迭代30个 太大了，一下子迭代完，非常耗时
        int stop = 0;
        while (iterator.hasNext()) {

            if (stop > 30) {
                break;
            }

            SimpleFeature feature = iterator.next();
            Collection<Property> p = feature.getProperties();
            Iterator<Property> it = p.iterator();

            // 5.特征里面的属性再迭代,属性里面有字段
            System.out.println("================================");
            while (it.hasNext()) {
                Property pro = it.next();
                System.out.println(pro.getName() + "\t = " + pro.getValue());
            } // end 里层while
            stop++;
        } // end 最外层 while
        iterator.close();
    }

    /**
     * 根据几何对象名称 查询几何对象信息 [Query]
     *
     * @param name
     * @throws Exception
     */
    public static void Query(String name) throws Exception {

        //String sql = "select st_astext(geom) from geotable where name ='"+name+"'";
        String sql = "select  geometrytype(geom) as type,st_astext(geom) as geom from geotable where name ='" + name + "'";
        statement = connection.createStatement();
        ResultSet result = statement.executeQuery(sql);
        if (result != null) {
            while (result.next()) {
                Object val = result.getString(1);
                if (val.equals("MULTIPOLYGON")) {
                    System.out.println("几何对象类型：多多边形");
                    org.locationtech.jts.geom.MultiPolygon mPolygon = Global.geoCreatorByLtc.createMulPolygonByWKT(result.getString(2));
                    System.out.println(mPolygon instanceof MultiPolygon);
                    System.out.println("获取几何对象中的点个数：" + mPolygon.getNumPoints());
                }

            }
        }
    }

    /**
     * 将几何对象信息写入一个shapfile文件并读取
     *
     * @throws Exception
     */
    public static void writeSHP(String path, Geometry geometry) throws Exception {

        //String path="C:\\my.shp";

        // 1.创建shape文件对象
       /* File file = new File(path);

        Map<String, Serializable> params = new HashMap<String, Serializable>();

        // 2.用于捕获参数需求的数据类
        //URLP:url to the .shp file.
        params.put(ShapefileDataStoreFactory.URLP.key, file.toURI().toURL());

        // 3.创建一个新的数据存储——对于一个还不存在的文件。
        ShapefileDataStore ds = (ShapefileDataStore) new ShapefileDataStoreFactory().createNewDataStore(params);


        // 4.定义图形信息和属性信息
        //SimpleFeatureTypeBuilder 构造简单特性类型的构造器
        SimpleFeatureTypeBuilder tBuilder = new SimpleFeatureTypeBuilder();

        // 5.设置
        //WGS84:一个二维地理坐标参考系统，使用WGS84数据
        tBuilder.setCRS(DefaultGeographicCRS.WGS84);
        tBuilder.setName("shapefile");

        // 6.添加 一个多多边形  ==  这里也可以当做参数传过来，具体情况具体对待
        tBuilder.add("the_geom", MultiPolygon.class);
        // 7.添加一个id
        tBuilder.add("osm_id", Long.class);
        // 8.添加名称
        tBuilder.add("name", String.class);

        // 9.添加描述
        tBuilder.add("des", String.class);


        // 10.设置此数据存储的特征类型
        ds.createSchema(tBuilder.buildFeatureType());

        // 11.设置编码
        ds.setCharset(Charset.forName("UTF-8"));


        // 12.设置writer
        //为给定的类型名称创建一个特性写入器
        FeatureWriter<SimpleFeatureType, SimpleFeature> writer = ds.getFeatureWriter(
                ds.getTypeNames()[0], Transaction.AUTO_COMMIT);


        //Interface SimpleFeature：一个由固定列表值以已知顺序组成的SimpleFeatureType实例。
        // 13.写一个点
        SimpleFeature feature = writer.next();
        feature.setAttribute("the_geom", geometry);
        feature.setAttribute("osm_id", 1234567890l);
        feature.setAttribute("name", "多多边形");
        feature.setAttribute("des", "国家大剧院");

        // 14.写入
        writer.write();

        // 15.关闭
        writer.close();

        // 16.释放资源
        ds.dispose();


        // 17.读取shapefile文件的图形信息
        ShpFiles shpFiles = new ShpFiles(path);
		*//*ShapefileReader(
		 ShpFiles shapefileFiles,
		 boolean strict, --是否是严格的、精确的
		 boolean useMemoryMapped,--是否使用内存映射
		 GeometryFactory gf,     --几何图形工厂
		 boolean onlyRandomAccess--是否只随机存取
		 )
		*//*
        ShapefileReader reader = new ShapefileReader(shpFiles,
                false, true, new GeometryFactory(), false);
        while (reader.hasNext()) {
            System.out.println(reader.nextRecord().shape());
        }
        reader.close();*/
    }


    public static List<Coordinate> divideDistance(LineString l){

        PointPairDistance pointPairDistance = new PointPairDistance();
        Coordinate[] coors = l.getCoordinates();
        List<Double> dLst = new ArrayList<>();
        for(int i = 0; i < coors.length - 1; i++){
            Coordinate pre = coors[i];
            Coordinate last = coors[i+1];
            pointPairDistance.initialize(pre, last);
            dLst.add(pointPairDistance.getDistance());
        }

        //将距离最小值作为划分的基准，最小的默认划分为10份
        //其余两点之间的分隔数以距离比*10来算
        int minDivNum = 1;
        double minDis = Collections.min(dLst);
        //long minDisNum = dLst.indexOf(minDis);
        List<Coordinate> coorLst = new ArrayList<>();
        for(int j = 0; j < dLst.size(); j++){
            int divNum = (int) (dLst.get(j)/minDis * minDivNum);
            Coordinate staCoor = coors[j];
            Coordinate endCoor = coors[j+1];
            double staCoorX = staCoor.x;
            double staCoorY = staCoor.y;
            double endCoorX = endCoor.x;
            double endCoorY = endCoor.y;

            //由相邻两点构成的直线
            GeoCreatorByLtc geoCreatorByLtc = Global.geoCreatorByLtc;
            LineString innerLine = geoCreatorByLtc.createLine(staCoorX, staCoorY, endCoorX, endCoorY);

            //coorLst.add(staCoor);
            double divX = (endCoorX - staCoorX)/divNum;
            double divY = (endCoorY - staCoorY)/divNum;
            for(int num = 1; num < divNum; num++){
                double x = staCoorX + divX * num;
                double y = staCoorY + divY * num;
                Coordinate c = new Coordinate(x, y);
                PointPairDistance pairDistance = new PointPairDistance();
                DistanceToPoint.computeDistance(innerLine, c, pairDistance);
                Coordinate[] pairCoors = pairDistance.getCoordinates();

                coorLst.add(pairCoors[0]);
            }
            coorLst.add(endCoor);
        }
        return  coorLst;

    }

    public static void main1(String[] args) throws Exception {

        // 1.利用Provider连接 空间数据库
        if (!connDataBase("192.168.1.104", 5432, "postgres", "bluethink", "test")) {
            System.out.println("连接postgresql数据库失败，请检查参数！");
        }

        System.out.println("===============连接postgis空间数据库==============");
        //connPostGis("postgis", "192.168.1.104", 5432, "test", "postgres", "bluethink");

        System.out.println("===============读取shp文件并存储至postgresql数据库==============");
        // A.建筑物的shapefile，多边形 MULTIPOLYGON
        // String path = "E:\\china-latest-free\\gis.osm_buildings_a_free_1.shp";

        // B.路的shapefile，多线MULTILINESTRING
        // String path = "E:\\china-latest-free\\gis.osm_roads_free_1.shp";

        // C.建筑物的点坐标 以Point为主
        // String path = "E:\\china-latest-free\\gis.osm_pois_free_1.shp";

        String path = "E:\\china-latest-free\\gis.osm_buildings_a_free_1.shp";
        //this.readSHP(path, "GBK", null);

        System.out.println("===============读取图层geotable==============");
        postGisReading("geotable");

        System.out.println("===============获取所有图层【所有空间几何信息表】==============");
        getAllLayers();

        System.out.println("===============创建自己的shp文件==============");
        String MPolygonWKT = "MULTIPOLYGON(((116.3824004 39.9032955,116.3824261 39.9034733,116.382512 39.9036313,116.382718 39.9038025,116.3831643 39.903954,116.383602 39.9040198,116.3840827 39.9040001,116.3844003 39.9039211,116.3846921 39.903763,116.3848552 39.9035787,116.3848981 39.9033548,116.3848037 39.9031244,116.3845719 39.9029071,116.3842286 39.9027754,116.3837823 39.9027227,116.3833789 39.9027095,116.383027 39.902749,116.3828038 39.9028346,116.382615 39.90294,116.3824776 39.9030717,116.3824004 39.9032955)))";
        org.locationtech.jts.geom.MultiPolygon multiPolygon = Global.geoCreatorByLtc.createMulPolygonByWKT(MPolygonWKT);
        System.out.println(multiPolygon.getGeometryType());
        //首先得创建my这个目录
        // writeSHP("C:/my/multipol.shp",multiPolygon);
        System.out.println("===============打开shp文件==============");
        GeoToolsUtils.openShpFile();
    }


}

