package com.map.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.map.dto.GeometryDto;
import com.map.entity.Global;
import com.map.geometryFilter.PointPairInterceptFilter;
import com.map.serviceImp.DataRestServiceImpl;
import com.map.svc.Service;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.locationtech.jts.algorithm.distance.DistanceToPoint;
import org.locationtech.jts.algorithm.distance.PointPairDistance;
import org.locationtech.jts.geom.*;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.*;

/**
 * @author xqsong
 * @create 2018/12/25
 * @since 1.0.0
 **/
public class RoutePlanningService extends Service {

    private final Log log = LogFactory.getLog(RoutePlanningService.class);

    private final double THRESHOLD = 3.3*1.0E-5;

    private String requestParam;

    public RoutePlanningService(String requestParam) {
        this.requestParam = requestParam;
    }

    @Override
    public String doService(HttpServletRequest req, HttpServletResponse res) throws IOException {
        JSONObject jsonObject = JSONObject.parseObject(this.requestParam);
        String order = jsonObject.getString("order");
        if ("switchScreen".equals(order)) {
            JSONObject jsonObject1 = jsonObject.getJSONObject("bound");
            JSONObject jsonObject2 = jsonObject1.getJSONObject("_sw");
            JSONObject jsonObject3 = jsonObject1.getJSONObject("_ne");
            double x1 = jsonObject2.getDoubleValue("lng");
            double y1 = jsonObject2.getDoubleValue("lat");
            double x2 = jsonObject3.getDoubleValue("lng");
            double y2 = jsonObject3.getDoubleValue("lat");
            Envelope envelope = new Envelope(x1, x2, y1, y2);
            String status = "success";
            String message = "";
            Map<String, LineString> roads = new LinkedHashMap<>();
            try {
                roads = switchScreen(Global.roadMap, expandEnvelope(envelope, 1));
            } catch (Exception e) {
                status = "Exception";
                message = e.getMessage();
                log.info(message);
                e.printStackTrace();
            }
            Collection<String> ids = roads.keySet();
            JSONObject jsonObject4 = new JSONObject();
            jsonObject4.put("data", ids);
            jsonObject4.put("status", status);
            jsonObject4.put("message", message);
            res.setContentType("application/json;charset=UTF-8");
            res.setHeader("Access-Control-Allow-Origin", "*");
            PrintWriter out = res.getWriter();
            out.write(jsonObject4.toJSONString());
        } else if ("first".equals(order)) {
            JSONObject jsonObject1 = jsonObject.getJSONObject("bound");
            JSONObject jsonObject2 = jsonObject1.getJSONObject("_sw");
            JSONObject jsonObject3 = jsonObject1.getJSONObject("_ne");
            JSONObject jsonObject4 = jsonObject.getJSONObject("coord");
            double x1 = jsonObject2.getDoubleValue("lng");
            double y1 = jsonObject2.getDoubleValue("lat");
            double x2 = jsonObject3.getDoubleValue("lng");
            double y2 = jsonObject3.getDoubleValue("lat");
            double x = jsonObject4.getDoubleValue("lng");
            double y = jsonObject4.getDoubleValue("lat");
            Envelope envelope = new Envelope(x1, x2, y1, y2);
            Point point = new GeometryFactory().createPoint(new Coordinate(x, y));
            String status = "success";
            String message = "";
            Map<String, LineString> roads = new HashMap<>();
            Set<Point> intersections;
            Set<Point> resultPoints = new HashSet<>();
            try {
                roads = switchScreen(Global.roadMap, expandEnvelope(envelope, 0.5));
                resultPoints = firstPoint(point, roads);
            } catch (Exception e) {
                status = "Exception";
                message = e.getMessage();
                log.info(message);
                e.printStackTrace();
            }
            Collection<String> ids = roads.keySet();

            GeometryDto[] points = new GeometryDto[resultPoints.size()];
            Iterator<Point> iterator = resultPoints.iterator();
            int i = 0;
            while (iterator.hasNext()) {
                Point point1 = iterator.next();
                points[i++] = new GeometryDto(point1);
            }

            Map<String, Object> data = new HashMap<>();
            data.put("points", points);
            data.put("ids", ids);
            Map<String, Object> result = new HashMap<>();
            result.put("data", data);
            result.put("status", status);
            result.put("message", message);
            JSONObject jsonObject5 = new JSONObject(result);
            res.setContentType("application/json;charset=UTF-8");
            res.setHeader("Access-Control-Allow-Origin", "*");
            PrintWriter out = res.getWriter();
            out.write(jsonObject5.toJSONString());
        } else if ("firstPlus".equals(order)) {
            GeometryFactory geometryFactory = new GeometryFactory();
            String status = "success";
            String message = "";
            JSONArray jsonArray1 = jsonObject.getJSONObject("prev").getJSONArray("coordinates");
            Coordinate coordinate1 = new Coordinate(((JSONObject) jsonArray1.get(0)).getDoubleValue("x"), ((JSONObject) jsonArray1.get(0)).getDoubleValue("y"));
            JSONArray jsonArray4 = jsonObject.getJSONObject("prev").getJSONArray("userData");
            List<String> userData1 = new LinkedList<>();
            Set<Geometry> geometrySet = new HashSet<>();
            for (int i = 0; i < jsonArray4.size(); i++) {
                userData1.add((String) jsonArray4.get(i));
            }
            Point startPoint = geometryFactory.createPoint(coordinate1);
            startPoint.setUserData(userData1);

            JSONArray jsonArray2 = jsonObject.getJSONObject("suff").getJSONArray("coordinates");
            Coordinate coordinate2 = new Coordinate(((JSONObject) jsonArray2.get(0)).getDoubleValue("x"), ((JSONObject) jsonArray2.get(0)).getDoubleValue("y"));
            JSONArray jsonArray5 = jsonObject.getJSONObject("suff").getJSONArray("userData");
            List<String> userData2 = new LinkedList<>();
            for (int i = 0; i < jsonArray5.size(); i++) {
                userData2.add((String) jsonArray5.get(i));
            }
            Point endPoint = geometryFactory.createPoint(coordinate2);
            endPoint.setUserData(userData2);

            List<String> ids = new LinkedList<>();
            JSONArray jsonArray = jsonObject.getJSONArray("ids");
            for (int i = 0; i < jsonArray.size(); i++) {
                String id = jsonArray.getString(i);
                ids.add(id);
            }
            try {
                geometrySet = firstPlusPoint(Global.roadMap, ids, startPoint, endPoint);
            } catch (Exception e) {
                status = "Exception";
                message = e.getMessage();
                log.info(message);
                e.printStackTrace();
            }
            GeometryDto[] geometryDtos = new GeometryDto[geometrySet.size()];
            Iterator<Geometry> iterator = geometrySet.iterator();
            int i = 0;
            //判断是否是环形路（两个路有多个交点）,此时lineString的数量大于1
            boolean isLineRing;
            int lineStringCount = 0;
            while (iterator.hasNext()) {
                Geometry geometry = iterator.next();
                geometryDtos[i++] = new GeometryDto(geometry);
                if (geometry instanceof LineString) {
                    lineStringCount++;
                }
            }
            isLineRing = lineStringCount > 1 ? true : false;
            Map<String, Object> data = new HashMap<>();
            data.put("coord", geometryDtos);
            data.put("isLineRing", isLineRing);
            Map<String, Object> result = new HashMap<>();
            result.put("data", data);
            result.put("status", status);
            result.put("message", message);
            JSONObject jsonObject5 = new JSONObject(result);
            res.setContentType("application/json;charset=UTF-8");
            res.setHeader("Access-Control-Allow-Origin", "*");
            PrintWriter out = res.getWriter();
            out.write(jsonObject5.toJSONString());
        } else if ("forLast".equals(order)) {
            GeometryDto[] geometryDtos = new GeometryDto[1];
            String status = "success";
            String message = "";
            try {
                JSONArray jsonArray = jsonObject.getJSONObject("coord").getJSONArray("coordinates");
                Coordinate coordinate = new Coordinate(((JSONObject) jsonArray.get(0)).getDoubleValue("x"), ((JSONObject) jsonArray.get(0)).getDoubleValue("y"));
                Point point = new GeometryFactory().createPoint(coordinate);
                JSONArray jsonArray1 = jsonObject.getJSONObject("coord").getJSONArray("userData");
                List<String> userData = new LinkedList<>();
                for (int i = 0; i < jsonArray1.size(); i++) {
                    String id = jsonArray1.getString(i);
                    userData.add(id);
                }
                point.setUserData(userData);
                Set<LineString> lineStringSet = forLast(Global.roadMap, point);
                geometryDtos = new GeometryDto[lineStringSet.size()];
                Iterator<LineString> iterator = lineStringSet.iterator();
                int i = 0;
                while (iterator.hasNext()) {
                    geometryDtos[i++] = new GeometryDto(iterator.next());
                }
            } catch (Exception e) {
                status = "Exception";
                message = e.getMessage();
                log.info(message);
                e.printStackTrace();
            }
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("data", geometryDtos);
            jsonObject1.put("status", status);
            jsonObject1.put("message", message);
            res.setContentType("application/json;charset=UTF-8");
            res.setHeader("Access-Control-Allow-Origin", "*");
            PrintWriter out = res.getWriter();
            out.write(jsonObject1.toJSONString());
        } else if ("last".equals(order)) {
            GeometryFactory geometryFactory = new GeometryFactory();
            String status = "success";
            String message = "";
            JSONArray jsonArray1 = jsonObject.getJSONObject("prev").getJSONArray("coordinates");
            Coordinate coordinate1 = new Coordinate(((JSONObject) jsonArray1.get(0)).getDoubleValue("x"), ((JSONObject) jsonArray1.get(0)).getDoubleValue("y"));
            JSONArray jsonArray4 = jsonObject.getJSONObject("prev").getJSONArray("userData");
            List<String> userData1 = new LinkedList<>();
            for (int i = 0; i < jsonArray4.size(); i++) {
                userData1.add((String) jsonArray4.get(i));
            }
            Point startPoint = geometryFactory.createPoint(coordinate1);
            startPoint.setUserData(userData1);

            JSONObject jsonObject1 = jsonObject.getJSONObject("suff");
            Coordinate coordinate = new Coordinate(jsonObject1.getDoubleValue("lng"), jsonObject1.getDoubleValue("lat"));
            Point endPoint = geometryFactory.createPoint(coordinate);

            Set<Geometry> geometrySet = new HashSet<>();
            try {
                geometrySet = lastPoint(Global.roadMap, startPoint, endPoint);
            } catch (Exception e) {
                status = "Exception";
                message = e.getMessage();
                log.info(message);
                e.printStackTrace();
            }
            GeometryDto[] geometryDtos = new GeometryDto[geometrySet.size()];
            Iterator<Geometry> iterator = geometrySet.iterator();
            int i = 0;
            while (iterator.hasNext()) {
                geometryDtos[i++] = new GeometryDto(iterator.next());
            }
            Map<String, Object> result = new HashMap<>();
            result.put("data", geometryDtos);
            result.put("status", status);
            result.put("message", message);
            JSONObject jsonObject5 = new JSONObject(result);
            res.setContentType("application/json;charset=UTF-8");
            res.setHeader("Access-Control-Allow-Origin", "*");
            PrintWriter out = res.getWriter();
            out.write(jsonObject5.toJSONString());
        }
        return "success";
    }


    /**
     *
     * 返回当前屏幕中包含的所有路的id
     * @param roads
     * @param envelope
     * @return
     */
    private Map<String, LineString> switchScreen(Map<String, LineString> roads, Envelope envelope) {
        Map<String, LineString> currentScreenroads = new HashMap<>();
        //Envelope expand = expandEnvelope(envelope, 1);
        Set<Map.Entry<String, LineString>> entries = roads.entrySet();
        Iterator<Map.Entry<String, LineString>> iterator = entries.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, LineString> entry = iterator.next();
            if (envelope.intersects(entry.getValue().getEnvelopeInternal())) {
                currentScreenroads.put(entry.getKey(), entry.getValue());
            }
        }
        DataRestServiceImpl.modifyRoadData(currentScreenroads);
        return currentScreenroads;
    }

    /**
     * 处理第一个点，给定起点，返回投影点、所在路的id与该路的起点、终点、交点
     *
     * @param point           路线中第一个点
     * @param currScreenroads 当前屏幕的所有路
     * @return
     */
    public Set<Point> firstPoint(Point point, Map<String, LineString> currScreenroads) {
        Set<Point> pointSet = new HashSet<>();
        //设置投影点
        String projectionRoadId = setStartPointProjection(pointSet, point, currScreenroads);
        LineString projectionRoad = currScreenroads.get(projectionRoadId);
        //设置路的交点
        setIntersections(currScreenroads, projectionRoadId, pointSet);
        //设置路的起点与终点
        firstPointStartAndEndPoint(pointSet, projectionRoadId, projectionRoad);
        //多条路（多与两条）交到同一点
        multiIntersecions(currScreenroads, pointSet);
        //相邻的点融合
        Set<Point> newPointSet = mergeIntersecions(pointSet, THRESHOLD);

        return newPointSet;
    }

    /**
     * 设置起点投影
     *
     * @param result
     * @param point
     * @param roadCollection
     * @return
     */
    private String setStartPointProjection(Set<Point> result, Point point, Map<String, LineString> roadCollection) {
        //点到路的最小距离
        double minDistance = Double.MAX_VALUE;
        PointPairDistance pointPairDistance;
        //在最近路上的投影点
        Coordinate projection = new Coordinate();
        //所在路的id
        String projectionRoadId = null;
        Set<Map.Entry<String, LineString>> entries = roadCollection.entrySet();
        Iterator<Map.Entry<String, LineString>> entryIterator = entries.iterator();
        //遍历当前屏幕的所有路，求出与给定点距离最近的路
        while (entryIterator.hasNext()) {
            Map.Entry<String, LineString> entry = entryIterator.next();
            pointPairDistance = new PointPairDistance();
            DistanceToPoint.computeDistance(entry.getValue(), point.getInteriorPoint().getCoordinate(), pointPairDistance);
            if (pointPairDistance.getDistance() < minDistance) {
                minDistance = pointPairDistance.getDistance();
                projection = pointPairDistance.getCoordinate(0);
                projectionRoadId = entry.getKey();
            }
        }
        Point project = new GeometryFactory().createPoint(projection);
        List<String> userData = new LinkedList<>();
        userData.add(projectionRoadId);
        userData.add("true");
        project.setUserData(userData);
        result.add(project);

        return projectionRoadId;
    }

    /**
     * 单个路与当前屏幕路的交点
     *
     * @param roads
     * @param id
     * @param result
     */
    private void setIntersections(Map<String, LineString> roads, String id, Set<Point> result) {
        List<String> ids = new LinkedList<>();
        ids.add(id);
        setIntersections(roads, ids, result);
    }

    /**
     * 多条路与当前屏幕路的交点
     *
     * @param roads
     * @param ids
     * @param result
     */
    private void setIntersections(Map<String, LineString> roads, List<String> ids, Set<Point> result) {
        for (String id : ids) {
            LineString lineString = roads.get(id);
            Set<Map.Entry<String, LineString>> entries = roads.entrySet();
            Iterator<Map.Entry<String, LineString>> iterator = entries.iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, LineString> entry = iterator.next();
                String roadId = entry.getKey();
                LineString road = entry.getValue();
                if (!lineString.getEnvelopeInternal().intersects(road.getEnvelopeInternal())) {
                    continue;
                }
                if (lineString.equals(road)) {
                    continue;
                }
                Geometry intersection = lineString.intersection(road);
                if (!intersection.isEmpty()) {
                    if (intersection instanceof MultiPoint || intersection instanceof Point) {
                        for (int i = 0; i < intersection.getNumGeometries(); i++) {
                            Point point = (Point) intersection.getGeometryN(i);
                            List<String> userData = new LinkedList<>();
                            //加入两条路的id
                            userData.add(id);
                            userData.add(roadId);
                            point.setUserData(userData);
                            result.add(point);
                        }
                    }
                } else {
                    Point startPoint1 = lineString.getStartPoint();
                    boolean startFlag1 = isOnLine(road, startPoint1);
                    if (startFlag1) {
                        List<String> userData = new LinkedList<>();
                        userData.add(id);
                        userData.add(roadId);
                        startPoint1.setUserData(userData);
                        result.add(startPoint1);
                    }

                    Point endPoint1 = lineString.getEndPoint();
                    boolean endFlag1 = isOnLine(road, endPoint1);
                    if (endFlag1) {
                        List<String> userData = new LinkedList<>();
                        userData.add(id);
                        userData.add(roadId);
                        endPoint1.setUserData(userData);
                        result.add(endPoint1);
                    }

                    Point startPoint2 = road.getStartPoint();
                    boolean startFlag2 = isOnLine(lineString, startPoint2);
                    if (startFlag2) {
                        List<String> userData = new LinkedList<>();
                        userData.add(roadId);
                        userData.add(id);
                        startPoint2.setUserData(userData);
                        result.add(startPoint2);
                    }

                    Point endPoint2 = road.getEndPoint();
                    boolean endFlag2 = isOnLine(lineString, endPoint2);
                    if (endFlag2) {
                        List<String> userData = new LinkedList<>();
                        userData.add(roadId);
                        userData.add(id);
                        endPoint2.setUserData(userData);
                        result.add(endPoint2);
                    }
                }
            }
        }
    }

    /**
     * 设置路的起点与终点
     *
     * @param result
     * @param roadId
     * @param road
     */
    private void firstPointStartAndEndPoint(Set<Point> result, String roadId, LineString road) {
        Point startPoint = road.getStartPoint();
        Point endPoint = road.getEndPoint();
        boolean hasStartPoint = false;
        boolean hasEndPoint = false;
        Iterator<Point> pointIterator = result.iterator();
        while (pointIterator.hasNext()) {
            Point point1 = pointIterator.next();
            if (point1.getCoordinate().equals2D(startPoint.getCoordinate())) {
                hasStartPoint = true;
            }
            if (point1.getCoordinate().equals2D(endPoint.getCoordinate())) {
                hasEndPoint = true;
            }
        }
        if (!hasStartPoint) {
            List<String> startPointUserData = new LinkedList<>();
            startPointUserData.add(roadId);
            startPoint.setUserData(startPointUserData);
            result.add(startPoint);
        }
        if (!hasEndPoint) {
            List<String> endPointUserData = new LinkedList<>();
            endPointUserData.add(roadId);
            endPoint.setUserData(endPointUserData);
            result.add(endPoint);
        }
    }

    /**
     * 合并相邻的交点
     * @param pointSet
     * @param threshold
     * @return
     */
    private Set<Point> mergeIntersecions(Set<Point> pointSet, double threshold) {
        Set<Point> newResult = new HashSet<>();
        GeometryFactory factory = new GeometryFactory();
        for (Point point : pointSet) {
            Point newPoint = factory.createPoint(point.getCoordinate());
            List<String> userData = (List<String>)point.getUserData();
            newPoint.setUserData(userData);
            newResult.add(newPoint);
        }
        Set<Coordinate> tempCoordinates = new HashSet<>();

        for (Point point1 : pointSet) {
            for (Point point2 : pointSet) {
                //point1与point2相邻
                if (!point1.getCoordinate().equals2D(point2.getCoordinate()) &&
                        point1.getCoordinate().distance(point2.getCoordinate()) < threshold) {
                    //防止重复迭代
                    if (!(tempCoordinates.contains(point1.getCoordinate()) && tempCoordinates.contains(point2.getCoordinate()))) {
                        tempCoordinates.add(point1.getCoordinate());
                        tempCoordinates.add(point2.getCoordinate());

                        //在newResult中，将point2中的id合并到point1
//                        Iterator<Point> newIterator = newResult.iterator();
                        boolean deleteFlag = false;
//                        while (newIterator.hasNext()) {
//                            Point point = newIterator.next();
//                            if (point.getCoordinate().equals2D(point1.getCoordinate())) {
//                                List<String> ids = (List<String>) point.getUserData();
//                                List<String> ids2 = (List<String>) point2.getUserData();
//                                for (String id : ids2) {
//                                    if (!ids.contains(id)) {
//                                        ids.add(id);
//                                    }
//                                }
//                                deleteFlag = true;
//                                break;
//                            }
//                        }
                        for (Point point : newResult){
                            if (point.getCoordinate().equals2D(point1.getCoordinate())) {
                                List<String> ids = (List<String>) point.getUserData();
                                List<String> ids2 = (List<String>) point2.getUserData();
                                for (String id : ids2) {
                                    if (!ids.contains(id)) {
                                        ids.add(id);
                                    }
                                }
                                deleteFlag = true;
                                break;
                            }
                        }

                        //在newResult中，将point2删除
                        Iterator<Point> newIterator = newResult.iterator();
                        while (newIterator.hasNext()){
                            Point point = newIterator.next();
                            if (point.getCoordinate().equals2D(point2.getCoordinate())) {
                                if (deleteFlag){
                                    newIterator.remove();
                                    break;
                                }
                            }
                        }

                    }
                }
            }
        }

        return newResult;
    }

    /**
     * 多路（多于两条）相交
     *
     * @param roads
     * @param result
     */
    private void multiIntersecions(Map<String, LineString> roads, Set<Point> result) {
        for (Point point : result) {
            List<String> list = (List<String>) point.getUserData();
            Set<Map.Entry<String, LineString>> entries = roads.entrySet();
            Iterator<Map.Entry<String, LineString>> iterator = entries.iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, LineString> entry = iterator.next();
                String id = entry.getKey();
                LineString lineString = entry.getValue();
                if (isOnLine(lineString, point) && !list.contains(id)) {
                    list.add(id);
                }
            }
        }
    }

    /**
     * 根据起点与终点创建路段
     * @param geometrySet
     * @param roads
     * @param startPoint
     * @param endPoint
     * @throws Exception
     */
    private void createLineString(Set<Geometry> geometrySet, Map<String, LineString> roads, Point startPoint, Point endPoint) throws Exception {
        //两个集合的交集就是路的id
        Collection<String> intersectionIds = CollectionUtils.intersection((List<String>) startPoint.getUserData(), (List<String>) endPoint.getUserData());
        if (CollectionUtils.isEmpty(intersectionIds)){
            //如果起点进行了合并，可能不在终点所在的路上，将起点映射到终点所在路的最近一条
            double minDistance = Double.MAX_VALUE;
            String nearestRoadId = "";
            PointPairDistance pointPairDistance = new PointPairDistance();
            List<String> ids = (List<String>)endPoint.getUserData();
            for (String id : ids){
                LineString road = roads.get(id);
                DistanceToPoint.computeDistance(road, startPoint.getCoordinate(), pointPairDistance);
                if (pointPairDistance.getDistance() < minDistance){
                    minDistance = pointPairDistance.getDistance();
                    nearestRoadId = id;
                }
            }
            intersectionIds.add(nearestRoadId);
        }

        if (CollectionUtils.isNotEmpty(intersectionIds)) {
            Iterator<String> iterator = intersectionIds.iterator();
            while (iterator.hasNext()) {
                String intersectionId = iterator.next();
                LineString intersectionRoad = roads.get(intersectionId);
                //线段过滤器
                PointPairInterceptFilter pointPairInterceptFilter = new PointPairInterceptFilter(startPoint, endPoint);
                intersectionRoad.apply(pointPairInterceptFilter);
                LineString resultLineString = pointPairInterceptFilter.getLineString();
                List<String> userData = new LinkedList<>();
                userData.addAll(intersectionIds);
                resultLineString.setUserData(userData);
                geometrySet.add(resultLineString);
                //如果构成环，返回两段
                if(intersectionRoad.isRing()){
                    LineString resultLineString2 = pointPairInterceptFilter.getRemainLingString();
                    resultLineString2.setUserData(userData);
                    geometrySet.add(resultLineString2);
                }
            }
        }
    }

    /**
     * 根据两个点与当前屏幕的路id 构造路
     *
     * @param roads      全局roads
     * @param ids        当前屏幕路的id
     * @param startPoint 一段路的起点
     * @param endPoint   一段路的终点
     * @return
     * @throws Exception
     */
    public Set<Geometry> firstPlusPoint(Map<String, LineString> roads, List<String> ids, Point startPoint, Point endPoint) throws Exception {
        Set<Geometry> geometrySet;
        Set<Point> pointSet;
        try {
            geometrySet = new HashSet<>();
            pointSet = new HashSet<>();
            //当前屏幕的所有路
            Map<String, LineString> currentScreenRoads = new HashMap<>();
            for (String id : ids) {
                LineString lineString = roads.get(id);
                currentScreenRoads.put(id, lineString);
            }
            //相关的路
            List<String> relevantRoadIds = (List<String>) endPoint.getUserData();
            if (CollectionUtils.isNotEmpty(relevantRoadIds)) {
                //相关路与当前屏幕路求交点
                setIntersections(currentScreenRoads, relevantRoadIds, pointSet);
                //设置相关路的起点与终点
                firstPlusPointStartAndEndPoint(pointSet, currentScreenRoads, relevantRoadIds, endPoint);
                //如果点与当前的endpoint重合，则移除
                Iterator<Point> iterator = pointSet.iterator();
                while (iterator.hasNext()){
                    Point point = iterator.next();
                    if (point.getCoordinate().equals2D(endPoint.getCoordinate())){
                        iterator.remove();
                    }
                }
            }
            //多条路（多于两条）交到同一点
            multiIntersecions(currentScreenRoads, pointSet);
            //相邻点合并
            Set<Point> newPointSet = mergeIntersecions(pointSet, THRESHOLD);
            //删除与所传入的终点相邻的点，要保留该点所在路的id，继续求交点
            List<String> remainIds = new LinkedList<>();
            //删除当前endpoint附近的点（点合并）
            Iterator<Point> iterator = newPointSet.iterator();
            while (iterator.hasNext()) {
                Point point = iterator.next();
                    if (point.getCoordinate().distance(endPoint.getCoordinate()) < THRESHOLD) {
                        List<String> nearIds = (List<String>) point.getUserData();
                        List<String> endIds = (List<String>) endPoint.getUserData();
                        remainIds.addAll(CollectionUtils.subtract(nearIds, endIds));
                        iterator.remove();
                }
            }
            //删除的点所在的路，重新求交点
            setIntersections(currentScreenRoads, remainIds, newPointSet);
            //设置路的起点与终点
            firstPlusPointStartAndEndPoint(newPointSet,currentScreenRoads,remainIds, endPoint);
            //如果点与当前的endpoint重合或在附近，则移除
            Iterator<Point> iterator1 = newPointSet.iterator();
            while (iterator1.hasNext()){
                Point point = iterator1.next();
                if (point.getCoordinate().equals2D(endPoint.getCoordinate()) ||
                        point.getCoordinate().distance(endPoint.getCoordinate()) < THRESHOLD){
                    iterator1.remove();
                }
            }
            //新求的点中有多路相交的情况
            multiIntersecions(currentScreenRoads, newPointSet);
            //创建线段
            createLineString(geometrySet, currentScreenRoads, startPoint, endPoint);
            geometrySet.addAll(newPointSet);
            return geometrySet;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    /**
     * 相关路的起点与终点
     * @param pointSet
     * @param roads
     * @param ids
     * @param exclusionPoint
     */
    private void firstPlusPointStartAndEndPoint(Set<Point> pointSet, Map<String, LineString> roads, List<String> ids, Point exclusionPoint){
        for (String id : ids){
            LineString relevantRoad = roads.get(id);

            Point start = relevantRoad.getStartPoint();
            Point end = relevantRoad.getEndPoint();
            boolean startFlag = true;
            boolean endFlag = true;

            if (CollectionUtils.isNotEmpty(pointSet)) {
                for (Point point : pointSet){
                    if (point.getCoordinate().equals2D(start.getCoordinate())) {
                        startFlag = false;
                    }
                    if (point.getCoordinate().equals2D(end.getCoordinate())) {
                        endFlag = false;
                    }
                }

            }
            //该相关路的起点与终点 不能 与 给定的endPoint相同（结果集不允许加入该点）
            if (start.getCoordinate().equals2D(exclusionPoint.getCoordinate())) {
                startFlag = false;
            }
            if (end.getCoordinate().equals2D(exclusionPoint.getCoordinate())) {
                endFlag = false;
            }
            if (startFlag) {
                List<String> userData1 = new LinkedList<>();
                userData1.add(id);
                start.setUserData(userData1);
                pointSet.add(start);
            }
            if (endFlag) {
                List<String> userData1 = new LinkedList<>();
                userData1.add(id);
                end.setUserData(userData1);
                pointSet.add(end);
            }
        }
    }


    /**
     * 选定终点之前----给出倒数第二个点所在的线路
     *
     * @param geometryMap
     * @param point
     * @return
     */
    private Set<LineString> forLast(Map<String, LineString> geometryMap, Point point) {
        Set<LineString> lineStringSet = new LinkedHashSet<>();
        List<String> ids = (List<String>) point.getUserData();
        if (CollectionUtils.isNotEmpty(ids)) {
            for (String id : ids) {
                LineString lineString = geometryMap.get(id);
                List<String> userData = new LinkedList<>();
                userData.add(id);
                lineString.setUserData(userData);
                lineStringSet.add(lineString);
            }
        }
        return lineStringSet;
    }

    /**
     * 终点-----------引导式
     *
     * @param startPoint
     * @param endPoint
     * @return
     * @throws Exception
     */
    private Set<Geometry> lastPoint(Map<String, LineString> roads, Point startPoint, Point endPoint) throws Exception {
        //结果集
        Set<Geometry> geometrySet = new HashSet<>();
        //倒数第二个点所在的路id
        List<String> relevantRoadIds = (List<String>) startPoint.getUserData();
        Map<String, LineString> relevantRoads = new HashMap<>();
        for (String id : relevantRoadIds) {
            LineString lineString = roads.get(id);
            relevantRoads.put(id, lineString);
        }
        //最后一个点所在的路
        double minDistance = Double.MAX_VALUE;
        PointPairDistance pointPairDistance = new PointPairDistance();
        String roadId = "";
        Coordinate projectionCoordinate = new Coordinate();
        Set<Map.Entry<String, LineString>> entries = relevantRoads.entrySet();
        Iterator<Map.Entry<String, LineString>> iterator = entries.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, LineString> entry = iterator.next();
            String id = entry.getKey();
            LineString lineString = entry.getValue();
            DistanceToPoint.computeDistance(lineString, endPoint.getCoordinate(), pointPairDistance);
            if (pointPairDistance.getDistance() < minDistance) {
                minDistance = pointPairDistance.getDistance();
                roadId = id;
                projectionCoordinate = pointPairDistance.getCoordinate(0);
            }
        }
        //投影所在路
        LineString projectionRoad = relevantRoads.get(roadId);
        //投影点
        Point projectionPoint = new GeometryFactory().createPoint(projectionCoordinate);
        List<String> userData = new LinkedList<>();
        userData.add(roadId);
        projectionPoint.setUserData(userData);
        geometrySet.add(projectionPoint);
        PointPairInterceptFilter filter = new PointPairInterceptFilter(startPoint, projectionPoint);
        projectionRoad.apply(filter);
        //投影线段
        LineString lineString = filter.getLineString();
        lineString.setUserData(userData);
        geometrySet.add(lineString);
        return geometrySet;
    }


    /**
     * 几何分解
     *
     * @param geom
     * @return
     */
    private static List<Geometry> flatFeatureList(Geometry geom) {
        final List<Geometry> singleGeoms = new ArrayList<>();
        final Stack<Geometry> geomStack = new Stack<>();
        Geometry nextGeom;
        int nextGeomCount;
        geomStack.push(geom);
        while (!geomStack.isEmpty()) {
            nextGeom = geomStack.pop();
            if (nextGeom instanceof Point
                    || nextGeom instanceof MultiPoint
                    || nextGeom instanceof LineString
                    || nextGeom instanceof MultiLineString
                    || nextGeom instanceof Polygon
                    || nextGeom instanceof MultiPolygon) {
                singleGeoms.add(nextGeom);
                System.out.println();
            } else if (nextGeom instanceof GeometryCollection) {
                // Push all child geometries
                nextGeomCount = nextGeom.getNumGeometries();
                for (int i = 0; i < nextGeomCount; ++i) {
                    geomStack.push(nextGeom.getGeometryN(i));
                }
            }
        }
        return singleGeoms;
    }

    /**
     * 几何的geojson
     *
     * @param geometry
     * @return
     */
    public static JSONObject geo2JSONObject(Geometry geometry) {
        Coordinate[] coordinates = geometry.getCoordinates();
        int size = coordinates.length;
        StringBuilder sb = new StringBuilder();
        if (size > 1) {
            sb.append("[");
        }
        for (int i = 0; i < size; i++) {
            Coordinate coord = coordinates[i];
            sb.append("[");
            sb.append(coord.x);

            sb.append(",");
            sb.append(coord.y);

            if (!Double.isNaN(coord.z)) {
                sb.append(",");
                sb.append(coord.z);
            }
            sb.append("],");
        }
        sb.setLength(sb.length() - 1);

        if (size > 1) {
            sb.append("]");
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", geometry.getGeometryType());
        jsonObject.put("coordinates", sb.toString());
        jsonObject.put("userData", geometry.getUserData());
        return jsonObject;
    }

    public static void readShapeFile(String filePath, Map<String, Geometry> geometryMap) throws Exception {
//        Map<String, Geometry> geometryMap = new LinkedHashMap<>();
        try {
            File file = new File(filePath);
            ShapefileDataStoreFactory shapefileDataStoreFactory = new ShapefileDataStoreFactory();
            ShapefileDataStore shapefileDataStore = (ShapefileDataStore) shapefileDataStoreFactory.createDataStore(file.toURI().toURL());
            shapefileDataStore.setCharset(Charset.forName("GBK"));
            SimpleFeatureSource simpleFeatureSource = shapefileDataStore.getFeatureSource();
            SimpleFeatureCollection simpleFeatureCollection = simpleFeatureSource.getFeatures();
            SimpleFeatureIterator simpleFeatureIterator = simpleFeatureCollection.features();
            while (simpleFeatureIterator.hasNext()) {
                System.out.println("当前map数量：" + geometryMap.size());
                SimpleFeature simpleFeature = simpleFeatureIterator.next();
                com.vividsolutions.jts.geom.Geometry geometry = (com.vividsolutions.jts.geom.Geometry) simpleFeature.getDefaultGeometry();
                if (geometry != null) {
                    Map<String, Object> userData = new HashMap<>();
                    Collection<Property> propertyCollection = simpleFeature.getProperties();
                    Iterator<Property> propertyIterator = propertyCollection.iterator();
                    while (propertyIterator.hasNext()) {
                        Property property = propertyIterator.next();
                        String name = property.getName().toString();
                        if (!"the_geom".equals(name)) {
                            userData.put(name, simpleFeature.getAttribute(name));
                        }
                    }
                    geometry.setUserData(userData);
                    String uuid = UUID.randomUUID().toString().replaceAll("-", "");
                    geometryMap.put(uuid, vividToLocationGeometry(geometry));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Geometry vividToLocationGeometry(com.vividsolutions.jts.geom.Geometry geometry) throws org.locationtech.jts.io.ParseException {
        Geometry geometry1 = new org.locationtech.jts.io.WKTReader().read(geometry.toText());
        geometry1.setUserData(geometry.getUserData());
        return geometry1;
    }

    public static boolean isOnLine(Geometry geometry, Point point) {
        PointPairDistance pointPairDistance = new PointPairDistance();
        DistanceToPoint.computeDistance(geometry, point.getCoordinate(), pointPairDistance);
        return pointPairDistance.getDistance() < 1.0e-6d;
    }

    /**
     * Envelope等比例扩展
     * expandFactor = 1, 宽度与高度是原来的3倍
     * expandFactor = 2, 宽度与高度是原来的5倍
     * expandFactor = 0.5, 宽度与高度是原来的2倍
     *
     * @param envelope
     * @param expandFactor
     * @return
     */
    private Envelope expandEnvelope(Envelope envelope, double expandFactor) {
        double width = envelope.getWidth();
        double height = envelope.getHeight();
        return new Envelope(envelope.getMinX() - width * expandFactor,
                envelope.getMaxX() + width * expandFactor,
                envelope.getMinY() - height * expandFactor,
                envelope.getMaxY() + height);
    }

}

