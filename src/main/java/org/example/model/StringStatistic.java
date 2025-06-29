package org.example.model;

import lombok.Data;

@Data
public class StringStatistic extends TypeStatistic<String> {

    int minLen = Integer.MAX_VALUE;
    int maxLen = Integer.MIN_VALUE;

    @Override
    protected void updateStat(String s) {
        super.typeCnt++;
        minLen = Math.min(minLen, s.length());
        maxLen = Math.max(maxLen, s.length());
    }
}
