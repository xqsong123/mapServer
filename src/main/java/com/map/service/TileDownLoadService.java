package com.map.service;

import com.map.serviceImp.TileDownLoadServiceImp;
import com.map.svc.Service;
import com.map.utils.WGS_Encrypt;
import com.wdtinc.mapbox_vector_tile.VectorTile;
import com.wdtinc.mapbox_vector_tile.adapt.jts.*;
import com.wdtinc.mapbox_vector_tile.adapt.jts.model.JtsLayer;
import com.wdtinc.mapbox_vector_tile.adapt.jts.model.JtsMvt;
import com.wdtinc.mapbox_vector_tile.build.MvtLayerBuild;
import com.wdtinc.mapbox_vector_tile.build.MvtLayerParams;
import com.wdtinc.mapbox_vector_tile.build.MvtLayerProps;
import com.wdtinc.mapbox_vector_tile.util.JdkUtils;
import com.wdtinc.mapbox_vector_tile.util.ParameterParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.locationtech.jts.algorithm.ConvexHull;
import org.locationtech.jts.geom.*;
import org.opengis.feature.simple.SimpleFeature;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

public class TileDownLoadService extends Service {

    private static Log log = LogFactory.getLog(TileDownLoadService.class);


    @Override
    public String doService(HttpServletRequest req, HttpServletResponse res) throws IOException {

        String test = ParameterParser.getString(req, "test", "400");
        byte[] b = new byte[0];

        /*Coordinate[] coordinates1 = new Coordinate[]{
                new Coordinate(117.2,34.8),new Coordinate(122.2,34.8),
                new Coordinate(122.2,31.5),new Coordinate(117.2,31.5),new Coordinate(117.2,34.8)
        };
        Collection<Geometry> geometries =getPoints();

        JtsLayer layer = new JtsLayer("square", geometries);
        JtsMvt mvt = new JtsMvt(singletonList(layer));*/

        TileDownLoadServiceImp tileDownLoadServiceImp = new TileDownLoadServiceImp();
        b = tileDownLoadServiceImp.testPoints();
        if (res.getBufferSize() < b.length)
            res.setBufferSize(b.length);
        res.setHeader("Access-Control-Allow-Origin", "*");
        res.setContentType("binary/octet-stream");
        res.setContentLength(b.length);
        res.getOutputStream().write(b);
        return "finish";
    }



}
