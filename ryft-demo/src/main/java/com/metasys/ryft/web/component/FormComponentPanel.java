package com.metasys.ryft.web.component;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.FeedbackMessages;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;

import com.metasys.ryft.Query;
import com.metasys.ryft.web.DemoPage;

/**
 * Panel wrapping an input field for a {@link Query} object and a label for feedback/error messages.
 *
 * @author Sylvain Crozon
 *
 */
public class FormComponentPanel extends Panel {

    // Wicket ID of the field
    private static final String FIELD_ID = "field";

    // Wicket ID of the fragment and its possible values
    private static final String FRAGMENT_ID = "fragment";
    private static final String TEXT_INPUT_FRAGMENT = "input";
    private static final String TEXT_FRAGMENT = "text";
    private static final String HIDDEN_FRAGMENT = "hidden";
    private static final String SPAN_FRAGMENT = "span";
    private static final String CHECKBOX_FRAGMENT = "checkbox";

    // Name of attributes to add to the form component
    private static final String PLACEHOLDER_ATTRIBUTE = "placeholder";
    private static final String TOOLTIP_ATTRIBUTE = "title";

    private FormComponent<?> formComponent;
    private String name;

    /**
     * Builds a new form component with the given id.
     *
     * @param id
     *            the Wicket ID of the component
     * @param componentType
     *            the type of form component to instantiate
     * @param container
     *            the container to use to resolve localized messages
     * @param query
     *            the query object the component is editing
     * @param choices
     *            optional list of possible choices for the component
     * @throws Exception
     *             if an error occurs building the component
     */
    @SuppressWarnings("rawtypes")
    public FormComponentPanel(String id, Class<? extends FormComponent> componentType, Component container, Query query, Enum<?>... choices)
            throws Exception {
        super(id);
        // the component may be validated during an AJAX call, the markup ID need to be set
        setOutputMarkupId(true);
        // localized name to use as a placeholder and in error messages
        name = new StringResourceModel(id, container, null, id).getObject();
        if (choices != null && choices.length > 0) {
            Constructor<? extends FormComponent> componentConstructor = componentType.getConstructor(String.class, IModel.class, List.class);
            formComponent = componentConstructor.newInstance(FRAGMENT_ID, new PropertyModel<>(query, id), Arrays.asList(choices));
        } else {
            Constructor<? extends FormComponent> componentConstructor = componentType.getConstructor(String.class, IModel.class);
            formComponent = componentConstructor.newInstance(FRAGMENT_ID, new PropertyModel<>(query, id));
        }
        formComponent.add(new AttributeModifier(PLACEHOLDER_ATTRIBUTE, name));
        formComponent.add(new AttributeModifier(TOOLTIP_ATTRIBUTE, new StringResourceModel(id + ".tooltip", container, null, id)));

        String fragmentName = SPAN_FRAGMENT;
        if (formComponent instanceof TextArea) {
            fragmentName = TEXT_FRAGMENT;
        } else if (formComponent instanceof CheckBox) {
            fragmentName = CHECKBOX_FRAGMENT;
        } else if (formComponent instanceof HiddenField) {
            fragmentName = HIDDEN_FRAGMENT;
        } else if (formComponent instanceof TextField) {
            fragmentName = TEXT_INPUT_FRAGMENT;
        }

        Fragment fragment = new Fragment(FIELD_ID, fragmentName, this);
        fragment.add(formComponent);
        add(fragment);

        add(new ComponentFeedback(this));
    }

    // Builds a string representation of all the feedback messages on the form component and its children.
    public String getFeedback() {
        final StringBuilder message = new StringBuilder();
        appendMessages(message, formComponent.getFeedbackMessages());
        formComponent.visitChildren(FormComponent.class, new IVisitor<FormComponent<?>, Void>() {
            @Override
            public void component(FormComponent<?> child, IVisit<Void> paramIVisit) {
                child.setMetaData(DemoPage.IGNORE_FEEDBACK, true);
                appendMessages(message, child.getFeedbackMessages());
            }
        });
        return message.toString();
    }

    private void appendMessages(StringBuilder message, FeedbackMessages feedbackMessages) {
        for (FeedbackMessage feedbackMessage : feedbackMessages.messages(null)) {
            message.append(feedbackMessage.getMessage()).append("<br />");
            feedbackMessage.markRendered();
        }
        feedbackMessages.clear();
    }

    public FormComponent<?> getFormComponent() {
        return formComponent;
    }

    public String getName() {
        return name;
    }

}
