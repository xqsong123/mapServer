package com.map.serviceImp;

import com.map.repository.PopulationDao;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JzwDetailServiceImpl {
    private Log log = LogFactory.getLog(JzwDetailServiceImpl.class);

    /* 根据建筑物编码查询建筑物的详情
     * @param jzwbm
     * */
    public Map queryByJzwbm(String jzwbm) {
        PopulationDao populationDao = new PopulationDao();
        Map map = (Map) populationDao.getJzwDetail(jzwbm);
        log.info("JzwDetailServiceImpl queryByJzwbm:" + map);
        if (map != null) {
            List roomInfoList = (List) map.get("roomInfoList");
            int allzdryNum = 0;//一栋建筑物重点人员数量
            int allczrkNum = 0;//一栋建筑物常住人口数量
            int allldrkNum = 0;//一栋建筑物流动人口数量
            int allwjrkNum = 0;//一栋建筑物外籍人口数量
            if (CollectionUtils.isNotEmpty(roomInfoList)) {
                for (int i = 0; i < roomInfoList.size(); i++) {
                    Map roomInfoMap = (Map) roomInfoList.get(i);
                    int zdryNum = (int) roomInfoMap.get("zdryNum"); //一间房间重点人员数量
                    allzdryNum = allzdryNum + zdryNum;
                    int czrkNum = (int) roomInfoMap.get("czrkNum"); //一间房间常住人口数量
                    allczrkNum = allczrkNum + czrkNum;
                    int ldrkNum = (int) roomInfoMap.get("ldrkNum");  //一间房间流动人口数量
                    allldrkNum = allldrkNum + ldrkNum;
                    int wjrkNum = (int) roomInfoMap.get("wjrkNum"); //一间房间外籍人口数量
                    allwjrkNum = allwjrkNum + wjrkNum;
                }
            }
            Map<String, Object> totalNumMap = new HashMap<String, Object>();
            totalNumMap.put("allzdryNum", allzdryNum);
            totalNumMap.put("allczrkNum", allczrkNum);
            totalNumMap.put("allldrkNum", allldrkNum);
            totalNumMap.put("allwjrkNum", allwjrkNum);
            map.put("totalRkNum", totalNumMap);
        }
        return map;
    }

    /* 根据人口编码查询人的详情
     * @param rkbm  人口编码
     * */
    public Map<String, Object> queryPersonInfoByRkbm(String rkbm) {
        PopulationDao populationDao = new PopulationDao();
        List list = populationDao.getPersonDetail(rkbm);
        log.info("JzwDetailServiceImpl queryPersonInfoByRkbm:" + list);
        Map<String, Object> resMap = new HashMap<String, Object>();
        if (CollectionUtils.isNotEmpty(list)) {
            Map map = (Map) list.get(0);
            resMap.put("rkbm", map.get("RKBM"));//人口编码
            if (map.get("SYRKGLLBDM") != null) {//SQJW_SYRKGLLB实有人口管理类别
                if (map.get("SYRKGLLBDM").toString().equals("11")) {//常口
                    resMap.put("rklb", "czrk");
                } else if (map.get("SYRKGLLBDM").toString().equals("12")) {//流口
                    resMap.put("rklb", "ldrk");
                } else if (map.get("SYRKGLLBDM").toString().equals("20")) {//境外
                    resMap.put("rklb", "jwrk");
                }
            }
            if (map.get("ZDRYBZ") != null) {//重点人员标识
                if (map.get("ZDRYBZ").toString().equals("Y")) {//重点人口
                    resMap.put("rklb", "zdrk");
                }
            }
            resMap.put("dzmc", map.get("DZMC"));//地址名称
            resMap.put("xm", map.get("XM"));//姓名
            resMap.put("sfzhm", map.get("GMSFHM"));//身份证号码
            if (map.get("XBDM") != null) {
                if (map.get("XBDM").toString().equals("1")) {//男
                    resMap.put("xb", "男");//性别
                } else if (map.get("XBDM").toString().equals("2")) {//女
                    resMap.put("xb", "女");//性别
                }
            }
            if (map.get("MZDM") != null) {//民族
                if (map.get("MZDM").equals("01")) {
                    resMap.put("mz", "汉族");//汉族
                } else if (map.get("MZDM").equals("02")) {//蒙古族
                    resMap.put("mz", "蒙古族");
                } else if (map.get("MZDM").equals("03")) {//回族
                    resMap.put("mz", "回族");
                } else if (map.get("MZDM").equals("04")) {//藏族
                    resMap.put("mz", "藏族");
                } else if (map.get("MZDM").equals("05")) {//维吾尔族
                    resMap.put("mz", "维吾尔族");
                } else if (map.get("MZDM").equals("06")) {//苗族
                    resMap.put("mz", "苗族");
                } else if (map.get("MZDM").equals("07")) {//彝族
                    resMap.put("mz", "彝族");
                } else if (map.get("MZDM").equals("56")) {//基诺族
                    resMap.put("mz", "基诺族");
                } else if (map.get("MZDM").equals("97")) {//其他
                    resMap.put("mz", "其他");
                } else if (map.get("MZDM").equals("98")) {//外国血统中国籍人士
                    resMap.put("mz", "外国血统中国籍人士");
                } else if (map.get("MZDM").equals("08")) {//壮族
                    resMap.put("mz", "壮族");
                } else if (map.get("MZDM").equals("09")) {//布依族
                    resMap.put("mz", "布依族");
                } else if (map.get("MZDM").equals("10")) {//朝鲜族
                    resMap.put("mz", "朝鲜族");
                } else if (map.get("MZDM").equals("11")) {//满族
                    resMap.put("mz", "满族");
                } else if (map.get("MZDM").equals("12")) {//侗族
                    resMap.put("mz", "侗族");
                } else if (map.get("MZDM").equals("13")) {//瑶族
                    resMap.put("mz", "瑶族");
                } else if (map.get("MZDM").equals("14")) {//白族
                    resMap.put("mz", "白族");
                } else if (map.get("MZDM").equals("15")) {//土家族
                    resMap.put("mz", "土家族");
                } else if (map.get("MZDM").equals("16")) {//哈尼族
                    resMap.put("mz", "哈尼族");
                } else if (map.get("MZDM").equals("17")) {//哈萨克族
                    resMap.put("mz", "哈萨克族");
                } else if (map.get("MZDM").equals("18")) {//傣族
                    resMap.put("mz", "傣族");
                } else if (map.get("MZDM").equals("19")) {//黎族
                    resMap.put("mz", "黎族");
                } else if (map.get("MZDM").equals("20")) {//傈僳族
                    resMap.put("mz", "傈僳族");
                } else if (map.get("MZDM").equals("21")) {//佤族
                    resMap.put("mz", "佤族");
                } else if (map.get("MZDM").equals("22")) {//畲族
                    resMap.put("mz", "畲族");
                } else if (map.get("MZDM").equals("23")) {//高山族
                    resMap.put("mz", "高山族");
                } else if (map.get("MZDM").equals("24")) {//拉祜族
                    resMap.put("mz", "拉祜族");
                } else if (map.get("MZDM").equals("25")) {//水族
                    resMap.put("mz", "水族");
                } else if (map.get("MZDM").equals("26")) {//东乡族
                    resMap.put("mz", "东乡族");
                } else if (map.get("MZDM").equals("27")) {//纳西族
                    resMap.put("mz", "纳西族");
                } else if (map.get("MZDM").equals("28")) {//景颇族
                    resMap.put("mz", "景颇族");
                } else if (map.get("MZDM").equals("29")) {//柯尔克族
                    resMap.put("mz", "柯尔克族");
                } else if (map.get("MZDM").equals("30")) {//土族
                    resMap.put("mz", "土族");
                } else if (map.get("MZDM").equals("31")) {//达斡尔族
                    resMap.put("mz", "达斡尔族");
                } else if (map.get("MZDM").equals("32")) {//仫佬族
                    resMap.put("mz", "仫佬族");
                } else if (map.get("MZDM").equals("33")) {//羌族
                    resMap.put("mz", "羌族");
                } else if (map.get("MZDM").equals("34")) {//布朗族
                    resMap.put("mz", "布朗族");
                } else if (map.get("MZDM").equals("35")) {//撒拉族
                    resMap.put("mz", "撒拉族");
                } else if (map.get("MZDM").equals("36")) {//毛南族
                    resMap.put("mz", "毛南族");
                } else if (map.get("MZDM").equals("37")) {//仡佬族
                    resMap.put("mz", "仡佬族");
                } else if (map.get("MZDM").equals("38")) {//锡伯族
                    resMap.put("mz", "锡伯族");
                } else if (map.get("MZDM").equals("39")) {//阿昌族
                    resMap.put("mz", "阿昌族");
                } else if (map.get("MZDM").equals("40")) {//普米族
                    resMap.put("mz", "普米族");
                } else if (map.get("MZDM").equals("41")) {//塔吉克族
                    resMap.put("mz", "塔吉克族");
                } else if (map.get("MZDM").equals("42")) {//怒族
                    resMap.put("mz", "怒族");
                } else if (map.get("MZDM").equals("43")) {//乌孜别克族
                    resMap.put("mz", "乌孜别克族");
                } else if (map.get("MZDM").equals("44")) {//俄罗斯族
                    resMap.put("mz", "俄罗斯族");
                } else if (map.get("MZDM").equals("45")) {//鄂温克族
                    resMap.put("mz", "鄂温克族");
                } else if (map.get("MZDM").equals("46")) {//德昂族
                    resMap.put("mz", "德昂族");
                } else if (map.get("MZDM").equals("47")) {//保安族
                    resMap.put("mz", "保安族");
                } else if (map.get("MZDM").equals("48")) {//裕固族
                    resMap.put("mz", "裕固族");
                } else if (map.get("MZDM").equals("49")) {//京族
                    resMap.put("mz", "京族");
                } else if (map.get("MZDM").equals("50")) {//塔塔尔族
                    resMap.put("mz", "塔塔尔族");
                } else if (map.get("MZDM").equals("51")) {//独龙族
                    resMap.put("mz", "独龙族");
                } else if (map.get("MZDM").equals("52")) {//鄂伦春族
                    resMap.put("mz", "鄂伦春族");
                } else if (map.get("MZDM").equals("53")) {//赫哲族
                    resMap.put("mz", "赫哲族");
                } else if (map.get("MZDM").equals("54")) {//门巴族
                    resMap.put("mz", "门巴族");
                } else if (map.get("MZDM").equals("55")) {//珞巴族
                    resMap.put("mz", "珞巴族");
                }
            }
            if (map.get("CSRQ") != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                resMap.put("csrq", sdf.format(map.get("CSRQ")));//出生日期
            } else {
                resMap.put("csrq", map.get("CSRQ"));//出生日期
            }
            resMap.put("lxdh", map.get("LXDH"));//联系电话
            resMap.put("hjd", map.get("HJD"));//户籍地
            resMap.put("sssj", map.get("SSSJ"));//所属市局
            resMap.put("ssfxj", map.get("SSFXJ"));//所属分县局
            resMap.put("sspcs", map.get("SSPCS"));//所属派出所
            resMap.put("sszrq", map.get("SSZRQ"));//所属责任区
            resMap.put("hz_hjd", map.get("HZ_HJD"));//户政户籍地，来源于户政系统
            return resMap;
        }
        return null;
    }
}
