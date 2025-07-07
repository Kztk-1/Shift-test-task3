package org.example.util;

import org.example.model.DataType;

public class TypeDetector {
    public DataType detectType(String s) {
        if (s.isEmpty()) return DataType.STRING;

        try {
            Long.parseLong(s);
            return DataType.INTEGER;
        } catch (Exception e) {
            try {
                Double.parseDouble(s);
                return DataType.FLOAT;
            } catch (Exception ex) {
                return DataType.STRING;
            }
        }
    }
}
