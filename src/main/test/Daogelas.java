
import com.map.entity.Point;
import com.map.utils.GeoToolsUtils;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;

import java.util.ArrayList;
import java.util.List;

public class Daogelas {
    /**
     * 存储采样点数据的链表
     */
    public List<Point> points = new ArrayList<Point>();

    /**
     * 控制数据压缩精度的极差
     */
    private static final double D = 1;

    public Daogelas(List<Point> source) {
        points = source;
    }

    /**
     * 对矢量曲线进行压缩
     *
     * @param from 曲线的起始点
     * @param to   曲线的终止点
     */
    public void compress(Point from, Point to) {
        /**
         * 压缩算法的开关量
         */
        boolean switchvalue = false;

        /**
         * 由起始点和终止点构成的直线方程一般式的系数
         */
        double A = (from.getY() - to.getY()) / Math.sqrt(Math.pow((from.getY() - to.getY()), 2) + Math.pow((from.getX() - to.getX()), 2));

        /**
         * 由起始点和终止点构成的直线方程一般式的系数
         */
        double B = (to.getX() - from.getX()) / Math.sqrt(Math.pow((from.getY() - to.getY()), 2) + Math.pow((from.getX() - to.getX()), 2));

        /**
         * 由起始点和终止点构成的直线方程一般式的系数
         */
        double C = (from.getX() * to.getY() - to.getX() * from.getY()) / Math.sqrt(Math.pow((from.getY() - to.getY()), 2) + Math.pow((from.getX() - to.getX()), 2));

        double d = 0;
        double dmax = 0;
        int m = points.indexOf(from);
        int n = points.indexOf(to);
        if (n == m + 1)
            return;
        Point middle = null;
        List<Double> distance = new ArrayList<Double>();
        for (int i = m + 1; i < n; i++) {
            d = Math.abs(A * (points.get(i).getX()) + B * (points.get(i).getY()) + C) / Math.sqrt(Math.pow(A, 2) + Math.pow(B, 2));
            distance.add(d);
        }
        dmax = distance.get(0);
        for (int j = 1; j < distance.size(); j++) {
            if (distance.get(j) > dmax)
                dmax = distance.get(j);
        }
        if (dmax > D)
            switchvalue = true;
        else
            switchvalue = false;
        if (!switchvalue) {
            //删除Points(m,n)内的坐标
            for (int i = m + 1; i < n; i++) {
                points.get(i).setIndex(-1);
            }
        } else {
            for (int i = m + 1; i < n; i++) {
                if ((Math.abs(A * (points.get(i).getX()) + B * (points.get(i).getY()) + C) / Math.sqrt(Math.pow(A, 2) + Math.pow(B, 2)) == dmax)) {
                    middle = points.get(i);
                }
            }
            compress(from, middle);
            compress(middle, to);
        }
    }

    public static void main(String[] args) {
        List<Point> source = new ArrayList<Point>();
       /* source.add(new Point(1, 4, 1));
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

            /*System.out.println("coor---:"+coor);*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        Daogelas d = new Daogelas(source);
        System.out.println("压缩前：");
        int qm=0;
        for (int i = 0; i < d.points.size(); i++) {
            Point p = d.points.get(i);
            System.out.println(p.getX() + " " + p.getY() + ";");
            qm++;
        }
        d.compress(d.points.get(0), d.points.get(d.points.size() - 1));
        System.out.println("\n 压缩后：");
        int hm=0;
        for (int i = 0; i < d.points.size(); i++) {
            Point p = d.points.get(i);
            if (p.getIndex() > -1) {
                System.out.println(p.getX() + " " + p.getY() + ";");
                hm++;
            }
        }
        System.out.println("qm-----:"+qm+"   "+"hm------:"+hm);
    }
}
