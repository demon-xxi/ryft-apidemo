package com.metasys.ryft;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.wicket.Page;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.metasys.ryft.web.DemoPage;

/**
 * Entry point for Wicket to configure the application. It also provides a <code>main</code> starting an embedded Jetty server is the application is
 * not deployed as a war.
 *
 * @author Sylvain Crozon
 *
 */
// matches the applicationBean property value of the Wicket filter declared in web.xml
@Component(value = "wicketApplication")
public class DemoApplication extends WebApplication {

    private static final Logger LOG = LogManager.getLogger(DemoApplication.class);

    @Autowired
    private Configuration config;

    @Override
    protected void init() {
        LOG.info("Initializing Wicket application");
        super.init();
        // recommended for Wicket jQuery UI
        getMarkupSettings().setStripWicketTags(true);
        // configures Spring wiring
        getComponentInstantiationListeners().add(new SpringComponentInjector(this));
        // prettier bookmarkable page
        mountPackage("/", DemoPage.class);
    }

    @Override
    public Class<? extends Page> getHomePage() {
        return DemoPage.class;
    }

    @Override
    public RuntimeConfigurationType getConfigurationType() {
        if (config.isDebug()) {
            return RuntimeConfigurationType.DEVELOPMENT;
        }
        return RuntimeConfigurationType.DEPLOYMENT;
    }

    // java -cp 'conf:lib/*' com.metasys.ryft.DemoApplication ./webapp 8080
    public static void main(String[] args) throws Exception {
        String resourceBase = null;
        int port = 0;
        if (args.length == 0) {
            port = 8989;
            String[] options = new String[] { "src/main/webapp", ".", "webapp" };
            for (String option : options) {
                if (new File(option + "/WEB-INF/web.xml").exists()) {
                    resourceBase = option;
                    break;
                }
            }
        } else if (args.length > 0) {
            resourceBase = args[0];
            if (args.length == 2) {
                try {
                    port = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                }
            }
        }
        if (resourceBase == null) {
            LOG.error("Can't find web.xml, {}", usage());
        } else if (port == 0) {
            LOG.error("Wrong port number, {}", usage());
        } else {
            LOG.info("Starting embedded Jetty server on port {}", port);
            Server server = new Server(port);
            WebAppContext wac = new WebAppContext();
            wac.setDescriptor("WEB-INF/web.xml");
            wac.setResourceBase(resourceBase);
            wac.setContextPath("/");
            wac.setParentLoaderPriority(true);
            server.setHandler(wac);
            server.start();
        }
    }

    private static String usage() {
        return "usage: java -cp 'conf:lib/*' com.metasys.ryft.DemoApplication <webapp base folder> <port>";
    }
}
