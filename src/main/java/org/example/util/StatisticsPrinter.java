package org.example.util;

import org.example.model.TotalStats;
import org.example.model.StringStatistic;
import org.example.model.IntegerStatistic;
import org.example.model.FloatStatistic;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Класс для вывода статистики в консоль
 */
public class StatisticsPrinter {
    private final DecimalFormat floatFormat;
    private final DecimalFormat intAvgFormat;

    public StatisticsPrinter() {
        this.floatFormat = createFloatFormat();
        this.intAvgFormat = createIntAvgFormat();
    }

    public void print(TotalStats stats, boolean fullStats) {
        if (fullStats) {
            printAll(stats);
        } else {
            printSummary(stats);
        }
    }

    private void printAll(TotalStats stats) {
        StringStatistic ss = stats.getStringStatistic();
        IntegerStatistic is = stats.getIntegerStatistic();
        FloatStatistic fs = stats.getFloatStatistic();

        System.out.println("=== Полная статистика ===");
        System.out.printf("Strings: count=%d, minLen=%d, maxLen=%d%n",
                ss.getTypeCnt(), ss.getMinLen(), ss.getMaxLen());

        System.out.printf("Integers: count=%d, min=%d, max=%d, sum=%d, avg=%s%n",
                is.getTypeCnt(),
                is.getMin(),
                is.getMax(),
                is.getSum(),
                intAvgFormat.format(is.getMiddle()));

        System.out.printf("Floats: count=%d, min=%s, max=%s, sum=%s, avg=%s%n",
                fs.getTypeCnt(),
                floatFormat.format(fs.getMin()),
                floatFormat.format(fs.getMax()),
                floatFormat.format(fs.getSum()),
                floatFormat.format(fs.getMiddle()));
    }

    private void printSummary(TotalStats stats) {
        IntegerStatistic is = stats.getIntegerStatistic();
        FloatStatistic fs = stats.getFloatStatistic();
        int stringCnt = stats.getStringStatistic().getTypeCnt();

        System.out.println("=== Краткая статистика ===");
        System.out.printf("Всего элементов: %d (int: %d, float: %d, string: %d)%n",
                is.getTypeCnt() + fs.getTypeCnt() + stringCnt,
                is.getTypeCnt(), fs.getTypeCnt(), stringCnt);
    }

    private static DecimalFormat createFloatFormat() {
        DecimalFormat df = new DecimalFormat("#.###");
        df.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.US));
        df.setMinimumFractionDigits(1);
        return df;
    }

    private static DecimalFormat createIntAvgFormat() {
        DecimalFormat df = new DecimalFormat("#.###");
        df.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.US));
        df.setMinimumFractionDigits(1);
        return df;
    }
}