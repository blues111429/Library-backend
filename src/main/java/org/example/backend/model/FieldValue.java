package org.example.backend.model;

import lombok.Getter;

@Getter
public class FieldValue {
    private final String field;
    private final Object oldValue;
    private final Object newValue;

    public FieldValue(String field, Object oldValue, Object newValue) {
        this.field = field;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

}
