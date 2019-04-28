package com.map.serviceImp;

import com.map.repository.DwDao;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DwServiceImpl {
    private Log log = LogFactory.getLog(DwServiceImpl.class);

    /* 根据一定范围查询所有的单位
     * firtype   2:普通单位   3:特种单位  4：保护单位    5：九小场所
     * sectype  饼图中的单位分类
     * @param minHzb 最小横坐标
     * @param maxHzb 最大横坐标
     * @param minZzb 最小纵坐标
     * @param maxZzb 最大纵坐标
     * */
    public List<Map<String, Object>> queryAllDw(String firtype, String sectype, double minHzb, double maxHzb, double minZzb, double maxZzb) {
        DwDao dwDao = new DwDao();
        List dwList = dwDao.queryDw(firtype, sectype, minHzb, maxHzb, minZzb, maxZzb);
        List<Map<String, Object>> dwResLst = new ArrayList();
        for (int i = 0; i < dwList.size(); i++) {
            Map map = (Map) dwList.get(i);
            Map<String, Object> resMap = new HashMap<String, Object>();
            resMap.put("zagldwbm", map.get("ZAGLDWBM"));//单位地址编码
            //resMap.put("dwfl", map.get("GAGL_DWFL"));//单位分类
            //resMap.put("sfzddw", map.get("ZDDWBS"));//是否重点单位
            //resMap.put("sfjxcs", map.get("SFJXCS"));//是否九小场所
            resMap.put("hzb", map.get("ZXDHZB"));
            resMap.put("zzb", map.get("ZXDZZB"));
            dwResLst.add(resMap);
        }
        return dwResLst;
    }

    /* 根据单位地址编码查询单位详情
     * @param dwdzbm 单位地址编码
     * */
    public Map<String, Object> queryDwDetail(String zagldwbm) {
        DwDao dwDao = new DwDao();
        List dwDetailLst = dwDao.getdwDetails(zagldwbm);
        log.info("DwServiceImpl queryDwDetail:" + dwDetailLst);
        Map map = (Map) dwDetailLst.get(0);
        Map<String, Object> resMap = new HashMap<String, Object>();
        resMap.put("dzmc", map.get("DWMC"));//单位名称
        resMap.put("dwlx", map.get("DWLXDM"));//单位类型
        resMap.put("frdb_sfzhm", map.get("FDDBR_GMSFHM"));//法定代表人公民身份号码
        resMap.put("dwfl", map.get("GAGL_DWFL"));//单位分类
        resMap.put("frdb_xm", map.get("FDDBR_XM"));//法定代表人姓名
        resMap.put("frdb_lxdh", map.get("FDDBR_LXDH"));//法定代表人联系电话
        resMap.put("zddwbs", map.get("ZDDWBS"));//重点单位标识
        resMap.put("sfjxcs", map.get("SFJXCS"));//是否九小场所Y/N
        resMap.put("wz", map.get("WZ"));//网址
        resMap.put("lxdh", map.get("LXDH"));//联系电话
        resMap.put("zczj", map.get("ZCZB"));//注册资金万元
        resMap.put("zy", map.get("JYFWZY"));//经营范围_主营
        resMap.put("jy", map.get("JYFWJY"));//经营范围_兼营
        resMap.put("jymj", map.get("JYMJ_MJPFM"));//经营面积面积平方米
        if (map.get("JYZT") != null) {//经营状态SQJW_JYZT    1:歇业  2:停业  3: 转业 4: 正常 5:注销
            if (map.get("JYZT").toString().equals("1")) {
                resMap.put("jyzt", "歇业");
            } else if (map.get("JYZT").toString().equals("2")) {
                resMap.put("jyzt", "停业");
            } else if (map.get("JYZT").toString().equals("3")) {
                resMap.put("jyzt", "转业");
            } else if (map.get("JYZT").toString().equals("4")) {
                resMap.put("jyzt", "正常");
            } else if (map.get("JYZT").toString().equals("5")) {
                resMap.put("jyzt", "注销");
            }
        } else {
            resMap.put("jyzt", map.get("JYZT"));
        }
        resMap.put("cyryzs", map.get("CYRYZS"));//从业人员总数
        resMap.put("aqcktds", map.get("AQCRKTDS"));//安全出入口通道数
        resMap.put("zgdw", map.get("ZGDW")); //主管单位
        resMap.put("sfss", map.get("SFSS")); //三防设施情况
        if (map.get("GAJGBASJ") != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            resMap.put("gajgbasj", sdf.format(map.get("GAJGBASJ")));//公安机关备案时间
        } else {
            resMap.put("gajgbasj", map.get("GAJGBASJ"));//公安机关备案时间
        }
        resMap.put("bwfzr_xm", map.get("BWFZR_XM"));//保卫负责人姓名
        resMap.put("bwfzr_lxdh", map.get("BWFZR_LXDH"));//保卫负责人联系电话
        resMap.put("sssj", map.get("SSSJ"));//所属市局
        resMap.put("ssfxj", map.get("SSFXJ"));//所属分县局
        resMap.put("sspcs", map.get("SSPCS"));//所属派出所
        resMap.put("sszrq", map.get("SSZRQ"));//所属责任区
        resMap.put("yyzzh", map.get("YYZZH")); //营业执照号
        resMap.put("dwdz_ssxqdz", map.get("DWDZ_SSXQDM")); //单位地址_省市县区
        resMap.put("dwdz_qhnxxdz", map.get("DWDZ_QHNXXDZ")); //单位地址_区划内详细地址
        resMap.put("sfyyyzz", map.get("SFYYYZZ"));//是否有营业执照（Y/N)
        resMap.put("jyfs", map.get("JYFS"));//经营方式
        return resMap;
    }
}
