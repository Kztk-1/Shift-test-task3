package org.example.engine;

import org.example.dto.FilterConfig;
import org.example.model.DataType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileWriterTest {

    @TempDir
    Path tempDir;
    private FileWriter fileWriter;

    @AfterEach
    void tearDown() {
        if (fileWriter != null) {
            fileWriter.close();
        }
    }

    // Вспомогательный метод для создания конфига
    private FilterConfig createConfig(String prefix) {
        return new FilterConfig(tempDir,
                prefix,
                false,
                false,
                false,
                List.of()
        );
    }

    @Test
    void write_createsCorrectFiles() throws IOException {
        // Arrange
        FilterConfig config = createConfig("test_");
        fileWriter = new FileWriter(config);
        String testData = "test_content\n";

        // Act
        fileWriter.write(DataType.INTEGER, testData);
        fileWriter.write(DataType.FLOAT, testData);
        fileWriter.write(DataType.STRING, testData);
        fileWriter.close(); // Принудительная запись данных

        // Assert
        assertFileContent("test_integers.txt", testData);
        assertFileContent("test_floats.txt", testData);
        assertFileContent("test_strings.txt", testData);
    }

    @Test
    void write_appendsToExistingFile() throws IOException {
        // Arrange
        FilterConfig config = createConfig("");
        fileWriter = new FileWriter(config);
        String firstLine = "line1\n";
        String secondLine = "line2\n";

        // Act
        fileWriter.write(DataType.STRING, firstLine);
        fileWriter.write(DataType.STRING, secondLine);
        fileWriter.close();

        // Assert
        assertFileContent("strings.txt", firstLine + secondLine);
    }

    @Test
    void write_handlesIoException() {
        // Arrange
        FilterConfig config = createConfig("invalid_");
        fileWriter = new FileWriter(config);

        // Создаем директорию с именем файла -> вызовет ошибку при записи
        assertDoesNotThrow(() -> Files.createDirectory(tempDir.resolve("invalid_strings.txt")));

        // Act & Assert (проверяем что не бросает исключение)
        assertDoesNotThrow(() -> fileWriter.write(DataType.STRING, "should_fail"));
    }

    // Вспомогательный метод для проверки содержимого файла
    private void assertFileContent(String fileName, String expectedContent) throws IOException {
        Path filePath = tempDir.resolve(fileName);
        assertTrue(Files.exists(filePath), "File not found: " + fileName);
        assertEquals(expectedContent, Files.readString(filePath));
    }
}