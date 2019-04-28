package com.map.svc;

import com.map.service.*;
import com.wdtinc.mapbox_vector_tile.util.ParameterParser;
import net.sf.json.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;

/**
 * 该类为请求的统一入口
 */
public class ServiceFactory {

    private static Log log = LogFactory.getLog(ServiceFactory.class);

    public static Service getService(HttpServletRequest req) {

       /* log.info(req.getRequestURI());
        System.out.println(req.getRequestURI() + "===" +
                req.getQueryString());
        String query = req.getQueryString();
        if(query != null){
            if(query.indexOf("dataRest") > 0){
                return new DataRestService();
            }
        }*/

        //获取前端的传参
        String jStr = ParameterParser.getParm(req);
        String test = JSONObject.fromObject(jStr).get("test").toString();
        if (null != JSONObject.fromObject(jStr).get("dataRest")) {
            return new DataRestService(jStr);
        }
        if (test.equals("200")) {
            return new TileDownLoadService();
        }
       /* else if (test.equals("pop")) {
            //返回给定范围内的人口点
            return new PopulationService(jStr);
        } else if (test.equals("storeFile")) {
            //存储安保路线
            return new StoreRoutesService(jStr);
        } else if (test.equals("dynamicPop")) {
            //动态播放报警变化
            return new DynamicPopService(jStr);
        } else if (test.equals("chartData")) {
            //右侧统计图数据
            return new ChartDataService(jStr);
        } else if (test.equals("queryFile")) {
            //查询安保路线
            return new QueryRoutesService(jStr);
        } else if (test.equals("1")) {
            return new ReloadService();
        } else if (test.equals("switchCity")) {
            //工具栏切换城市
            return new CityBoundService(jStr);
        }else if (test.equals("policeDaily")) {
            //给定范围内的工作日常
            return new PoliceDailyService(jStr);
        } else if (test.equals("cameraNum")) {
            //给定范围内的摄像头数据
            return new CameraNumService(jStr);
        } else if (test.equals("cameraInPolygon")) {
            //在多边形或者圆形范围内的摄像头数据
            return new CamerasInPolygonService(jStr);
        } else if (test.equals("alarmNum")) {
            //给定范围内的报警数据
            return new AlarmNumService(jStr);
        } else if (test.equals("divDistance")) {
            //对安保路线进行划分更密的点
            return new DivideDistanceService(jStr);
        } else if (test.equals("getAllFileNames")) {
            //获取所有保存安保路线的文件名
            return new FileNamesService();
        } else if (test.equals("case")){
            //获取各类案件数据
            return new DynamicCaseService(jStr);
        }*/
        else if ("routePlanning".equals(test)) {
            return new RoutePlanningService(jStr);
        } else if (test.equals("popDynamic")) {//人口热力图
            return new PopService(jStr);
        } else if (test.equals("jzwDetail")) {//建筑物详情
            return new JzwDetailService(jStr);
        } else if (test.equals("popDensity")) {//密度图
            return new AreaDensityService(jStr);
        } else if (test.equals("chartData")) {//右侧统计图数据
            return new ChartDataService(jStr);
        } else if (test.equals("fwData")) {//一定范围内所有房屋
            return new AllFwService(jStr);
        } else if (test.equals("dwData")) {//一定范围内所有单位
            return new DwService(jStr);
        } else if (test.equals("personDetail")) {//人口详情
            return new PersonDetailService(jStr);
        } else if (test.equals("dwDetail")) {//单位详情
            return new DwDetailService(jStr);
        } else if (test.equals("fwDetail")) {//房屋详情
            return new FwDetailService(jStr);
        } else if (test.equals("getNum")) {//获取3D建筑物上铭牌数字接口
            return new QueryNumByTypeService(jStr);
        } else if (test.equals("getThirNum")) {//点击重点人口饼图查询更细一级分类的数量
            return new QueryThirTypesNumService(jStr);
        } else if (test.equals("dynamicCase")) {//案件热力图接口
            return new DynamicCaseService(jStr);
        } else if (test.equals("case")) {//案件点位图接口
            return new CasesService(jStr);
        } else if (test.equals("getSecNum")) {//根据一级案件查询该类型下的所有二级案件类型和数量
            return new QuerySecCaseNumService(jStr);
        } else if (test.equals("caseDetail")) {//案件详情
            return new CaseDetailService(jStr);
        } else if (test.equals("caseChartData")) {//右侧案件tab页数据
            return new CaseChartDataService(jStr);
        } else if (test.equals("caseDensity")) {//派出所辖区的密度图接口
            return new CaseAreaDensityService(jStr);
        }
        return null;
    }
}
