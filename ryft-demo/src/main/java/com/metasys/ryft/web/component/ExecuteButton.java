package com.metasys.ryft.web.component;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.googlecode.wicket.kendo.ui.panel.KendoFeedbackPanel;
import com.metasys.ryft.Query;
import com.metasys.ryft.Result;
import com.metasys.ryft.RyftException;
import com.metasys.ryft.api.RyftApi;

/**
 * Button used to execute an algorithm. It extends the form's default submission behavior, and if the form is valid triggers the execution as an AJAX
 * call.
 *
 * @author Sylvain Crozon
 *
 */
public class ExecuteButton extends Label {

    private static final Logger LOG = LogManager.getLogger(ExecuteButton.class);

    @SpringBean
    private RyftApi api;

    private DemoForm form;
    private Query query;
    private ResultPanel resultPanel;

    /**
     * Creates a new button to execute a query.
     *
     * @param id
     *            the Wicket ID of the button
     * @param form
     *            the form to use to validate the submission
     * @param query
     *            the query to execute
     * @param resultPanel
     *            the result panel to use to display the results
     */
    public ExecuteButton(String id, DemoForm form, Query query, ResultPanel resultPanel) {
        super(id, new StringResourceModel(id, form, null, id));
        this.form = form;
        this.query = query;
        this.resultPanel = resultPanel;
        add(new ExecuteBehavior("onclick"));
    }

    class ExecuteBehavior extends AjaxFormSubmitBehavior {
        public ExecuteBehavior(String event) {
            super(event);
        }

        @Override
        protected void onEvent(AjaxRequestTarget target) {
            // notify the form that the user requested to trigger an execution, which modifies the form's validation
            form.setExecute(true);
            super.onEvent(target);
            form.setExecute(false);
            target.addChildren(getPage(), KendoFeedbackPanel.class);
            target.addChildren(getPage(), FormComponentPanel.class);
        }

        @Override
        protected void onAfterSubmit(AjaxRequestTarget target) {
            // execute the query if the form was successfully submitted
            target.add(resultPanel);
            try {
                query.setType(form.getPanelModel().getObject());
                Result result = api.execute(query);
                resultPanel.getResult().copy(result);
                // reset the output location as the program can't use twice the same output
                query.indexOutput();
            } catch (RyftException e) {
                LOG.error("Error executing query {}: {}", query, e.getMessage());
                error(e.getMessage() + " (" + query.getId() + ")");
            } catch (Exception e) {
                LOG.error("Error executing query {}", query, e);
                error(RyftException.GENERIC.getMessage() + " (" + query.getId() + ")");
            }
        }
    }
}
