package org.example.model;

import lombok.Data;

import java.math.BigInteger;

@Data
public class TotalStats {
    private StringStatistic  stringStatistic;
    private FloatStatistic   floatStatistic;
    private IntegerStatistic integerStatistic;

    public TotalStats() {
        stringStatistic  = new StringStatistic();
        floatStatistic   = new FloatStatistic();
        integerStatistic = new IntegerStatistic();
    }

    public void updateStat(String o) {
        switch (getType(o)) {
            case STRING  -> stringStatistic.updateStat(o);
            case FLOAT   -> floatStatistic.updateStat(Double.parseDouble(o));
            case INTEGER -> integerStatistic.updateStat(Long.parseLong(o));

            default -> throw new IllegalStateException("Unexpected value: " + getType(o));
        }
    }

    public DataType getType(String s) {
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
