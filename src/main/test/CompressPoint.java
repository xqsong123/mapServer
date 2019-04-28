import com.map.entity.Point;
import com.map.utils.GeoToolsUtils;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;

import java.util.ArrayList;
import java.util.List;

public class CompressPoint {

    public static void main(String[] args) {
        List<Point> source = new ArrayList<Point>();
      /*  source.add(new Point(1, 4, 1));
        source.add(new Point(2, 3, 2));
        source.add(new Point(4, 2, 4));
        source.add(new Point(6, 6, 6));
        source.add(new Point(7, 7, 7));
        source.add(new Point(8, 6, 8));
        source.add(new Point(9, 5, 9));
        source.add(new Point(10, 10, 10));
        source.add(new Point(11, 10.5, 11));
        source.add(new Point(12, 14, 12));
        source.add(new Point(13, -10, 13));*/
        GeoToolsUtils geoTools = new GeoToolsUtils();
        try {
            List<Geometry> roadLst = geoTools.readSHP("D:\\测试抽希的文件\\GROALN_GAOGUO.shp", "GBK", null, null);
            int m = 1;
            for (int k = 0; k < roadLst.size(); k++) {
                Geometry line = (Geometry) roadLst.get(k);
                int parts = line.getNumGeometries();
                for (int i = 0; i < parts; i++) {
                    LineString l = (LineString) line.getGeometryN(i);
                    for (int j = 0, num = l.getNumPoints(); j < num; j++) {
                        Coordinate coor = l.getCoordinateN(j);
                        Point point = new Point(coor.x, coor.y, m);
                        source.add(point);
                        m++;
                       /*double[] xy = WGS_Encrypt.WGS2Mars(coor.y, coor.x);
                       Coordinate newc = new Coordinate(xy[1], xy[0]);
                       coor.setCoordinate(newc);*/
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("压缩前：");
        int qm=0;
        for (int i = 0; i < source.size(); i++) {
            Point p = source.get(i);
            System.out.println(p.getX() + " " + p.getY() + ";");
            qm++;
        }
        CompressFunction(source);
        System.out.println("\n 压缩后：");
        int hm=0;
        for (int i = 0; i < source.size(); i++) {
            Point p = source.get(i);
            if (p.getIndex() > -1) {
                System.out.println(p.getX() + " " + p.getY() + ";");
                hm++;
            }
        }
        System.out.println("qm-----:"+qm+"   "+"hm------:"+hm);
    }

    public static void CompressFunction(List<Point> source) {
        int lastIndex = -1;
        double dMax = 1;//定义阈值，越小越精确
        for (int i = 1; i < source.size() - 1; i++) {
            if (i == 1) {
                lastIndex = 0;
            } else {
                Point currentPoint = source.get(i);
                Point lastEffectivePoint = source.get(lastIndex);
                Point followPoint = source.get(i + 1);
                /**
                 * 由起始点和终止点构成的直线方程一般式的系数
                 */
                double A = (lastEffectivePoint.getY() - followPoint.getY()) / Math.sqrt(Math.pow((lastEffectivePoint.getY() - followPoint.getY()), 2) + Math.pow((lastEffectivePoint.getX() - followPoint.getX()), 2));

                /**
                 * 由起始点和终止点构成的直线方程一般式的系数
                 */
                double B = (followPoint.getX() - lastEffectivePoint.getX()) / Math.sqrt(Math.pow((lastEffectivePoint.getY() - followPoint.getY()), 2) + Math.pow((lastEffectivePoint.getX() - followPoint.getX()), 2));

                /**
                 * 由起始点和终止点构成的直线方程一般式的系数
                 */
                double C = (lastEffectivePoint.getX() * followPoint.getY() - followPoint.getX() * lastEffectivePoint.getY()) / Math.sqrt(Math.pow((lastEffectivePoint.getY() - followPoint.getY()), 2) + Math.pow((lastEffectivePoint.getX() - followPoint.getX()), 2));

                double d = Math.abs(A * currentPoint.getX() + B * currentPoint.getY() + C) / Math.sqrt(Math.pow(A, 2) + Math.pow(B, 2));
                if (d <= dMax) {
                    currentPoint.setIndex(-1);//删除节点
                } else {
                    lastIndex = i;//把当前点作为下一个点的参照
                }
            }
        }
    }
}



