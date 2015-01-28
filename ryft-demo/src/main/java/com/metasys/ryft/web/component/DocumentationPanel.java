package com.metasys.ryft.web.component;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

public class DocumentationPanel extends Panel {

    private static final String DOC_ID = "doc";

    public DocumentationPanel(String id, String content) {
        super(id);
        add(new Label(DOC_ID, content).setEscapeModelStrings(false));
    }

}
