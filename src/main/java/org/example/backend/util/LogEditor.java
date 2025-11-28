package org.example.backend.util;

import java.util.*;

public class LogEditor {
    public static String generateEditLog(String entityName, String entityId,
                                         Map<String, Object> oldValues, Map<String, Object> newValues) {
        StringBuilder logBuilder = new StringBuilder(entityName + " ID: " + entityId + " 修改字段: ");

        oldValues.forEach((field, oldValue) -> {
            Object newValue = newValues.get(field);
            if(newValue != null && !newValue.equals(oldValue)) {
                logBuilder.append(field).append("从[").append(oldValue).append("]改为[").append(newValue).append("];");
            }
        });
        return logBuilder.toString();
    }
}