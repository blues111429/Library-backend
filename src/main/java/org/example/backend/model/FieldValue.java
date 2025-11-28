package org.example.backend.model;

public class FieldValue {
    private String field;
    private Object oldValue;
    private Object newValue;

    public FieldValue(String field, Object oldValue, Object newValue) {
        this.field = field;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public String getField() {
        return field;
    }

    public Object getNewValue() {
        return newValue;
    }

    public Object getOldValue() {
        return oldValue;
    }
}
