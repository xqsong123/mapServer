package com.map.service;

import com.map.svc.Service;
import com.map.svc.ServiceInitializer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ReloadService extends Service {
    @Override
    public String doService(HttpServletRequest req, HttpServletResponse res) throws IOException {
        ServiceInitializer.init();
        res.getOutputStream().println("reload finished");
        return "reload";
    }
}
