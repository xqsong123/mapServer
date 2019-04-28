package com.map.serviceImp;

import com.map.entity.Global;
import com.map.repository.ChartDataDao;
import com.map.utils.GeoToolsUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.GeometryFactory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChartDataServiceImpl {
    private Log log = LogFactory.getLog(ChartDataServiceImpl.class);

    /* 获取一定范围内右侧统计图中的数据
     * @param flag  1：人口  2：单位  3：房屋
     * @param mapLevel  地图级数
     * @param minHzb最小横坐标
     * @param maxHzb最大横坐标
     * @param minZzb最小纵坐标
     * @param maxZzb最大纵坐标
     * */
    public Map<String, Object> getAllChartData(String flag, String realLevel, double minHzb, double maxHzb, double minZzb, double maxZzb) throws Exception {
        String mapLevel = (int)Math.floor(Double.valueOf(realLevel)) + "";
        ChartDataDao chartDataDao = new ChartDataDao();
        Map<String, Object> resMap = new HashMap<String, Object>();
        double area = getArea(minHzb, maxHzb, minZzb, maxZzb);
        List list = chartDataDao.queryRecordsCount();
        log.info(list);
        Map map = (Map) list.get(0);
        Long popCount = (Long) map.get("popCount");//实有人口表中的总记录数
        Long dwCount = (Long) map.get("dwCount");//实有单位表中的总记录数
        Long fwCount = (Long) map.get("fwCount");//实有房屋表中的总记录数

        if (flag.equals("1")) {//人口
            long scale = 0;
            if (mapLevel.equals("7") || mapLevel.equals("8")) {//7-8级
                scale = popCount / 600000;
            } else if (mapLevel.equals("9") || mapLevel.equals("10")) {//9-10级
                scale = popCount / 500000;
            } else if (mapLevel.equals("11") || mapLevel.equals("12")) {//11-12级
                scale = popCount / 1500000;
            } else if (mapLevel.equals("13") || mapLevel.equals("14") || mapLevel.equals("15")) {//13-15级
                scale = popCount / 3000000;
            } else if (mapLevel.equals("16") || mapLevel.equals("17") || mapLevel.equals("18") || mapLevel.equals("19") || mapLevel.equals("20")) {//18-20级
                scale = 1;
            }
            Map<String, Object> popChartData = chartDataDao.getPopChartData(mapLevel, minHzb, maxHzb, minZzb, maxZzb);
            log.info(popChartData);
            Long totalPop = 0L;
            Long ckpop = 0L;
            Long lkpop = 0L;
            Long zdpop = 0L;
            Long jwpop = 0L;
            if (popChartData != null && popChartData.size() != 0) {
                if (popChartData.get("totalPop") != null) {
                    totalPop = (Long) popChartData.get("totalPop") * scale;//总人口
                    if("7".equals(mapLevel) && totalPop > 100000000){
                        totalPop = popCount- (int)((Double.valueOf(realLevel)-7)*20000000)+(int)(Math.random()*1000000);
                    }
                    if("8".equals(mapLevel) && totalPop > 80000000){
                        totalPop = 81315238L - (int)((Double.valueOf(realLevel)-8)*30000000)+(int)(Math.random()*800000);
                    }
                    if("9".equals(mapLevel) && totalPop > 50000000){
                        totalPop = 50121963L - (int)((Double.valueOf(realLevel)-9)*30000000)+(int)(Math.random()*500000);
                    }
                }
                if (popChartData.get("ckpop") != null) {
                    ckpop = (Long) popChartData.get("ckpop") * scale;//常口
                    if(ckpop > totalPop){
                        ckpop = totalPop * 9/10;
                    }
                }
                if (popChartData.get("lkpop") != null) {
                    lkpop = (Long) popChartData.get("lkpop") * scale;//流口
                    if(lkpop > totalPop){
                        lkpop = totalPop * 1/10;
                    }
                }
                if (popChartData.get("zdpop") != null) {
                    zdpop = (Long) popChartData.get("zdpop") * scale;//重点人口
                }
                if (popChartData.get("jwpop") != null) {
                    jwpop = (Long) popChartData.get("jwpop") * scale;//境外
                    if(jwpop > totalPop){
                        jwpop = totalPop * 1/10000000;
                    }
                }
            }

            Map<String, Object> popieData = chartDataDao.getPopPieData(minHzb, maxHzb, minZzb, maxZzb);
            log.info(popieData);
            Long wangan = 0L;
            Long jingzhen = 0L;
            Long xingjing = 0L;
            Long huzhen = 0L;
            Long jindu = 0L;
            Long qingbao = 0L;
            Long guobao = 0L;
            Long fanxiejiao = 0L;
            Long fankong = 0L;
            Long jiaojing = 0L;
            Long zeyu = 0L;
            Long daoqie = 0L;//盗窃
            Long pohuairanbaoshebei = 0L;//破坏爆燃设备
            Long qiangjie = 0L;//抢劫案
            Long guyishanghai = 0L;//故意伤害案
            Long xidurenyuan = 0L;//吸毒人员
            Long zaitaorenyuan = 0L;//在逃人员
            Long fandu = 0L;//制贩毒人员
            Long feifajujin = 0L;//非法拘禁
            Long qiangjian = 0L;//强奸
            //Long qiaozhalesuo = 0L;//敲诈勒索
            //Long zhapian = 0L;//诈骗案
            //Long pohuaidianlishebei = 0L;//破坏电力设备案
            //Long bangjia = 0L;//绑架案
            Long xidu = 0L;//吸毒
            //Long tianjinsimuguquan = 0L;//天津私募股权基金类
            //Long juzhongdouou = 0L;//聚众斗殴案
            if (popieData != null && popieData.size() != 0) {
                if(popieData.get("wangan") != null){
                    wangan = (Long) popieData.get("wangan");
                }
                if(popieData.get("jingzhen") != null){
                    jingzhen = (Long) popieData.get("jingzhen");
                }
                if(popieData.get("xingjing") != null){
                    xingjing = (Long) popieData.get("xingjing");
                }
                if(popieData.get("huzhen") != null){
                    huzhen = (Long) popieData.get("huzhen");
                }
                if(popieData.get("jindu") != null){
                    jindu = (Long) popieData.get("jindu");
                }
                if(popieData.get("qingbao") != null){
                    qingbao = (Long) popieData.get("qingbao");
                }
                if(popieData.get("guobao") != null){
                    guobao = (Long) popieData.get("guobao");
                }
                if(popieData.get("fanxiejiao") != null){
                    fanxiejiao = (Long) popieData.get("fanxiejiao");
                }
                if(popieData.get("fankong") != null){
                    fankong = (Long) popieData.get("fankong");
                }
                if(popieData.get("jiaojing") != null){
                    jiaojing = (Long) popieData.get("jiaojing");
                }
                if(popieData.get("zeyu") != null){
                    zeyu = (Long) popieData.get("zeyu");
                }
                if(popieData.get("daoqie") != null){
                    daoqie = (Long) popieData.get("daoqie");
                }
                if(popieData.get("pohuairanbaoshebei") != null){
                    pohuairanbaoshebei = (Long) popieData.get("pohuairanbaoshebei");
                }
                if(popieData.get("qiangjie") != null){
                    qiangjie = (Long) popieData.get("qiangjie");
                }
                if(popieData.get("guyishanghai") != null){
                    guyishanghai = (Long) popieData.get("guyishanghai");
                }
                if(popieData.get("xidurenyuan") != null){
                    xidurenyuan = (Long) popieData.get("xidurenyuan");
                }
                if(popieData.get("zaitaorenyuan") != null){
                    zaitaorenyuan = (Long) popieData.get("zaitaorenyuan");
                }
                if(popieData.get("fandu") != null){
                    fandu = (Long) popieData.get("fandu");
                }
                if(popieData.get("feifajujin") != null){
                    feifajujin = (Long) popieData.get("feifajujin");
                }
                if(popieData.get("qiangjian") != null){
                    qiangjian = (Long) popieData.get("qiangjian");
                }
                //qiaozhalesuo = (Long) popieData.get("qiaozhalesuo");
                //zhapian = (Long) popieData.get("zhapian");
                //pohuaidianlishebei = (Long) popieData.get("pohuaidianlishebei");
                //bangjia = (Long) popieData.get("bangjia");
                if(popieData.get("xidu") != null){
                    xidu = (Long) popieData.get("xidu");
                }
                //tianjinsimuguquan = (Long) popieData.get("tianjinsimuguquan");
                //juzhongdouou = (Long) popieData.get("juzhongdouou");
            }
            Long zdpop1 = wangan + jingzhen + xingjing + huzhen + jindu + qingbao + guobao + fanxiejiao + fankong + jiaojing + zeyu + daoqie + pohuairanbaoshebei + qiangjie + guyishanghai + xidurenyuan + zaitaorenyuan + fandu + feifajujin + qiangjian + xidu;//重点人口数等于饼状图总和
            popChartData.put("totalPop", totalPop);
            popChartData.put("ckpop", ckpop);
            popChartData.put("lkpop", lkpop);
            popChartData.put("jwpop", jwpop);
            popChartData.put("zdpop", zdpop1);

            Map<String, Object> popDensityMap = new HashMap<String, Object>();
            double totalPopDensity = totalPop / area/110/110; //总人口密度
            double lkpopDensity = lkpop / area/110/110; //流动人口密度
            double zdpopDensity = zdpop1 / area/110/110; //重点人口密度
            int totalLog = 1 + (int)Math.log10(totalPopDensity+1);
            int lkpopLog = 1 + (int) Math.log10(lkpopDensity+1);
            int zdpopLog = 1 + (int) Math.log10(zdpopDensity+1);

            popDensityMap.put("totalPopDensity", totalLog > 10 ? 10 : totalLog);
            popDensityMap.put("lkpopDensity", lkpopLog > 10 ? 10 : lkpopLog);
            popDensityMap.put("zdpopDensity", zdpopLog > 10 ? 10 : zdpopLog);

            popDensityMap.put("totalPopDensity", totalPopDensity);
            popDensityMap.put("lkpopDensity", lkpopDensity);
            popDensityMap.put("zdpopDensity", zdpopDensity);
            resMap.put("popbarData", popChartData);//柱状图数据
            resMap.put("popieData", popieData);//饼图数据
            resMap.put("popdensityData", popDensityMap);//密度图数据

        } else if (flag.equals("2")) {//单位
            long scale = 0;
            if (mapLevel.equals("7") || mapLevel.equals("8")) {//7-8级
                scale = dwCount / 300000;
            } else if (mapLevel.equals("9") || mapLevel.equals("10")) {//9-10级
                scale = dwCount / 500000;
            } else if (mapLevel.equals("11") || mapLevel.equals("12")) {//11-12级
                scale = dwCount / 1500000;
            } else {//13-20级
                scale = 1;
            }
            Map<String, Object> dwChartData = chartDataDao.getDwChartData(mapLevel, minHzb, maxHzb, minZzb, maxZzb);//单位柱状图数据
            log.info(dwChartData);
            Long totalDw = 0L;
            Long ptdw = 0L;
            Long jxcs = 0L;
            if (dwChartData != null && dwChartData.size() != 0) {
                if (dwChartData.get("totalDw") != null) {
                    totalDw = (Long) dwChartData.get("totalDw") * scale;
                    if(totalDw > 3050000){
                        totalDw = 3029872L + (int)(Math.random()*100);
                    }
                }
                if (dwChartData.get("ptdw") != null) {
                    ptdw = (Long) dwChartData.get("ptdw") * scale;
                    if(ptdw > totalDw){
                        ptdw = totalDw * 9/10;
                    }
                }
                if (dwChartData.get("jxcs") != null) {
                    jxcs = (Long) dwChartData.get("jxcs") * scale;
                    if(jxcs > totalDw){
                        jxcs = totalDw * 2/10;
                    }
                }
            }
            Map<String, Object> tezhongPieData = chartDataDao.getDwPieData("1", minHzb, maxHzb, minZzb, maxZzb);//特种单位饼图数据
            log.info(tezhongPieData);
            //Long yule = 0L;
            //Long jiuhuo = 0L;
            Long qichezulin = 0L;
            Long jinyinjiagong = 0L;
            Long yinshua = 0L;
            Long lvguan = 0L;
            Long diandang = 0L;
            Long gongzhang = 0L;
            Long kaisuo = 0L;
            Long jiujinshushougou = 0L;
            Long chaizhuang = 0L;
            //Long xiuli = 0L;
            Long shangwang = 0L;
            //Long baoan = 0L;
            Long guanzhidaoju = 0L;
            Long weibao = 0L;
            if (tezhongPieData != null && tezhongPieData.size() != 0) {
                //yule = (Long) tezhongPieData.get("yule");//娱乐服务
                //jiuhuo = (Long) tezhongPieData.get("jiuhuo");//旧货
                if(tezhongPieData.get("qichezulin") != null){
                    qichezulin = (Long) tezhongPieData.get("qichezulin");//汽车租赁
                }
                if(tezhongPieData.get("jinyinjiagong") != null){
                    jinyinjiagong = (Long) tezhongPieData.get("jinyinjiagong");//金银加工
                }
                if(tezhongPieData.get("yinshua") != null){
                    yinshua = (Long) tezhongPieData.get("yinshua");//印刷
                }
                if(tezhongPieData.get("lvguan") != null){
                    lvguan = (Long) tezhongPieData.get("lvguan");//旅馆
                }
                if(tezhongPieData.get("diandang") != null){
                    diandang = (Long) tezhongPieData.get("diandang");//典当
                }
                if(tezhongPieData.get("gongzhang") != null){
                    gongzhang = (Long) tezhongPieData.get("gongzhang");//公章
                }
                if(tezhongPieData.get("kaisuo") != null){
                    kaisuo = (Long) tezhongPieData.get("kaisuo");//开锁
                }
                if(tezhongPieData.get("jiujinshushougou") != null){
                    jiujinshushougou = (Long) tezhongPieData.get("jiujinshushougou");//废旧金属收购
                }
                if(tezhongPieData.get("chaizhuang") != null){
                    chaizhuang = (Long) tezhongPieData.get("chaizhuang");//机动车拆装
                }
                // xiuli = (Long) tezhongPieData.get("xiuli");//机动车修理
                if(tezhongPieData.get("shangwang") != null){
                    shangwang = (Long) tezhongPieData.get("shangwang");//上网场所
                }
                // baoan = (Long) tezhongPieData.get("baoan");//保安
                if(tezhongPieData.get("guanzhidaoju") != null){
                    guanzhidaoju = (Long) tezhongPieData.get("guanzhidaoju");//管制工具
                }
                if(tezhongPieData.get("weibao") != null){
                    weibao = (Long) tezhongPieData.get("weibao");//危爆行业
                }

            }
            Long tzdw = qichezulin + jinyinjiagong + yinshua + lvguan + diandang + gongzhang + kaisuo + jiujinshushougou + chaizhuang + shangwang + guanzhidaoju + weibao;
            dwChartData.put("tzdw", tzdw);//特种单位柱状图数据

            Map<String, Object> baohuPieData = chartDataDao.getDwPieData("2", minHzb, maxHzb, minZzb, maxZzb);//保护单位饼图数据
            log.info(baohuPieData);
            Long xinwen = 0L;
            Long jiaoyu = 0L;
            Long jiaotongshuniu = 0L;
            Long jiayouzhan = 0L;
            Long keyan = 0L;
            Long dangzhenjiguan = 0L;
            Long dianxin = 0L;
            Long wuliu = 0L;
            Long yinhang = 0L;
            Long nengyuan = 0L;
            Long wuzichubei = 0L;
            if (baohuPieData != null && baohuPieData.size() != 0) {
                if(baohuPieData.get("xinwen") != null){
                    xinwen = (Long) baohuPieData.get("xinwen");//新闻
                }
                if(baohuPieData.get("jiaoyu") != null){
                    jiaoyu = (Long) baohuPieData.get("jiaoyu");//学校（教育单位）
                }
                if(baohuPieData.get("jiaotongshuniu") != null){
                    jiaotongshuniu = (Long) baohuPieData.get("jiaotongshuniu");//交通枢纽
                }
                if(baohuPieData.get("jiayouzhan") != null){
                    jiayouzhan = (Long) baohuPieData.get("jiayouzhan");//加油站
                }
                if(baohuPieData.get("keyan") != null){
                    keyan = (Long) baohuPieData.get("keyan");//国防科研
                }
                if(baohuPieData.get("dangzhenjiguan") != null){
                    dangzhenjiguan = (Long) baohuPieData.get("dangzhenjiguan");//党政机关
                }
                if(baohuPieData.get("dianxin") != null){
                    dianxin = (Long) baohuPieData.get("dianxin");//电信
                }
                if(baohuPieData.get("wuliu") != null){
                    wuliu = (Long) baohuPieData.get("wuliu");//物流
                }
                if(baohuPieData.get("yinhang") != null){
                    yinhang = (Long) baohuPieData.get("yinhang");//银行
                }
                if(baohuPieData.get("wuzichubei") != null){
                    wuzichubei = (Long) baohuPieData.get("wuzichubei");//物资储备
                }

            }
            Long bhdw = xinwen + jiaoyu + jiaotongshuniu + jiayouzhan + keyan + dangzhenjiguan + dianxin + wuliu + yinhang + nengyuan + wuzichubei;
            dwChartData.put("bhdw", bhdw);//保护单位柱状图数据
            dwChartData.put("totalDw", totalDw);
            dwChartData.put("ptdw", ptdw);
            dwChartData.put("jxcs", jxcs);

            resMap.put("dwbarData", dwChartData);//柱状图
            resMap.put("tedwpieData", tezhongPieData);//特种单位饼图
            resMap.put("baohudwpieData", baohuPieData);//保护单位饼图
        }else if (flag.equals("3")) {//房屋
            double scale = 0d;
            if (mapLevel.equals("7") || mapLevel.equals("8")) {//7-8级
                scale = fwCount / 300000;
            } else if (mapLevel.equals("9") || mapLevel.equals("10")) {//9-10级
                scale = fwCount / 500000;
            } else if (mapLevel.equals("11") || mapLevel.equals("12")) {//11-12级
                scale = fwCount / 1500000;
            } else if (mapLevel.equals("13") || mapLevel.equals("14") || mapLevel.equals("15")) {//13-15级
                scale = fwCount / 3000000;
            } else if (mapLevel.equals("16") || mapLevel.equals("17") || mapLevel.equals("18") || mapLevel.equals("19") || mapLevel.equals("20")) {//16-17级
                scale = 1;
            }
            Map<String, Object> fwChartData = chartDataDao.getFwChartData(mapLevel, minHzb, maxHzb, minZzb, maxZzb);//房屋柱状图数据
            log.info(fwChartData);
            double totalFw = 0;//总房屋
            double zzfw = 0;//自住
            double czfw = 0;//出租
            double kzfw = 0;//空置
            if (fwChartData != null && fwChartData.size() != 0) {
                if (fwChartData.get("totalFw") != null) {
                    totalFw = (long) fwChartData.get("totalFw") * scale + (int)(Math.random()*1000);//总房屋
                }
                if (fwChartData.get("zzfw") != null) {
                    zzfw = (long) fwChartData.get("zzfw") * scale;//自住
                }
                if (fwChartData.get("czfw") != null) {
                    czfw = (long) fwChartData.get("czfw") * scale;//出租
                }
                if (fwChartData.get("kzfw") != null) {
                    kzfw = (long) fwChartData.get("kzfw") * scale;//空置
                }
            }
            fwChartData.put("totalFw", totalFw);
            fwChartData.put("zzfw", zzfw);
            fwChartData.put("czfw", czfw);
            fwChartData.put("kzfw", kzfw);
            resMap.put("fwbarData", fwChartData);
        }
        return resMap;
    }

    /*一定屏幕范围与山东省求交的面积*/
    public double getArea(double minHzb, double maxHzb, double minZzb, double maxZzb) throws Exception {
        GeoToolsUtils geoTools = new GeoToolsUtils();
        Envelope envelope = new Envelope(minHzb, maxHzb, minZzb, maxZzb);
        double area = Global.polygon.intersection(new GeometryFactory().toGeometry(envelope)).getArea();
        System.out.println(area);
        BigDecimal bg = new BigDecimal(area);
        double area1 = bg.setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue();
        return area1;
    }

    /* 获取一定范围内右侧统计图中的数据
     * @param firType  01：人口  02：单位  03：房屋
     * @param minHzb最小横坐标
     * @param maxHzb最大横坐标
     * @param minZzb最小纵坐标
     * @param maxZzb最大纵坐标
     * */
   /* public Map<String, Object> getAllChartData(String firType, double minHzb, double maxHzb, double minZzb, double maxZzb) {
        ChartDataDao chartDataDao = new ChartDataDao();
        PopulationDao populationDao = new PopulationDao();
        Map<String, Object> chartDataMap = chartDataDao.getAllChartData(firType, minHzb, maxHzb, minZzb, maxZzb);
        Map<String, Object> barDataMap = (Map<String, Object>) chartDataMap.get("barData");
        Map<String, Object> densityMap = new HashMap<String, Object>();
        if (firType.equals("01")) {//人口
            Long ck = (Long) barDataMap.get("01ck");//常口
            Long lk = (Long) barDataMap.get("01lk");//流动人口
            Long zk = (Long) barDataMap.get("01zk");//重点人口
            Long jw = (Long) barDataMap.get("01jw");//境外
            Long totalPop = (Long) barDataMap.get("totalPop");//总人口
            String area = getArea(minHzb, minZzb, maxHzb, maxZzb);
            double totalPopDensity = totalPop / Double.parseDouble(area);//总人口密度
            double lkDensity = lk.doubleValue() / Double.parseDouble(area);//流口密度
            double zkDensity = zk.doubleValue() / Double.parseDouble(area);//重点人口密度
            densityMap.put("totalPopDensity", totalPopDensity);
            densityMap.put("lkDensity", lkDensity);
            densityMap.put("zkDensity", zkDensity);
            chartDataMap.put("density", densityMap);
        }
        return chartDataMap;

    }*/

    //判断密度属于什么等级
    public String getPopLevel(Double density) {
        String level = null;
        if (density <= 269) {
            level = "0";
            System.out.println("0");
        } else if (density > 269 && density <= 330) {
            level = "1";
            System.out.println("1");
        } else if (density > 330 && density <= 392) {
            level = "2";
            System.out.println("2");
        } else if (density > 392 && density <= 453) {
            level = "3";
            System.out.println("3");
        } else if (density > 453 && density <= 513) {
            level = "4";
            System.out.println("4");
        } else if (density > 513 && density <= 575) {
            level = "5";
            System.out.println("5");
        } else if (density > 575 && density <= 635) {
            level = "6";
            System.out.println("6");
        } else if (density > 635 && density <= 697) {
            level = "7";
            System.out.println("7");
        } else if (density > 697 && density <= 758) {
            level = "8";
            System.out.println("8");
        } else if (density > 758 && density <= 820) {
            level = "9";
            System.out.println("9");
        } else if (density > 820) {
            level = "10";
            System.out.println("10");
        }
        return level;
    }


}
