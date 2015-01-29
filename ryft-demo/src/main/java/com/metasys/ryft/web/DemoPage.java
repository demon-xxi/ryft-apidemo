package com.metasys.ryft.web;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Calendar;

import org.apache.commons.io.IOUtils;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.ajax.IAjaxIndicatorAware;
import org.apache.wicket.devutils.debugbar.DebugBar;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.FeedbackMessagesModel;
import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.RangeValidator;

import com.googlecode.wicket.kendo.ui.panel.KendoFeedbackPanel;
import com.metasys.ryft.Configuration;
import com.metasys.ryft.Query;
import com.metasys.ryft.Query.SortOrder;
import com.metasys.ryft.Result;
import com.metasys.ryft.web.component.DemoForm;
import com.metasys.ryft.web.component.DocumentationPanel;
import com.metasys.ryft.web.component.ExecuteButton;
import com.metasys.ryft.web.component.FormComponentPanel;
import com.metasys.ryft.web.component.ResultPanel;
import com.metasys.ryft.web.validator.ConditionalRequired;
import com.metasys.ryft.web.validator.Required;
import com.metasys.ryft.web.validator.TermFormatValidator;

/**
 * Wicket page for the
 *
 * @author Sylvain Crozon
 *
 */
public class DemoPage extends WebPage implements IAjaxIndicatorAware {

    // ****** Wicket IDs ********************************************
    // page layout
    private static final String DEBUG_ID = "debug";
    private static final String AJAX_INDICATOR_ID = "ajaxIndicator";
    private static final String FEEDBACK_PANEL_ID = "feedbackPanel";
    private static final String YEAR_ID = "year";

    // settings
    private static final String INPUT = "input";
    private static final String OUTPUT = "output";
    private static final String WRITE_INDEX = "writeIndex";
    private static final String NODES = "nodes";
    private static final String SETTINGS = "settings";

    // common
    private static final String DOC_SUFFIX = "Doc";
    private static final String RESULT_SUFFIX = "Result";
    private static final String EXECUTE_SUFFIX = "Execute";

    // search
    private static final String SEARCH = Query.SEARCH;
    private static final String SEARCH_QUERY = SEARCH + "Query";
    private static final String SEARCH_WIDTH = SEARCH + "Width";

    // fuzzy search
    private static final String FUZZY = Query.FUZZY;
    private static final String FUZZY_QUERY = FUZZY + "Query";
    private static final String FUZZY_WIDTH = FUZZY + "Width";
    private static final String FUZZYNESS = "fuzziness";

    // term frequency
    private static final String TERM = Query.TERM;
    private static final String TERM_FORMAT = TERM + "Format";
    private static final String TERM_FIELD = TERM + "Field";

    // sort
    private static final String SORT = Query.SORT;
    private static final String SORT_FIELD = SORT + "Field";
    private static final String SORT_ORDER = SORT + "Order";

    // ****** End Wicket IDs ****************************************

    // CSS to load for jQuery and Kendo
    private static final String[] KENDO_CSS = new String[] { "css/smoothness/jquery-ui.min.css", "css/kendo/kendo.common.min.css",
            "css/kendo/kendo.default.min.css" };

    // Holder of feedback keys already rendered to avoid duplicated messages for form components having their own embedded error messages
    public static final MetaDataKey<Boolean> IGNORE_FEEDBACK = new MetaDataKey<Boolean>() {
    };

    @SpringBean
    private Configuration config;

    private Query query;
    private FeedbackPanel feedbackPanel;
    private DemoForm form;

    public DemoPage() throws Exception {
        super();
        query = new Query();
        query.setInput("passengers.txt");
        query.setSearchQuery("(RAW_TEXT CONTAINS (\"310-555-2323\"))");
        query.setSearchWidth(21);
        query.setFuzzyQuery("(RAW_TEXT CONTAINS \"Mr. Thomas Magnum\")");
        query.setFuzzyWidth(25);
        query.setFuzziness(2);
        addCommonComponents();
        addSettings();
        addSearch();
        addFuzzySearch();
        addTermFrequency();
        addSort();
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        for (String css : KENDO_CSS) {
            response.render(CssHeaderItem.forUrl(css));
        }
        response.render(OnDomReadyHeaderItem.forScript("init()"));
    }

    @Override
    public String getAjaxIndicatorMarkupId() {
        return AJAX_INDICATOR_ID;
    }

    private void addCommonComponents() {
        if (config.isDebug()) {
            add(new DebugBar(DEBUG_ID));
        } else {
            add(new Label(DEBUG_ID));
        }
        feedbackPanel = new FeedbackPanel(FEEDBACK_PANEL_ID);
        feedbackPanel.setOutputMarkupId(true);
        add(feedbackPanel);
        add(new Label(YEAR_ID, Calendar.getInstance().get(Calendar.YEAR)));
        form = new DemoForm(config.getPanel());
        add(form);
    }

    private void addSettings() throws Exception {
        required(addHidden(INPUT));
        addDoc(INPUT);

        addTextField(OUTPUT);
        addCheckBox(WRITE_INDEX);
        addDoc(OUTPUT);

        addTextField(NODES).getFormComponent().add(RangeValidator.range(1, 4));
        addDoc(SETTINGS);
    }

    private void addSearch() throws Exception {
        conditionalRequired(addTextArea(SEARCH_QUERY), SEARCH);
        FormComponentPanel width = addTextField(SEARCH_WIDTH);
        width.getFormComponent().add(RangeValidator.minimum(0));
        conditionalRequired(width, SEARCH);
        addDoc(SEARCH);
        addResult(SEARCH);
    }

    private void addFuzzySearch() throws Exception {
        conditionalRequired(addTextArea(FUZZY_QUERY), FUZZY);
        FormComponentPanel width = addTextField(FUZZY_WIDTH);
        width.getFormComponent().add(RangeValidator.minimum(0));
        conditionalRequired(width, FUZZY);
        FormComponentPanel fuzziness = addTextField(FUZZYNESS);
        fuzziness.getFormComponent().add(RangeValidator.minimum(0));
        conditionalRequired(fuzziness, FUZZY);
        addDoc(FUZZY);
        addResult(FUZZY);
    }

    private void addTermFrequency() throws Exception {
        conditionalRequired(addTextField(TERM_FIELD), TERM);
        FormComponentPanel format = addTextField(TERM_FORMAT);
        format.getFormComponent().add(new TermFormatValidator());
        conditionalRequired(format, TERM);
        addDoc(TERM);
        addResult(TERM);
    }

    private void addSort() throws Exception {
        conditionalRequired(addTextField(SORT_FIELD), SORT);
        conditionalRequired(addChoice(SORT_ORDER, SortOrder.values()), SORT);
        addDoc(SORT);
        addResult(SORT);
    }

    // ****** Utility methods to create the different types of components on each panel *************************
    private FormComponentPanel addTextField(String id) throws Exception {
        FormComponentPanel component = new FormComponentPanel(id, TextField.class, this, query);
        form.add(component);
        return component;
    }

    private FormComponentPanel addTextArea(String id) throws Exception {
        FormComponentPanel component = new FormComponentPanel(id, TextArea.class, this, query);
        form.add(component);
        return component;
    }

    private FormComponentPanel addChoice(String id, Enum<?>... choices) throws Exception {
        FormComponentPanel component = new FormComponentPanel(id, RadioChoice.class, this, query, choices);
        form.add(component);
        return component;
    }

    private FormComponentPanel addCheckBox(String id, Enum<?>... choices) throws Exception {
        FormComponentPanel component = new FormComponentPanel(id, CheckBox.class, this, query);
        form.add(component);
        return component;
    }

    private FormComponentPanel addHidden(String id) throws Exception {
        FormComponentPanel component = new FormComponentPanel(id, HiddenField.class, this, query);
        form.add(component);
        return component;
    }

    private void addDoc(String id) throws IOException {
        // load the content of the panel's documentation from the HTML files under doc
        StringWriter writer = new StringWriter();
        IOUtils.copy(Thread.currentThread().getContextClassLoader().getResourceAsStream("doc/" + id + ".html"), writer);
        form.add(new DocumentationPanel(id + DOC_SUFFIX, writer.toString()).setEscapeModelStrings(false));
    }

    private void addResult(String id) {
        ResultPanel resultPanel = new ResultPanel(id + RESULT_SUFFIX, new Result());
        form.add(resultPanel);
        form.add(new ExecuteButton(id + EXECUTE_SUFFIX, form, query, resultPanel));
    }

    private void required(FormComponentPanel panel) {
        panel.getFormComponent().add(new Required<>(panel.getName()));
    }

    private void conditionalRequired(FormComponentPanel panel, String value) {
        panel.getFormComponent().add(new ConditionalRequired<>(panel.getName(), form.getPanelModel(), value, form.getExecuteModel()));
    }

    // ****** End Utility methods to create the different types of components on each panel **********************

    /*
     * Extension of Kendo's feedback panel with a filter using the 'IGNORE_FEEDBACK' metadata key to avoid duplication with embedded error messages
     */
    private static class FeedbackPanel extends KendoFeedbackPanel {
        public FeedbackPanel(String id) {
            super(id);
        }

        @Override
        protected FeedbackMessagesModel newFeedbackMessagesModel() {
            return new FeedbackMessagesModel(this).setFilter(new FeedbackFiler());
        }
    }

    private static class FeedbackFiler implements IFeedbackMessageFilter {
        @Override
        public boolean accept(FeedbackMessage message) {
            try {
                return !message.getReporter().getMetaData(IGNORE_FEEDBACK);
            } catch (NullPointerException e) {
                return true;
            }
        }
    }

}
