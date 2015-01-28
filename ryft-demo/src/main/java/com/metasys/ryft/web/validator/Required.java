package com.metasys.ryft.web.validator;

import java.util.HashMap;
import java.util.Map;

import org.apache.wicket.validation.INullAcceptingValidator;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.ValidationError;

/**
 * Custom required validator using the given name as the component's name in the error message.
 *
 * <p>
 * The validator fails if the object is null or it's an empty string.
 *
 * @author Sylvain Crozon
 *
 * @param <T>
 *            the type of object being validated
 */
public class Required<T> implements INullAcceptingValidator<T> {

    // Key for the error message
    private static final String KEY = "Required";
    // name of the variable containing the name of the component
    private static final String NAME_VAR = "name";
    private Map<String, Object> variables;

    public Required(String name) {
        variables = new HashMap<>();
        variables.put(NAME_VAR, name);
    }

    @Override
    public void validate(IValidatable<T> validatable) {
        if (validatable.getValue() == null || validatable.getValue() instanceof String && ((String) validatable.getValue()).length() == 0) {
            validatable.error(error());
        }
    }

    private ValidationError error() {
        ValidationError error = new ValidationError();
        error.addKey(KEY);
        error.setVariables(variables);
        return error;
    }

}
