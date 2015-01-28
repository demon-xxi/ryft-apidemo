package com.metasys.ryft.web.validator;

import org.apache.wicket.model.IModel;
import org.apache.wicket.validation.IValidatable;

/**
 * Required validator applying only when the value of a String model matched a target value and a Boolean model provides a <code>true</code> value. If
 * the 2 conditions are not met, the validation is not executed.
 *
 * @author Sylvain Crozon
 *
 * @param <T>
 *            the type of object being validated
 */
public class ConditionalRequired<T> extends Required<T> {

    private IModel<String> model;
    private String value;
    private IModel<Boolean> execute;

    /**
     * Builds a new validator that will be executed only when <code>execute.getObject()</code> and <code>value.equals(model.getObject())</code> are
     * <code>true</code>.
     *
     * @param name
     *            name of the component to validate
     * @param model
     *            the String model to check the value against
     * @param value
     *            the target value the model should have to apply the validation
     * @param execute
     *            the Boolean model defining is the validation should be applied or not
     */
    public ConditionalRequired(String name, IModel<String> model, String value, IModel<Boolean> execute) {
        super(name);
        this.model = model;
        this.value = value;
        this.execute = execute;
    }

    @Override
    public void validate(IValidatable<T> validatable) {
        if (execute.getObject() && value.equals(model.getObject())) {
            super.validate(validatable);
        }
    }

}
