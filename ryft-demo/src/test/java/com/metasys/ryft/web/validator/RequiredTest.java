package com.metasys.ryft.web.validator;

import org.apache.wicket.validation.Validatable;
import org.junit.Assert;
import org.junit.Test;

public class RequiredTest {

    @Test
    public void testNull() {
        Required<String> required = new Required<>("name");
        Validatable<String> v = new Validatable<>((String) null);
        required.validate(v);
        assertValidatable(v, false);
    }

    @Test
    public void testEmpty() {
        Required<String> required = new Required<>("name");
        Validatable<String> v = new Validatable<>("");
        required.validate(v);
        assertValidatable(v, false);
    }

    @Test
    public void testValid() {
        Required<String> required = new Required<>("name");
        Validatable<String> v = new Validatable<>("anything");
        required.validate(v);
        assertValidatable(v, true);
    }

    @Test
    public void testNullNumber() {
        Required<Number> required = new Required<>("name");
        Validatable<Number> v = new Validatable<>((Number) null);
        required.validate(v);
        assertValidatable(v, false);
    }

    @Test
    public void testZeroNumber() {
        Required<Integer> required = new Required<>("name");
        Validatable<Integer> v = new Validatable<>(0);
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
