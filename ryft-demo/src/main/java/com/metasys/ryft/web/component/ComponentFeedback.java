package com.metasys.ryft.web.component;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.PropertyModel;

import com.metasys.ryft.web.DemoPage;

/**
 * Panel displaying error messages related to a form component when it is not valid.
 *
 * @author Sylvain Crozon
 *
 */
public class ComponentFeedback extends Label {

    private static final String FEEDBACK_ID = "feedback";

    private FormComponentPanel componentPanel;

    public ComponentFeedback(FormComponentPanel componentPanel) {
        super(FEEDBACK_ID, new PropertyModel<String>(componentPanel, FEEDBACK_ID));
        this.componentPanel = componentPanel;
        setEscapeModelStrings(false);
        componentPanel.getFormComponent().add(new ErrorBehavior());
        componentPanel.getFormComponent().setMetaData(DemoPage.IGNORE_FEEDBACK, true);
    }

    @Override
    public boolean isVisible() {
        return !componentPanel.getFormComponent().isValid();
    }

    // component behavior adding the "formError" class when it is not valid
    static class ErrorBehavior extends Behavior {

        private static final String CLASS_ATTRIBUTE = "class";
        private static final String FIELD_ERROR_STYLE = "formError";

        @Override
        public void onComponentTag(Component component, ComponentTag tag) {
            super.onComponentTag(component, tag);
            if (component.hasErrorMessage()) {
                tag.append(CLASS_ATTRIBUTE, FIELD_ERROR_STYLE, " ");
            }
        }
    }
}
