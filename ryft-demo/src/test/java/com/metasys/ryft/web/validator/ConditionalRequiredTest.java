package com.metasys.ryft.web.validator;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.validation.Validatable;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ConditionalRequiredTest {

    private IModel<String> stringModel;
    private IModel<Boolean> booleanModel;
    private String sValue;
    private Boolean bValue;

    @Before
    public void setUp() {
        stringModel = new Model<String>() {
            @Override
            public String getObject() {
                return sValue;
            }
        };
        booleanModel = new Model<Boolean>() {
            @Override
            public Boolean getObject() {
                return bValue;
            }
        };
    }

    @Test
    public void testNull_false() {
        bValue = false;
        sValue = "value";
        ConditionalRequired<String> required = new ConditionalRequired<>("name", stringModel, "value", booleanModel);
        Validatable<String> v = new Validatable<>((String) null);
        required.validate(v);
        assertValidatable(v, true);
    }

    @Test
    public void testNull_wrongValue() {
        bValue = true;
        sValue = "wrong";
        ConditionalRequired<String> required = new ConditionalRequired<>("name", stringModel, "value", booleanModel);
        Validatable<String> v = new Validatable<>((String) null);
        required.validate(v);
        assertValidatable(v, true);
    }

    @Test
    public void testNull() {
        bValue = true;
        sValue = "value";
        ConditionalRequired<String> required = new ConditionalRequired<>("name", stringModel, "value", booleanModel);
        Validatable<String> v = new Validatable<>((String) null);
        required.validate(v);
        assertValidatable(v, false);
    }

    @Test
    public void testOK() {
        bValue = true;
        sValue = "value";
        ConditionalRequired<String> required = new ConditionalRequired<>("name", stringModel, "value", booleanModel);
        Validatable<String> v = new Validatable<>("anything");
        required.validate(v);
        assertValidatable(v, true);
    }

    private void assertValidatable(Validatable<?> v, boolean valid) {
        Assert.assertEquals(valid, v.isValid());
        if (!valid) {
            Assert.assertEquals(1, v.getErrors().size());
            Assert.assertEquals("[ValidationError message=[null], keys=[Required], variables=[[name=name]]]", v.getErrors().get(0).toString());
        }
    }
}
