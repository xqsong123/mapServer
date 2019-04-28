import com.map.repository.ChartDataDao;
import com.map.repository.PopulationDao;
import com.map.repository.QuantityDao;
import com.map.serviceImp.ChartDataServiceImpl;
import com.map.utils.MongoDBConnection;
import com.mongodb.*;
import com.mongodb.client.*;
import com.mongodb.client.MongoClient;
import net.sf.json.JSONObject;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.Test;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class TestMongodb {
    //获取数据库连接对象
    MongoDatabase mongoDatabase = MongoDBConnection.getConnect2();

    //插入一个文档
    @Test
    public void insertOneTest() {
        //获取集合
        MongoCollection<Document> collection = mongoDatabase.getCollection("user");
        //要插入的数据
        Document document = new Document().append("name", "小杨")
                .append("sex", "男")
                .append("age", 35)
                .append("shengao", 177);
        //插入一个文档
        collection.insertOne(document);
    }

    //查找集合中的所有文档
    @Test
    public void findTest() {
        //获取集合
        MongoCollection<Document> collection = mongoDatabase.getCollection("user");
        //指定查询过滤器
        // Bson filter = Filters.eq

        //查找集合中的所有文档
        //FindIterable findIterable = collection.find();
        FindIterable<Document> findIterable = collection.find(new Document("age", new Document("$gte", 15).append("$lte", 38)).append("shengao", new Document("$gte", 170).append("$lte", 180)).append("name", "小王")).projection(new BasicDBObject().append("name", 1).append("age", 1));
        System.out.println("findIterable--------:" + findIterable);
        MongoCursor cursor = findIterable.iterator();
        System.out.println("cursor--------:" + cursor);
        while (cursor.hasNext()) {
            System.out.println(cursor.next());
        }
    }

    private static int getAgeByBirth(Date birthday) {
        int age = 0;
        try {
            Calendar now = Calendar.getInstance();
            now.setTime(new Date());// 当前时间

            Calendar birth = Calendar.getInstance();
            birth.setTime(birthday);

            if (birth.after(now)) {//如果传入的时间，在当前时间的后面，返回0岁
                age = 0;
            } else {
                age = now.get(Calendar.YEAR) - birth.get(Calendar.YEAR);
                if (now.get(Calendar.DAY_OF_YEAR) > birth.get(Calendar.DAY_OF_YEAR)) {
                    age += 1;
                }
            }
            return age;
        } catch (Exception e) {//兼容性更强,异常后返回数据
            return 0;
        }
    }

    @Test
    public void query() {
        ChartDataDao jzwDaozwDao = new ChartDataDao();
        List<String> secTypeLst = new ArrayList<String>();
        secTypeLst.add("0103");
        //jzwDaozwDao.getAllChartData("01", 116, 117, 36, 37);
        //PopulationDao populationDao = new PopulationDao();
        // List list = populationDao.queryPopulation(116, 117, 36, 37);
      /*  for (int i = 0; i < list.size(); i++) {
            Document obj = (Document) list.get(i);
            Object RKBM = obj.get("RKBM");
            Object SYRKGLLBDM = obj.get("SYRKGLLBDM");
            Object GMSFHM = obj.get("GMSFHM");
            Date CSRQ = (Date) obj.get("CSRQ");
           *//* SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String sDate=sdf.format(CSRQ);
            System.out.println(sDate);*//*
            int age = getAgeByBirth(CSRQ);
            System.out.println("age---------:" + age);
            Object ZDRYBZ = obj.get("ZDRYBZ");
            Object DJSJ = obj.get("DJSJ");
            double ZXDHZB = (double) obj.get("ZXDHZB");
            double ZXDZZB = (double) obj.get("ZXDZZB");
            System.out.println("RKBM----:" + RKBM + "SYRKGLLBDM-----:" + SYRKGLLBDM + "GMSFHM-----:" + GMSFHM + "CSRQ-----:" + CSRQ + "ZDRYBZ----:" + ZDRYBZ + "DJSJ-----:" + DJSJ + "ZXDHZB----:" + ZXDHZB + "ZXDZZB---:" + ZXDZZB);
        }*/

    }

//    @Test
//    public void testLeft1() {
//        String dzbm = "68138254482720DBE053B692300A522F";
//        try (MongoClient client = new MongoClient("localhost", 27017)) {
//
//            MongoDatabase database = client.getDatabase("demo");
//            MongoCollection<Document> collection = database.getCollection("jzwdyfw");
//
//            List<? extends Bson> pipeline = Arrays.asList(
//                    new Document()
//                            .append("$match", new Document()
//                                    .append("SSJZW_DZBM", dzbm)),
//                    new Document()
//                            .append("$project", new Document()
//                                    .append("_id", 0)
//                                    .append("x", "$$ROOT")
//                            ),
//                    new Document()
//                            .append("$lookup", new Document()
//                                    .append("localField", "x.DZBM")
//                                    .append("from", "rkdw")
//                                    .append("foreignField", "DZBM")
//                                    .append("as", "y")
//                            ),
//                    new Document()
//                            .append("$unwind", new Document()
//                                    .append("path", "$y")
//                                    .append("preserveNullAndEmptyArrays", false)
//                            )
//                    ,
//                    new Document()
//                            .append("$project", new Document()
//                                    .append("_id", "$x._id")
//                                    .append("mlpl", "$x.MLPL")
//                                    .append("jzwdyfw_sjlj", "$x.SJLY")
//                                    .append("rksx", "$y.RKSX")
//                                    .append("zdrybz", "$y.ZDRYBZ")
//                                    .append("djsj", "$y.DJSJ")
//                                    .append("gmsfhm", "$y.GMSFHM")
//                                    .append("rkdw_sjly", "$y.SJLY"))
//            );
//
//            MongoCursor<Document> iterator = collection.aggregate(pipeline)
//                    .allowDiskUse(true)
//                    .iterator();
//            int count = 0; //定义一个统计器
//            while (iterator.hasNext()) {
//                Document document = iterator.next();
//                count = count + 1;
//                System.out.println(document.getObjectId("id"));
//                System.out.println(document.getString("dzbm"));
//                System.out.println(document.getString("longitude"));
//                System.out.println(document.getString("latitude"));
//            }
//
//            System.out.println("count-----------:" + count);
//        } catch (MongoException e) {
//            // handle MongoDB exception
//        }
//    }

    @Test
    public void testLeft2() throws Exception {
        MongoCollection<Document> collection = mongoDatabase.getCollection("jzwdyfw");
        Document sub_match = new Document();
        sub_match.put("SSJZW_DZBM", "68138254482720DBE053B692300A522F");

        Document sub_project = new Document();
        sub_project.put("_id", 0);
        sub_project.put("x", "$$ROOT");

        Document sub_lookup = new Document();
        sub_lookup.put("localField", "x.DZBM");
        sub_lookup.put("from", "rkdw");
        sub_lookup.put("foreignField", "DZBM");
        sub_lookup.put("as", "y");

        Document sub_unwind = new Document();
        sub_unwind.put("path", "$y");
        sub_unwind.put("preserveNullAndEmptyArrays", true);//显示没有关联到人口单位的数据

        Document sub_project2 = new Document();
        sub_project2.put("_id", "$x._id");
        sub_project2.put("mlpl", "$x.MLPH");
        sub_project2.put("jzwdyfw_sjly", "$x.SJLY");
        sub_project2.put("rksx", "$y.RKSX");
        sub_project2.put("zdrybz", "$y.ZDRYBZ");
        sub_project2.put("djsj", "$y.DJSJ");
        sub_project2.put("gmsfhm", "$y.GMSFHM");
        sub_project2.put("rkdw_sjly", "$y.SJLY");
        sub_project2.put("dzbm", "$x.DZBM");

        Document match = new Document("$match", sub_match);
        Document project1 = new Document("$project", sub_project);
        Document lookup = new Document("$lookup", sub_lookup);
        Document unwind = new Document("$unwind", sub_unwind);
        Document project2 = new Document("$project", sub_project2);
        List<Document> aggregateList = new ArrayList<Document>();
        aggregateList.add(match);
        aggregateList.add(project1);
        aggregateList.add(lookup);
        aggregateList.add(unwind);
        aggregateList.add(project2);

        AggregateIterable<Document> resultset = collection.aggregate(aggregateList).allowDiskUse(true);
        MongoCursor<Document> cursor = resultset.iterator();
        int count = 0; //定义一个统计器
        try {
            while (cursor.hasNext()) {
                Document item_doc = cursor.next();
                count = count + 1;
                String mlpl = item_doc.getString("mlpl");
                String jzwdyfw_sjly = item_doc.getString("jzwdyfw_sjly");
                String rksx = item_doc.getString("rksx");
                System.out.println("item_doc------:" + item_doc);
            }
            System.out.println("count-----------:" + count);
        } finally {
            cursor.close();
        }
    }

    @Test
    //判断list中是否有重复的值
    public void testChong() {
        List<String> list = new ArrayList<String>();
        list.add("aa");
        list.add("bb");
        list.add("cc");
        list.add("dd");
        list.add("bb");
        list.add("ee");
        list.add("dd");
        list.add("ff");

        String temp = "";
        for (int i = 0; i < list.size() - 1; i++) {
            temp = list.get(i);
            for (int j = i + 1; j < list.size(); j++) {
                if (temp.equals(list.get(j))) {
                    System.out.println("第" + (i + 1) + "个跟第" + (j + 1) + "个重复，值是：" + temp);
                }
            }
        }
    }

    @Test
    public void compareTime() {
        int i = compare_date("2995-11-12 15:21:00", "1999-12-11 09:59:00");
        System.out.println("i==" + i);
    }

    public static int compare_date(String DATE1, String DATE2) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            Date dt1 = df.parse(DATE1);
            Date dt2 = df.parse(DATE2);
            if (dt1.getTime() > dt2.getTime()) {
                System.out.println("dt1 在dt2前");
                return 1;
            } else if (dt1.getTime() < dt2.getTime()) {
                System.out.println("dt1在dt2后");
                return -1;
            } else {
                return 0;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return 0;
    }

    @Test
    public void isNei() {
        // 生效时间
        Date effectivetime = strToDate("2018-7-8 17:00:00");
        // 失效时间
        Date invalidtime = strToDate("2019-9-8 17:30:00");
        boolean flag = belongCalendar(new Date(), new Date(), invalidtime);
        System.out.println(flag);

    }

    /**
     *      * 判断时间是否在时间段内
     *      * 
     *      * @param nowTime
     *      * @param beginTime
     *      * @param endTime
     *      * @return
     *     
     */
    public static boolean belongCalendar(Date nowTime, Date beginTime, Date endTime) {
        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);
        Calendar begin = Calendar.getInstance();
        begin.setTime(beginTime);
        Calendar end = Calendar.getInstance();
        end.setTime(endTime);
        if (date.after(begin) && date.before(end)) {
            return true;
        } else if (nowTime.compareTo(beginTime) == 0 || nowTime.compareTo(endTime) == 0) {
            return true;
        } else {
            return false;
        }
    }


    // 字符串 转 日期
    public static Date strToDate(String str) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = sdf.parse(str);
        } catch (ParseException e) {
        }
        return date;
    }

//    @Test
//    public void caluteTime() {
//        List list = new ArrayList<>();
//        long start = System.currentTimeMillis();
//        //连接到 mongodb 服务
//        MongoClient mongoClient = new MongoClient("localhost", 27017);
//        MongoCollection<Document> collection = mongoClient.getDatabase("gis").getCollection("test1");//集合名
//        FindIterable<Document> findIterable = collection.find();
//               /* .projection(new BasicDBObject().append("RKBM", 1).append("SYRKGLLBDM", 1)
//                        .append("GMSFHM", 1).append("CSRQ", 1).append("ZDRYBZ", 1).append("DJSJ", 1).append("ZXDHZB", 1).append("ZXDZZB", 1));*/
//        MongoCursor cursor = findIterable.iterator();
//        int count = 0;
//        while (cursor.hasNext()) {
//            list.add(cursor.next());
//            count = count + 1;
//            if (count % 10000 == 0) {
//                System.out.println("==========" + count);
//                System.out.println(System.currentTimeMillis() - start);
//            }
//        }
//        long end = System.currentTimeMillis();
//        System.out.println("count-----------:" + count + "++" + (end - start) / 1000d + "秒");
//    }

    @Test
    public void queryQuantirty() {
      /*  QuantityDao quantityDao = new QuantityDao();
        List list = quantityDao.queryPointsByType("01", "", "");

        Map<String, Object> map = (Map<String, Object>) list.get(0);
        String jzwbm = (String) map.get("jzwbm");
        long firNum = (Long) map.get("firNum");
        System.out.println("firNum===:" + firNum + "   " + "jzwbm-----:" + jzwbm);*/
    }

    @Test
    public void queryDensity() {
        PopulationDao populationDao = new PopulationDao();
       /* List list =new ArrayList();
        list.add("03");
        list.add("04");
        populationDao.getAreaDensity(list,"01","02");*/
      /*  Map object= (Map) populationDao.getJzwDetail("6813823E20F920DBE053B692300A522F");
        String jzwbm = (String) object.get("jzwbm");
        Object jsonObject= (Object)object.get("detail");
System.out.println("jzwbm----:"+jzwbm+"====="+"jsonObject-----:"+jsonObject);*/
        List list = populationDao.getPersonDetail("6815D1B23F32EE29E053B592300A5538");
        Map map= (Map)list.get(0);
        map.get("RKBM");
        map.get("XM");
        map.get("GMSFHM");
        System.out.println( "rkbm---:"+map.get("RKBM")+"===="+"xm----:"+map.get("XM")+"====="+"sfz-----:"+map.get("GMSFHM"));
    }


}
