package com.map.entity;

import java.util.Map;

/**
 * 本类主要用于存放人口、案件等图表所需的数据
 */
public class Common {

    //存放人口分布与数量的关系
    private Map nums;

    //存放类别与人口密度的关系
    private Map popDensity;

    //存放案件分布与数量的关系
    private Map caseDistri;

    //存放类别与案件密度的关系
    private Map caseDensity;

    //存放年龄分布与数量的关系
    private Map ageDistri;

    //存放报警分布与数量的关系
    private Map alarmDistri;

    //存放类别与报警密度的关系
    private Map alarmDensity;

    public Map getNums() {
        return nums;
    }

    public void setNums(Map nums) {
        this.nums = nums;
    }

    public Map getPopDensity() {
        return popDensity;
    }

    public void setPopDensity(Map popDensity) {
        this.popDensity = popDensity;
    }

    public Map getCaseDensity() {
        return caseDensity;
    }

    public void setCaseDensity(Map caseDensity) {
        this.caseDensity = caseDensity;
    }

    public Map getAgeDistri() {
        return ageDistri;
    }

    public void setAgeDistri(Map ageDistri) {
        this.ageDistri = ageDistri;
    }

    public Map getCaseDistri() {
        return caseDistri;
    }

    public void setCaseDistri(Map caseDistri) {
        this.caseDistri = caseDistri;
    }

    public Map getAlarmDistri() {
        return alarmDistri;
    }

    public void setAlarmDistri(Map alarmDistri) {
        this.alarmDistri = alarmDistri;
    }

    public Map getAlarmDensity() {
        return alarmDensity;
    }

    public void setAlarmDensity(Map alarmDensity) {
        this.alarmDensity = alarmDensity;
    }
}
