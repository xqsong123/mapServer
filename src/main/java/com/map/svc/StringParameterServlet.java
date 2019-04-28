package com.map.svc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;

public class StringParameterServlet extends HttpServlet {

    private static Log log = LogFactory.getLog(StringParameterServlet.class);

    public void init() throws ServletException {
        log.debug("StringParameterServlet.init() ....");
        super.init();
        ServiceInitializer.init();
        log.debug("StringParameterServlet.init() done");
    }

    public void destroy() {
        //ServiceInitializer.destroy();
        super.destroy();
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        log.info("StringParameterServlet get请求");
        doService1(req, res);

    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        log.info("StringParameterServlet post请求");
        doService2(req, res);

    }

    private void doService1(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            long t1 = System.currentTimeMillis();
            Service service = ServiceFactory.getService(req);
            String answerLog = service.doService(req, res);
            long t2 = System.currentTimeMillis();
            log.info(getLog(req) + answerLog + " in [" + (t2 - t1) + "]");
        } catch (Throwable t) {
            log.debug(getLog(req) + " Exceptions!", t);
        } finally {
            res.getOutputStream().close();
        }
    }

    private void doService2(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            long t1 = System.currentTimeMillis();
            String answerLog = ServiceFactory.getService(req).doService(req, res);
            long t2 = System.currentTimeMillis();
            log.info(getLog(req) + answerLog + " in [" + (t2 - t1) + "]");
        } catch (Throwable t) {
            log.debug(getLog(req) + " Exceptions!", t);
        } finally {
            res.getWriter().close();
        }
    }

    private String getLog(HttpServletRequest req) throws IOException {
        //String argsStr = "{" + param2string(req) + "}";
        String argsStr = "";
        StringBuilder loginfo = new StringBuilder("[");
        loginfo.append(req.getRemoteAddr());
        String originalIP = req.getHeader("X-Forwarded-For");
        if (originalIP != null) {
            loginfo.append(", ").append(originalIP);
        }
        loginfo.append("] ").append(argsStr);
        return loginfo.toString();
    }

    @SuppressWarnings("unchecked")
    private String param2string(HttpServletRequest req) {
        StringBuilder result = new StringBuilder();
        Enumeration<String> e = req.getParameterNames();
        while (e.hasMoreElements()) {
            String name = e.nextElement();
            String[] values = req.getParameterValues(name);
            for (String value : values) {
                result.append(name + "=" + value + "&");
            }
        }
        result.deleteCharAt(result.length() - 1);
        return result.toString();
    }
}
