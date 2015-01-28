package com.metasys.ryft.web.validator;

import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.Validatable;
import org.junit.Assert;
import org.junit.Test;

public class TermFormatValidatorTest {

    @Test
    public void testNull() {
        IValidatable<String> v = new Validatable<>((String) null);
        new TermFormatValidator().validate(v);
        Assert.assertFalse(v.isValid());
    }

    @Test
    public void testEmpty() {
        IValidatable<String> v = new Validatable<>("");
        new TermFormatValidator().validate(v);
        Assert.assertFalse(v.isValid());
    }

    @Test
    public void testRawUpper() {
        IValidatable<String> v = new Validatable<>("RAW_TEXT");
        new TermFormatValidator().validate(v);
        Assert.assertTrue(v.isValid());
    }

    @Test
    public void testRawLower() {
        IValidatable<String> v = new Validatable<>("raw_text");
        new TermFormatValidator().validate(v);
        Assert.assertTrue(v.isValid());
    }

    @Test
    public void testRecordUpper() {
        IValidatable<String> v = new Validatable<>("RECORD");
        new TermFormatValidator().validate(v);
        Assert.assertTrue(v.isValid());
    }

    @Test
    public void testRecordLower() {
        IValidatable<String> v = new Validatable<>("record");
        new TermFormatValidator().validate(v);
        Assert.assertTrue(v.isValid());
    }

    @Test
    public void testFieldUpper() {
        IValidatable<String> v = new Validatable<>("RECORD.field");
        new TermFormatValidator().validate(v);
        Assert.assertTrue(v.isValid());
    }

    @Test
    public void testFieldLower() {
        IValidatable<String> v = new Validatable<>("record.field");
        new TermFormatValidator().validate(v);
        Assert.assertTrue(v.isValid());
    }

    @Test
    public void testBadField() {
        IValidatable<String> v = new Validatable<>("recordfield");
        new TermFormatValidator().validate(v);
        Assert.assertFalse(v.isValid());
    }

}
