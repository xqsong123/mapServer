package com.map.serviceImp;

import com.map.repository.PopulationDao;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AreaDensityServiceImpl {
    private Log log = LogFactory.getLog(AreaDensityServiceImpl.class);

    /* 根据派出所编号查询出该派出所辖区的密度值
     * @param xqbms  与shp文件中派出所的编号对应
     * @param type  第一大类区分标识,  1:人口
     * @param secType 第二大类区分标识   1:总人口密度  02：流口密度  03：重点人口密度
     * */
    public Map<String, Object> queryAreaDensity(List xqbms, String type, String secType) {
        PopulationDao populationDao = new PopulationDao();
        List list = populationDao.getAreaDensity(xqbms, type, secType);
        log.info("AreaDensityServiceImpl queryAreaDensity:" + list);
        Map<String, Object> resMap = new HashMap<String, Object>();
        for (int i = 0; i < list.size(); i++) {
            Map map = (Map) list.get(i);
            String xqbm = (String) map.get("xqbm");
            Long density = (Long) map.get("density");
            /*resMap.put("xqbm", xqbm);
            resMap.put("density", density);*/
            resMap.put(xqbm, density);
            //densityLst.add(resMap);
        }
        return resMap;
    }
}
