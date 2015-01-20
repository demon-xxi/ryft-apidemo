package com.metasys.ryft.web;

import org.apache.wicket.Page;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.protocol.http.WebApplication;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.springframework.stereotype.Component;

@Component(value = "wicketApplication")
public class DemoApplication extends WebApplication {

    @Override
    public Class<? extends Page> getHomePage() {
        return DemoPage.class;
    }

    @Override
    public RuntimeConfigurationType getConfigurationType() {
        return RuntimeConfigurationType.DEVELOPMENT;
        // return RuntimeConfigurationType.DEPLOYMENT;
    }

    public static void main(String[] args) throws Exception {
        Server server = new Server(8989);
        WebAppContext wac = new WebAppContext();
        wac.setDescriptor("WEB-INF/web.xml");
        wac.setResourceBase("src/main/webapp");
        wac.setContextPath("/");
        wac.setParentLoaderPriority(true);
        server.setHandler(wac);
        server.start();
    }

}
