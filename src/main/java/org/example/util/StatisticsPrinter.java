package org.example.util;

import org.example.model.TotalStats;
import org.example.model.StringStatistic;
import org.example.model.IntegerStatistic;
import org.example.model.FloatStatistic;

/**
 * Утилита для вывода собранной статистики в консоль.
 */
public class StatisticsPrinter {

    public static void print(TotalStats stats, boolean fullStats) {
        StatisticMode mode = fullStats ? StatisticMode.ALL : StatisticMode.SUMMARY;
        switch (mode) {
            case SUMMARY:
                printSummary(stats);
                break;
            case ALL:
            default:
                printAll(stats);
                break;
        }
    }

    private static void printAll(TotalStats stats) {
        StringStatistic ss = stats.getStringStatistic();
        IntegerStatistic is = stats.getIntegerStatistic();
        FloatStatistic  fs = stats.getFloatStatistic();

        System.out.println("=== Полная статистика ===");
        System.out.printf("Строки: count=%d, minLen=%d, maxLen=%d\n",
                ss.getTypeCnt(), ss.getMinLen(), ss.getMaxLen());
        System.out.printf("Целые: count=%d, min=%d, max=%d, sum=%d, avg=%.3f\n",
                is.getTypeCnt(), is.getMin(), is.getMax(), is.getSum(), is.getMiddle());
        System.out.printf("Вещественные: count=%d, min=%.3f, max=%.3f, sum=%.3f, avg=%.3f\n",
                fs.getTypeCnt(), fs.getMin(), fs.getMax(), fs.getSum(), fs.getMiddle());
    }

    private static void printSummary(TotalStats stats) {
        IntegerStatistic is = stats.getIntegerStatistic();
        FloatStatistic  fs = stats.getFloatStatistic();

        System.out.println("=== Краткая статистика ===");
        System.out.printf("Всего элементов: %d (int: %d, float: %d, string: %d)\n",
                is.getTypeCnt() + fs.getTypeCnt() + stats.getStringStatistic().getTypeCnt(),
                is.getTypeCnt(), fs.getTypeCnt(), stats.getStringStatistic().getTypeCnt());
    }

    enum StatisticMode {
        SUMMARY, ALL
    }
}


