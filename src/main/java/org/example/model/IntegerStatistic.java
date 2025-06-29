package org.example.model;

import lombok.Data;

@Data
public class IntegerStatistic extends TypeStatistic<Long> {

    long min = Integer.MAX_VALUE;
    long max = Integer.MIN_VALUE;
    long sum = 0;
    double middle;

    @Override
    protected void updateStat(Long integer) {
        super.typeCnt++;
        min = Math.min(min, integer);
        max = Math.max(max, integer);
        sum += integer;
        middle = (double) sum / typeCnt;
    }

}