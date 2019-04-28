package com.map.serviceImp;

import com.map.repository.FwDao;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AllFwServiceImpl {
    private Log log = LogFactory.getLog(AllFwServiceImpl.class);

    /* 获取一定范围内所有房屋地址和编码
     * type 1:自用   2：出租   3：闲置
     * @param minHzb最小横坐标
     * @param maxHzb最大横坐标
     * @param minZzb最小纵坐标
     * @param maxZzb最大纵坐标
     * */
    public List<Map<String, Object>> getAllFw(String type, double minHzb, double maxHzb, double minZzb, double maxZzb) throws Exception {
        FwDao fwDao = new FwDao();
        List fwLst = fwDao.queryBulidings(type, minHzb, maxHzb, minZzb, maxZzb);
        log.info("AllFwServiceImpl getAllFw:" + fwLst);
        List<Map<String, Object>> fwResLst = new ArrayList();
        for (int i = 0; i < fwLst.size(); i++) {
            Map map = (Map) fwLst.get(i);
            Map<String, Object> resMap = new HashMap<String, Object>();
            resMap.put("dzbm", map.get("DZBM"));
            resMap.put("syxs", map.get("SYXS"));
            resMap.put("hzb", map.get("ZXDHZB"));
            resMap.put("zzb", map.get("ZXDZZB"));
            fwResLst.add(resMap);
        }
        return fwResLst;
    }

    /*根据房屋编码查询房屋相关信息
     * @param  dzbm 房屋编码
     * */
    public Map<String, Object> queryInfoByFw(String dzbm) throws Exception {
        FwDao jzwDao = new FwDao();
        List jzwInfoLst = jzwDao.queryBulidingsByDz(dzbm);
        log.info("AllFwServiceImpl queryInfoByFw:" + jzwInfoLst);
        Map<String, Object> resMap = new HashMap<String, Object>();
        Document document = (Document) jzwInfoLst.get(0);
        resMap.put("dzmc", document.get("DZMC"));//地址名称
        if (document.get("SYXS") != null) { //使用形式（非标准，SQJW_SYXS） 1：自用       2：出租    3：闲置
            if (document.get("SYXS").equals("1")) {
                resMap.put("syxs", "自用");
            } else if (document.get("SYXS").equals("2")) {
                resMap.put("syxs", "出租");
            } else if (document.get("SYXS").equals("3")) {
                resMap.put("syxs", "闲置");
            }
        } else {
            resMap.put("syxs", document.get("SYXS"));
        }
        if (document.get("FWYTDM") != null) {//房屋用途SQJW_FWYT   10:住宅  20：工业、交通、仓储  30：商业、金融、信息 40：教育、医疗、卫生、科研 50：文化、娱乐、体育 60：办公  70:军事  90：其他
            if (document.get("FWYTDM").toString().equals("10")) {
                resMap.put("fwyt", "住宅");
            } else if (document.get("FWYTDM").toString().equals("20")) {
                resMap.put("fwyt", "工业、交通、仓储");
            } else if (document.get("FWYTDM").toString().equals("30")) {
                resMap.put("fwyt", "商业、金融、信息");
            } else if (document.get("FWYTDM").toString().equals("40")) {
                resMap.put("fwyt", "教育、医疗、卫生、科研");
            } else if (document.get("FWYTDM").toString().equals("50")) {
                resMap.put("fwyt", "文化、娱乐、体育");
            } else if (document.get("FWYTDM").toString().equals("60")) {
                resMap.put("fwyt", "办公");
            } else if (document.get("FWYTDM").toString().equals("70")) {
                resMap.put("fwyt", "军事");
            } else if (document.get("FWYTDM").toString().equals("90")) {
                resMap.put("fwyt", "其他");
            }
        } else {
            resMap.put("fwyt", document.get("FWYTDM"));
        }
        if (document.get("FWLBDM") != null) {//房屋类别（SQJW_FWLB）10:单元楼、公寓楼 20:筒子楼 30:别墅 40:自建楼 50:平房 60:四合院 90:其他
            if (document.get("FWLBDM").toString().equals("10")) {
                resMap.put("fwlb", "单元楼、公寓楼");
            } else if (document.get("FWLBDM").toString().equals("20")) {
                resMap.put("fwlb", "筒子楼");
            } else if (document.get("FWLBDM").toString().equals("30")) {
                resMap.put("fwlb", "别墅");
            } else if (document.get("FWLBDM").toString().equals("40")) {
                resMap.put("fwlb", "自建楼");
            } else if (document.get("FWLBDM").toString().equals("50")) {
                resMap.put("fwlb", "平房");
            } else if (document.get("FWLBDM").toString().equals("60")) {
                resMap.put("fwlb", "四合院");
            } else if (document.get("FWLBDM").toString().equals("90")) {
                resMap.put("fwlb", "其他");
            }
        } else {
            resMap.put("fwlb", document.get("FWLBDM"));
        }
        if (document.get("FWCQXZZLDM") != null) {//房屋性质（SQJW_FWXZ） 10:国有房产 20:集体所有房产 30:私有房产 40:联营企业房产 50: 股份制企业房产 60: 港、澳、台胞房产 70: 涉外房产 90:其他
            if (document.get("FWCQXZZLDM").toString().equals("10")) {
                resMap.put("fwxz", "国有房产");
            } else if (document.get("FWCQXZZLDM").toString().equals("20")) {
                resMap.put("fwxz", "集体所有房产");
            } else if (document.get("FWCQXZZLDM").toString().equals("30")) {
                resMap.put("fwxz", "私有房产");
            } else if (document.get("FWCQXZZLDM").toString().equals("40")) {
                resMap.put("fwxz", "联营企业房产");
            } else if (document.get("FWCQXZZLDM").toString().equals("50")) {
                resMap.put("fwxz", "股份制企业房产");
            } else if (document.get("FWCQXZZLDM").toString().equals("60")) {
                resMap.put("fwxz", "港、澳、台胞房产");
            } else if (document.get("FWCQXZZLDM").toString().equals("70")) {
                resMap.put("fwxz", "涉外房产");
            } else if (document.get("FWCQXZZLDM").toString().equals("90")) {
                resMap.put("fwxz", "其他");
            }
        } else {
            resMap.put("fwxz", document.get("FWCQXZZLDM"));
        }
        resMap.put("fwjs", document.get("FWJS"));//房屋间数
        resMap.put("fwmj", document.get("FWMJ_MJPFM"));//房屋面积：面积平方米
        resMap.put("fwcqzh", document.get("FWCQZH"));//房屋产权证号
        resMap.put("fz_xm", document.get("FZ_GMSFHM"));//房主姓名
        resMap.put("fz_lxdh", document.get("FZ_LXDH"));//房主联系电话
        resMap.put("fz_sfzhm", document.get("FZ_GMSFHM"));//房主公民身份号码
        resMap.put("tgr_xm", document.get("TGR_XM"));//托管人姓名
        resMap.put("tgr_gmsfzhm", document.get("TGR_GMSFHM"));//托管人公民身份号码
        resMap.put("tgr_lxdh", document.get("TGR_LXDH"));// 托管人联系电话
        resMap.put("fwssdw_dwmc", document.get("FWSSDW_DWMC"));//房屋所属单位_单位名称
        resMap.put("sssj", document.get("SSSJ"));//所属市局
        resMap.put("sspcs", document.get("SSPCS"));//所属派出所
        resMap.put("ssfxj", document.get("SSFXJ"));//所属分县局
        resMap.put("sszrq", document.get("SSZRQ"));//所属责任区
        return resMap;
    }


}
