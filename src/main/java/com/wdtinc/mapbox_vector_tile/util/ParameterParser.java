package com.wdtinc.mapbox_vector_tile.util;


import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public abstract class ParameterParser<T extends Parameter> {

    public abstract T parse(HttpServletRequest req);

    public static String getString(HttpServletRequest req, String name, String defaultValue) {
        String value = req.getParameter(name);
        if (value == null) {
            if (defaultValue == null)
                throw new IllegalArgumentException("need parameter " + name);
            else
                return defaultValue;
        }
        return value;
    }

    public static int getInt(HttpServletRequest req, String name, String defaultValue) {
        String value = getString(req, name, defaultValue);
        int intval;
        try {
            intval = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("unknown " + name + ":" + value);
        }
        return intval;
    }

    public static String[] getStringArray(HttpServletRequest req, String name, String[] defaultValue) {
        String[] value = req.getParameterValues(name);
        if (value == null) {
            if (defaultValue == null)
                throw new IllegalArgumentException("need parameter " + name);
            else
                return defaultValue;
        }
        return value;
    }

    public static int[] getIntArray(HttpServletRequest req, String name, String[] defaultValue) {
        String[] values = getStringArray(req, name, null);
        int[] intvals = new int[values.length];
        int i = 0;
        try {
            for (; i < values.length; i++) {
                intvals[i] = Integer.parseInt(values[i]);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("unknown " + name + ":" + values[i]);
        }
        return intvals;
    }

    /**
     * 获取POST请求中Body参数
     *
     * @param request
     * @return 字符串
     */
    public static String getParm(HttpServletRequest request) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String line = null;
        StringBuilder sb = new StringBuilder();
        try {
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

}
