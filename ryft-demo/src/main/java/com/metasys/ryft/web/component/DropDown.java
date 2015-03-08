package com.metasys.ryft.web.component;

import java.util.List;

import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.IModel;

public class DropDown<T> extends DropDownChoice<T> {

    private String nullKey;

    public DropDown(String id, IModel<T> model, List<? extends T> choices) {
        super(id, model, choices);
    }

    protected void setNullKey(String nullKey) {
        this.nullKey = nullKey;
    }

    @Override
    protected String getNullKey() {
        return nullKey;
    }

}
