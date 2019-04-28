package com.map.serviceImp;

import com.map.repository.PopulationDao;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Date;
import java.util.List;

public class DynamicPopServiceImpl {
    private Log log = LogFactory.getLog(DynamicPopServiceImpl.class);

    //根据开始时间结束时间查询在一定范围内符合条件的人口
  /*  public List queryByTime(double minHzb, double maxHzb, double minZzb, double maxZzb, Date beginTime, Date endTime) throws Exception {
        PopulationDao PopulationDao = new PopulationDao();
        List popLst = PopulationDao.queryPopsByTime(minHzb, maxHzb, minZzb, maxZzb, beginTime, endTime, null, null);
        String temp = "";
        List removeLst = new ArrayList();
        for (int i = 0; i < popLst.size() - 1; i++) {
            Document obj1 = (Document) popLst.get(i);
            temp = obj1.getString("GMSFHM");
            for (int j = i + 1; j < popLst.size(); j++) {
                Document obj2 = (Document) popLst.get(j);
                if (temp.equals(obj2.getString("GMSFHM"))) {
                    System.out.println("第" + (i + 1) + "个跟第" + (j + 1) + "个重复，值是：" + temp);
                    int flag = compare_date(obj1.getString("DJSJ"), obj2.getString("DJSJ"));
                    if (flag == 1) {//date1为最新时间
                        removeLst.add(popLst.get(j));
                    } else if (flag == -1) {//date2为最新时间
                        removeLst.add(popLst.get(i));
                    }
                }
            }
        }
        if (removeLst.size() > 0) {
            popLst.removeAll(removeLst);
        }
        Calendar c = Calendar.getInstance();
        c.setTime(beginTime);
        c.add(Calendar.MONTH, 1);
        Date sBeginTime = c.getTime();
        //int month=c.get(Calendar.MONTH)+1;//得到月，因为从0开始的，所以要加1
        for (int n = 0; n < popLst.size(); n++) {
            Document obj = (Document) popLst.get(n);
            Date djsj = (Date) obj.get("DJSJ");
            if (belongCalendar(new Date(), beginTime, sBeginTime)) {//第一个月

            }

        }
        return null;
    }

    //比较时间大小
    public static int compare_date(String date1, String date2) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            Date dt1 = df.parse(date1);
            Date dt2 = df.parse(date2);
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

    */

    /**
     *      * 判断时间是否在时间段内
     *      * 
     *      * @param nowTime
     *      * @param beginTime
     *      * @param endTime
     *      * @return
     *     
     *//*
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
    }*/

    /*根据开始时间结束时间查询在一定范围内符合条件的人口
     * @param minHzb 最小横坐标
     * @param maxHzb 最大横坐标
     * @param minZzb 最小纵坐标
     * @param maxZzb 最大纵坐标
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @param gltype 11：常口   12：流动  20：境外
     * @param zdtype 查询重点人口的标识，值为Y时是重点人口
     * @param zdrkfl 重点人口分类
     * */
    public List queryByTime(double minHzb, double maxHzb, double minZzb, double maxZzb, Date beginTime, Date endTime, String gltype, String zdtype, String zdrkfl,double level) throws Exception {
        PopulationDao PopulationDao = new PopulationDao();
        List popLst = PopulationDao.queryPopsByTime(minHzb, maxHzb, minZzb, maxZzb, beginTime, endTime, gltype, zdtype, zdrkfl, level);
        return popLst;
    }

}
