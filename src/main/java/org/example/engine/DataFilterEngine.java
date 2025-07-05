package org.example.engine;

import lombok.Data;
import org.example.dto.FilterConfig;
import org.example.model.DataType;
import org.example.model.TotalStats;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Data
public class DataFilterEngine {

    TotalStats totalStats;

    public DataFilterEngine() {
        totalStats = new TotalStats();
    }

    public void process(FilterConfig config) {
        FileWriter writer = new FileWriter(config);

        // Читаем файлы построчно
        for (Path inputFile : config.inputFiles()) {
            try (BufferedReader reader = Files.newBufferedReader(inputFile)) {
                String word;

                while ((word = reader.readLine()) != null) {
                    // Определяем тип
                    DataType type = getType(word);
                    // Обновляем статистику
                    totalStats.updateStat(type, word);
                    // Пишем файлы
                    writer.write(type, word);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        writer.close();
    }


    public DataType getType(String s) {
        if (s.isEmpty()) return DataType.STRING;

        try {
            Long.parseLong(s);
            return DataType.INTEGER;
        } catch (Exception e) {
            try {
                Double.parseDouble(s);
                return DataType.FLOAT;
            } catch (Exception ex) {
                return DataType.STRING;
            }
        }
    }

}
