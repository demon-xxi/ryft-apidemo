package com.metasys.ryft.web.validator;

import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

import com.metasys.ryft.Query;

/**
 * Validator checking the format field of a term frequency query. The format can be one of <code>RAW_TEXT</code>, <code>RECORD</code> or
 * <code>RECORD.[field name]</code>.
 *
 * @author Sylvain Crozon
 *
 */
public class TermFormatValidator extends Behavior implements IValidator<String> {

    @Override
    public void validate(IValidatable<String> validatable) {
        String format = validatable.getValue();
        if (format == null || !format.equalsIgnoreCase(Query.RAW) && !format.equalsIgnoreCase(Query.RECORD)
                && !format.toUpperCase().startsWith(Query.FIELD)) {
            validatable.error(new ValidationError(this));
        }
    }
}
