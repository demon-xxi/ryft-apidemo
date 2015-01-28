package com.metasys.ryft.web.component;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

/**
 * Form holding all the components for the user to configure and execute a query.
 *
 * <p>
 * The from controls when the user can change panel or execute an algorithm.
 *
 * @author Sylvain Crozon
 *
 */
public class DemoForm extends Form<Void> {

    private static final String FORM_ID = "ryft-demo-form";

    // holds the name of the current panel being displayed
    private static final String CURRENT_PANEL_ID = "currentPanel";
    // name of the new panel the user is trying to navigate to
    private static final String NEW_PANEL_ID = "newPanel";
    // indicates the user requested to execute an algorithm
    private static final String EXECUTE_ID = "execute";

    private String currentPanel;
    private String newPanel;
    private IModel<String> panelModel;
    private boolean execute;
    private IModel<Boolean> executeModel;

    public DemoForm(String currentPanel) {
        super(FORM_ID);
        this.currentPanel = currentPanel;
        panelModel = new PropertyModel<>(this, CURRENT_PANEL_ID);
        executeModel = new PropertyModel<>(this, EXECUTE_ID);
        add(new HiddenField<>(CURRENT_PANEL_ID, new PropertyModel<>(this, CURRENT_PANEL_ID)));
        add(new HiddenField<>(NEW_PANEL_ID, new PropertyModel<>(this, NEW_PANEL_ID)));
    }

    @Override
    protected void onError() {
        super.onError();
        // if there's an error on the panel, do not allow to navigate to the requested panel, fix the errors first.
        newPanel = currentPanel;
    }

    @Override
    protected void onSubmit() {
        super.onSubmit();
        currentPanel = newPanel;
    }

    public IModel<String> getPanelModel() {
        return panelModel;
    }

    public IModel<Boolean> getExecuteModel() {
        return executeModel;
    }

    public boolean isExecute() {
        return execute;
    }

    public void setExecute(boolean execute) {
        this.execute = execute;
    }

}
