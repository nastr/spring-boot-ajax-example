package com.nastrsoft.converter;

import com.nastrsoft.model.Type;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class TypeConverter implements Converter<String, Type> {

    @Nullable
    @Override
    public Type convert(String s) {
        return Type.fromValue(s.trim().toUpperCase());
    }
}
