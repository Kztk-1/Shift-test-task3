package org.example.util;

import org.example.model.TotalStats;
import org.example.model.StringStatistic;
import org.example.model.IntegerStatistic;
import org.example.model.FloatStatistic;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Утилита для вывода собранной статистики в консоль.
 */
public class StatisticsPrinter {

    // Формат для дробей: максимум 3 цифры после точки, минимум 0
    private static final DecimalFormat DF_FLOAT;
    // Формат для среднего по целым: максимум 3 цифры после точки, минимум 1
    private static final DecimalFormat DF_INT_AVG;

    static {
        DF_FLOAT = new DecimalFormat("#.###");
        DF_FLOAT.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.US));
        DF_FLOAT.setMinimumFractionDigits(1);

        DF_INT_AVG = new DecimalFormat("#.###");
        DF_INT_AVG.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.US));
        DF_INT_AVG.setMinimumFractionDigits(1);
    }

    public static void print(TotalStats stats, boolean fullStats) {
        if (fullStats) {
            printAll(stats);
        } else {
            printSummary(stats);
        }
    }

    private static void printAll(TotalStats stats) {
        StringStatistic ss = stats.getStringStatistic();
        IntegerStatistic is = stats.getIntegerStatistic();
        FloatStatistic  fs = stats.getFloatStatistic();

        System.out.println("=== Полная статистика ===");
        System.out.printf("Strings: count=%d, minLen=%d, maxLen=%d%n",
                ss.getTypeCnt(), ss.getMinLen(), ss.getMaxLen());

        System.out.printf("Integers: count=%d, min=%d, max=%d, sum=%d, avg=%s%n",
                is.getTypeCnt(),
                is.getMin(),
                is.getMax(),
                is.getSum(),
                DF_INT_AVG.format(is.getMiddle()));

        System.out.printf("Floats: count=%d, min=%s, max=%s, sum=%s, avg=%s%n",
                fs.getTypeCnt(),
                DF_FLOAT.format(fs.getMin()),
                DF_FLOAT.format(fs.getMax()),
                DF_FLOAT.format(fs.getSum()),
                DF_FLOAT.format(fs.getMiddle()));
    }

    private static void printSummary(TotalStats stats) {
        IntegerStatistic is = stats.getIntegerStatistic();
        FloatStatistic  fs = stats.getFloatStatistic();
        int stringCnt = stats.getStringStatistic().getTypeCnt();

        System.out.println("=== Краткая статистика ===");
        System.out.printf("Всего элементов: %d (int: %d, float: %d, string: %d)%n",
                is.getTypeCnt() + fs.getTypeCnt() + stringCnt,
                is.getTypeCnt(), fs.getTypeCnt(), stringCnt);
    }
}
