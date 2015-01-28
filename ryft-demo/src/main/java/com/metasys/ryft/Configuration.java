package com.metasys.ryft;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Configuration {

    @Value("${ryft.debug:false}")
    private boolean debug;
    @Value("${ryft.startPanel}")
    private String panel;

    public boolean isDebug() {
        return debug;
    }

    public String getPanel() {
        return panel;
    }

}
