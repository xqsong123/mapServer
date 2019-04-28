package com.map.serviceImp;

import com.map.repository.QuantityDao;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QueryThirTypesNumServiceImpl {
    private Log log = LogFactory.getLog(QueryThirTypesNumServiceImpl.class);

    /* 获取网安A级、网安B级、网安C级、假币、传销、贿赂、涉稳等重点人口类型的数量
     * @param  type    类型编码
     * @param minHzb 最小横坐标
     * @param maxHzb 最大横坐标
     * @param minZzb 最小纵坐标
     * @param maxZzb 最大纵坐标
     * */
    public Map<String, Object> getPopThirNum(String type, double minHzb, double maxHzb, double minZzb, double maxZzb) {
        List typeLst = new ArrayList();
        if (type.equals("304000000000")) {//网安
            typeLst.add("340100000000");//网安A级
            typeLst.add("340200000000");//网安B级
            typeLst.add("340300000000");//网安C级
        } else if (type.equals("405000000000")) {//经侦
            typeLst.add("450100000000");//假币
            typeLst.add("450200000000");//传销
            typeLst.add("450300000000");//贿赂
            typeLst.add("450800000000");//涉稳
            typeLst.add("450500000000");//集资
            typeLst.add("450600000000");//洗钱
            typeLst.add("450700000000");//知识产权
            typeLst.add("450400000000");//银行卡
        } else if (type.equals("203000000000")) {//刑警
            typeLst.add("230300000000");//重点场所涉案
            typeLst.add("230200000000");//黑恶犯罪涉案
            typeLst.add("230100000000");//涉黑打击处理
        } else if (type.equals("102000000000")) {//户政
            typeLst.add("120300000000");//潜在风险
            typeLst.add("120400000000");//刑释未满五年
            typeLst.add("120500000000");//吸毒
            typeLst.add("120100000000");//危害国家安全
            typeLst.add("120700000000");//流动重点人员
            typeLst.add("120200000000");//刑事犯罪嫌疑
            typeLst.add("120600000000");//其他刑事人员
        } else if (type.equals("501000000000")) {//禁毒
            typeLst.add("510100000000");//目标案件嫌疑
            typeLst.add("510200000000");//一般案件嫌疑
            typeLst.add("510300000000");//其他嫌疑人员
        } else if (type.equals("001000000000")) {//情报
            typeLst.add("080000000000");//管控对象
            typeLst.add("090000000000");//关注对象
            typeLst.add("011000000000");//知悉对象
            typeLst.add("050000000000");//前科人员
            typeLst.add("999999999999");//其他重点人员
            typeLst.add("040000000000");//涉毒人员
            typeLst.add("070000000000");//重点上访人员
            typeLst.add("010000000000");//涉恐人员
            typeLst.add("020000000000");//涉稳人员
            typeLst.add("030000000000");//在逃人员
            typeLst.add("060000000000");//精神病人
        }  else if (type.equals("701000000000")) {//反邪教
            typeLst.add("710300000000");//有害气功组织
            typeLst.add("710200000000");//法轮功人员
            typeLst.add("710100000000");//邪教组织人员
        } else if (type.equals("801000000000")) {//反恐
            typeLst.add("810100000000");//列控人
            typeLst.add("810200000000");//重点人
            typeLst.add("810500000000");//准重点人
            typeLst.add("810400000000");//基础群体
        } else if (type.equals("901000000000")) {//交警
            typeLst.add("910200000000");//关注人员
            typeLst.add("910100000000");//管控人员
        } else if (type.equals("120800000000")) {//泽雨
            typeLst.add("120900000001");//持卡人
            typeLst.add("121200000001");//投资人
            typeLst.add("121300000001");//关系人
            typeLst.add("121100000001");//员工
            typeLst.add("121000000002");//供货人
            typeLst.add("121400000001");//其它
        }
        QuantityDao quantityDao = new QuantityDao();
        Map<String, Object> resMap = quantityDao.queryPopTypesNum(typeLst, minHzb, maxHzb, minZzb, maxZzb);
        return resMap;
    }

}
