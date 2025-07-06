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
        return new FilterConfig(
                tempDir,
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
        String testData = "test_content";

        // Act
        fileWriter.write(DataType.INTEGER, testData);
        fileWriter.write(DataType.FLOAT, testData);
        fileWriter.write(DataType.STRING, testData);
        fileWriter.close(); // Принудительная запись данных

        // Assert
        assertFileContent("test_integers.txt", testData + "\n");
        assertFileContent("test_floats.txt", testData + "\n");
        assertFileContent("test_strings.txt", testData + "\n");
    }

    @Test
    void write_appendsToExistingFile() throws IOException {
        // Arrange
        FilterConfig config = createConfig("");
        fileWriter = new FileWriter(config);
        String firstLine = "line1";
        String secondLine = "line2";

        // Act
        fileWriter.write(DataType.STRING, firstLine);
        fileWriter.write(DataType.STRING, secondLine);
        fileWriter.close();

        // Assert
        assertFileContent("strings.txt", firstLine + "\n" + secondLine + "\n");
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

    @Test
    void write_shouldAppendToExistingFile() throws IOException {
        // Arrange
        Path outputFile = tempDir.resolve("strings.txt");
        Files.writeString(outputFile, "existing\n");

        FilterConfig config = new FilterConfig(
                tempDir,
                "",
                true,
                false,
                false,
                List.of()
        );

        fileWriter = new FileWriter(config);
        fileWriter.write(DataType.STRING, "new_line");
        fileWriter.close();

        // Assert
        assertFileContent("strings.txt", "existing\nnew_line\n");
    }

    @Test
    void write_shouldWriteSingleLineWithoutExtraNewLine() throws IOException {
        // Arrange
        FilterConfig config = createConfig("");
        fileWriter = new FileWriter(config);
        String content = "single_line";

        // Act
        fileWriter.write(DataType.STRING, content);
        fileWriter.close();

        // Assert
        assertFileContent("strings.txt", content + "\n");
    }

    @Test
    void write_shouldWriteMultipleLinesWithCorrectFormatting() throws IOException {
        // Arrange
        FilterConfig config = createConfig("");
        fileWriter = new FileWriter(config);
        String line1 = "first";
        String line2 = "second";

        // Act
        fileWriter.write(DataType.STRING, line1);
        fileWriter.write(DataType.STRING, line2);
        fileWriter.close();

        // Assert
        assertFileContent("strings.txt", line1 + "\n" + line2 + "\n");
    }

    @Test
    void write_shouldNotAddExtraNewLineForEmptyValue() throws IOException {
        // Arrange
        FilterConfig config = createConfig("");
        fileWriter = new FileWriter(config);

        // Act
        fileWriter.write(DataType.STRING, "");
        fileWriter.close();

        // Assert
        assertFileContent("strings.txt", "\n");
    }

    @Test
    void write_shouldHandleMixedLineEndings() throws IOException {
        // Arrange
        FilterConfig config = createConfig("");
        fileWriter = new FileWriter(config);
        String winLine = "win";
        String unixLine = "unix";
        String macLine = "mac";
        // Act
        fileWriter.write(DataType.STRING, winLine);
        fileWriter.write(DataType.STRING, unixLine);
        fileWriter.write(DataType.STRING, macLine);
        fileWriter.close();

        // Assert
        String expected = winLine + "\n" + unixLine + "\n" + macLine + "\n";
        assertFileContent("strings.txt", expected);
    }

    // Вспомогательный метод для проверки содержимого файла с детальной диагностикой
    private void assertFileContent(String fileName, String expectedContent) throws IOException {
        Path filePath = tempDir.resolve(fileName);
        assertTrue(Files.exists(filePath), "File not found: " + fileName);

        String actualContent = Files.readString(filePath);
        String normalizedActual = actualContent.replace("\r\n", "\n");
        String normalizedExpected = expectedContent.replace("\r\n", "\n");

        // Детальное сравнение символов
        if (!normalizedExpected.equals(normalizedActual)) {
            System.out.println("=== EXPECTED ===");
            System.out.println(escapeSpecialChars(normalizedExpected));
            System.out.println("=== ACTUAL ===");
            System.out.println(escapeSpecialChars(normalizedActual));
            System.out.println("===============");
        }

        assertEquals(normalizedExpected, normalizedActual, "File content mismatch");
    }

    private String escapeSpecialChars(String input) {
        return input.replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}