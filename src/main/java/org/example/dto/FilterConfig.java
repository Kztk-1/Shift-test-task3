package org.example.dto;

import java.nio.file.Path;

public record FilterConfig (
        Path outputPath,      // Путь для результатов
        String filePrefix,    // Префикс файлов
        boolean appendMode,   // Режим добавления
        boolean shortStats,   // Краткая статистика
        boolean fullStats     // Полная статистика
) {
    // Дефолтные значения
    public FilterConfig {
        outputPath = outputPath != null ? outputPath : Path.of("");
        filePrefix = filePrefix != null ? filePrefix : "";
    }
}