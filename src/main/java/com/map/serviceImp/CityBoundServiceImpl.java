package com.map.serviceImp;

import com.map.entity.Global;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;

import java.util.HashMap;
import java.util.Map;

/**
 * 本类作为处理切换城市的应用类
 */
public class CityBoundServiceImpl {
    private Log log = LogFactory.getLog(CityBoundServiceImpl.class);

    public Map<String, Object> getCityName(double x, double y) throws Exception {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        if (null != Global.cityLst && Global.cityLst.size() != 0) {
            for (int i = 0; i < Global.cityLst.size(); i++) {
                Geometry obj = Global.cityLst.get(i);
                Point point = Global.geoCreatorByLtc.createPoint(x, y, null);
                if (obj.contains(point)) {
                    Map map = (Map) obj.getUserData();
                    for (Object key : map.keySet()) {
                        resultMap.put("city_id", map.get("city_id").toString());
                        resultMap.put("city_name", map.get("cname").toString());
                    }
                }
            }
        }
        return resultMap;
    }
}
