package com.nastrsoft.converter;

import com.nastrsoft.model.Type;

import java.beans.PropertyEditorSupport;

public class TypeConverter extends PropertyEditorSupport {
    @Override
    public void setAsText(final String text) {
        setValue(Type.valueOf(text.trim().toUpperCase()));
    }
}
