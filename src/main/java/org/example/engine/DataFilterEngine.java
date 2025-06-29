package org.example.engine;

import lombok.Data;
import org.example.dto.FilterConfig;
import org.example.model.TotalStats;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;

@Data
public class DataFilterEngine {

    TotalStats totalStats;

    public DataFilterEngine() {
        totalStats = new TotalStats();
    }

    public void process(FilterConfig config) {
        // Читаем файлы построчно
        for (Path inputFile : config.inputFiles()) {
            try (BufferedReader reader = Files.newBufferedReader(inputFile)) {
                String word;

                while ((word = reader.readLine()) != null) {
                    // Определяем тип и обновляем статистику
                    totalStats.updateStat(word);
                    }
                } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // Пишем файлы
        FileWriter writer = new FileWriter(config);
    }

}
