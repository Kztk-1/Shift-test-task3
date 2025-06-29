package org.example.engine;

import org.example.dto.FilterConfig;
import org.example.model.DataType;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.Map;

public class FileWriter {

    private FilterConfig config;
    private final Map<DataType, BufferedWriter> writers;

    public FileWriter(FilterConfig config) {
        this.config = config;
        writers = new EnumMap<>(DataType.class);
    }

    public void write(DataType type, String value) {

        try {
            var writer = getOrCreate(type);
            writer.write(value);

        } catch (IOException e) {
            handleWriteError(type, e);
        }


    }

    private BufferedWriter getOrCreate(DataType type) throws IOException {
        if (writers.containsKey(type)) {
            return writers.get(type);
        }

        Path typePath = buildFilePath(type);
        BufferedWriter typeWriter = Files.newBufferedWriter(typePath);
        System.out.println(typeWriter.toString());
        writers.put(type, typeWriter);
        return typeWriter;
    }

    private void handleWriteError(DataType type, IOException e) {
        System.err.println("🚨 Ошибка записи в файл для " + type + ": " + e.getMessage());

        // Закрываем проблемный writer
        closeWriter(type);
    }

    private void closeWriter(DataType type) {
        try {
            BufferedWriter writer = writers.get(type);
            if (writer != null) {
                writer.close();
            }
        } catch (IOException e) {
            System.err.println("⚠️ Ошибка при закрытии файла " + type + ": " + e.getMessage());
        } finally {
            writers.remove(type);
        }
    }

    private Path buildFilePath(DataType type) {
        String fileName = switch (type) {
            case INTEGER -> "integers.txt";
            case FLOAT -> "floats.txt";
            case STRING -> "strings.txt";
        };
        return config.outputPath().resolve(config.filePrefix() + fileName);
    }

    public void close() {
        writers.forEach((type, writer) -> closeWriter(type));
    }
}
