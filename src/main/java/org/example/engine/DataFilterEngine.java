package org.example.engine;

import lombok.Data;
import org.example.dto.FilterConfig;
import org.example.model.DataType;
import org.example.model.TotalStats;
import org.example.util.FileWriter;
import org.example.util.TypeDetector;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Data
public class DataFilterEngine {

    TotalStats totalStats;
    TypeDetector typeDetector;

    public DataFilterEngine() {
        totalStats = new TotalStats();
        typeDetector = new TypeDetector();
    }

    public void process(FilterConfig config) throws IOException {
        if (!Files.isWritable(config.outputPath())) {
            throw new IOException();
        }

        FileWriter writer = new FileWriter(config);

        // Читаем файлы построчно
        for (Path inputFile : config.inputFiles()) {
            try (BufferedReader reader = Files.newBufferedReader(inputFile)) {
                String word;
                while ((word = reader.readLine()) != null) {
                    // Определяем тип
                    DataType type = typeDetector.detectType(word);
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


}
