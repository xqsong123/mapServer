package com.map.svc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class FileUploadServlet extends HttpServlet {
    private static Log log = LogFactory.getLog(FileUploadServlet.class);

    public void init() throws ServletException {
        log.debug("FileUploadServlet.init() ....");
        super.init();
        log.debug("FileUploadServlet.init() done");
    }

    public void destroy() {
        super.destroy();
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        log.info("FileUploadServlet get请求");
        doService1(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        log.info("FileUploadServlet post请求");
        doService2(req, res);
    }

    private void doService1(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            Service service = ServiceFactory2.getService(req);
            String answerLog = service.doService(req, res);
        } catch (Throwable t) {
        } finally {
            res.getOutputStream().close();
        }
    }

    private void doService2(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            String answerLog = ServiceFactory2.getService(req).doService(req, res);
        } catch (Throwable t) {
        } finally {
            res.getWriter().close();
        }
    }

}
