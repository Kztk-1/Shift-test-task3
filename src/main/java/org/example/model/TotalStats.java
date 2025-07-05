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

    public void updateStat(DataType type, String o) {
        switch (type) {
            case STRING  -> stringStatistic.updateStat(o);
            case FLOAT   -> floatStatistic.updateStat(Double.parseDouble(o));
            case INTEGER -> integerStatistic.updateStat(Long.parseLong(o));

            default -> throw new IllegalStateException("Unexpected value: " + type);
        }
    }

}
