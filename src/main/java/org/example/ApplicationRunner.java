package org.example;

import org.example.cli.ArgsParser;
import org.example.dto.FilterConfig;
import org.example.engine.DataFilterEngine;
import org.example.model.TotalStats;
import org.example.util.StatisticsPrinter;

import java.io.IOException;

public class ApplicationRunner {
    private final ArgsParser argsParser;
    private final DataFilterEngine engine;
    private final StatisticsPrinter statisticsPrinter;

    // Цепочка конструкторов (тут вызвается конструктор для тестов и в него пихаются new-аргументы)
    public ApplicationRunner() {
        this(new ArgsParser(), new DataFilterEngine(), new StatisticsPrinter());
    }

    // Конструктор для тестов (внедрение зависимостей)
    public ApplicationRunner(ArgsParser argsParser,
                             DataFilterEngine engine,
                             StatisticsPrinter statisticsPrinter) {
        this.argsParser = argsParser;
        this.engine = engine;
        this.statisticsPrinter = statisticsPrinter;
    }

    public int run(String[] args) {
        try {
            // Получаем данные из args
            FilterConfig config = argsParser.parse(args);
            // Обработка файлов + сбор статистики + запись в новые файлы
            engine.process(config);
            // Получение собранной статистики
            TotalStats stats = engine.getTotalStats();
            // Вывод статистики
            statisticsPrinter.print(stats, config.fullStats());
            return 0;
        } catch (IllegalArgumentException e) {
            System.err.println("Argument error: " + e.getMessage());
            return 1;
        } catch (IOException e) {
            System.err.println("I/O error: " + e.getMessage());
            return 2;
        } catch (Exception e) {
            System.err.println("Processing error: " + e.getMessage());
            e.printStackTrace();
            return 3;
        }
    }
}