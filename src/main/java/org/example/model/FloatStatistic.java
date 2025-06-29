package org.example.model;

import lombok.Data;

@Data
public class FloatStatistic extends TypeStatistic<Double> {

    double min = Integer.MAX_VALUE;
    double max = Integer.MIN_VALUE;
    double sum = 0;
    double middle;

    @Override
    protected void updateStat(Double d) {
        super.typeCnt++;
        min = Math.min(min, d);
        max = Math.max(max, d);
        sum += d;
        middle = sum / typeCnt;
    }

}