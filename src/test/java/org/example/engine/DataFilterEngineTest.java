package org.example.engine;

import org.example.dto.FilterConfig;
import org.example.model.DataType;
import org.example.model.TotalStats;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DataFilterEngineTest {

    @TempDir
    Path tempDir;

    private FilterConfig createConfig(Path... inputFiles) {
        return new FilterConfig(
                tempDir,
                "test_",
                false,
                false,
                false,
                List.of(inputFiles)
        );
    }

    private Path createTempFile(String fileName, String content) throws IOException {
        Path file = tempDir.resolve(fileName);
        Files.writeString(file, content);
        return file;
    }

    private String readOutputFile(String filename) throws IOException {
        Path output = tempDir.resolve("test_" + filename);
        return Files.exists(output) ? Files.readString(output) : "";
    }

    @Test
    void process_capturesWriterCreationInStdOut() throws IOException {
        Path file = createTempFile("single.txt", "1\n");
        FilterConfig config = createConfig(file);
        DataFilterEngine engine = new DataFilterEngine();

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));
        try {
            engine.process(config);
        } finally {
            System.setOut(originalOut);
        }

        String logs = outContent.toString(StandardCharsets.UTF_8.name());
        assertTrue(logs.contains("BufferedWriter@"), "Expected writer creation log in stdout, but got: " + logs);
    }

    @Test
    void process_readsMultipleFilesAndUpdatesStats() throws IOException {
        Path file1 = createTempFile("file1.txt", "10\n3.14\nhello\n");
        Path file2 = createTempFile("file2.txt", "-5\nworld\n");
        FilterConfig config = createConfig(file1, file2);

        DataFilterEngine engine = new DataFilterEngine();
        engine.process(config);

        TotalStats stats = engine.getTotalStats();
        assertEquals(2, stats.getIntegerStatistic().getTypeCnt());
        assertEquals(1, stats.getFloatStatistic().getTypeCnt());
        assertEquals(2, stats.getStringStatistic().getTypeCnt());

        assertEquals("10-5", readOutputFile("integers.txt").replaceAll("\\R", ""));
        assertEquals("3.14", readOutputFile("floats.txt").replaceAll("\\R", ""));
        assertEquals("helloworld", readOutputFile("strings.txt").replaceAll("\\R", ""));
    }

    @Test
    void process_handlesEmptyFiles() throws IOException {
        Path emptyFile = createTempFile("empty.txt", "");
        FilterConfig config = createConfig(emptyFile);
        DataFilterEngine engine = new DataFilterEngine();
        engine.process(config);

        TotalStats stats = engine.getTotalStats();
        assertEquals(0, stats.getIntegerStatistic().getTypeCnt());
        assertEquals(0, stats.getFloatStatistic().getTypeCnt());
        assertEquals(0, stats.getStringStatistic().getTypeCnt());
    }

    @Test
    void process_handlesMixedContent() throws IOException {
        Path file = createTempFile("mixed.txt",
                "100\n" +
                        "3.14\n" +
                        "text\n" +
                        "\n" +
                        "-42\n" +
                        "123ABC\n" +
                        "0.001\n");

        FilterConfig config = createConfig(file);
        DataFilterEngine engine = new DataFilterEngine();
        engine.process(config);

        TotalStats stats = engine.getTotalStats();
        assertEquals(2, stats.getIntegerStatistic().getTypeCnt());
        assertEquals(2, stats.getFloatStatistic().getTypeCnt());
        assertEquals(3, stats.getStringStatistic().getTypeCnt());

        assertTrue(readOutputFile("integers.txt").contains("100"));
        assertTrue(readOutputFile("floats.txt").contains("3.14"));
        assertTrue(readOutputFile("strings.txt").contains("text"));
    }

    @Test
    void process_handlesLargeNumbers() throws IOException {
        Path file = createTempFile("large.txt",
                "2147483648\n" +
                        "1.7976931348623157E308\n");

        FilterConfig config = createConfig(file);
        DataFilterEngine engine = new DataFilterEngine();
        engine.process(config);

        TotalStats stats = engine.getTotalStats();
        assertEquals(1, stats.getIntegerStatistic().getTypeCnt());
        assertEquals(1, stats.getFloatStatistic().getTypeCnt());
    }

    @Test
    void process_throwsExceptionOnInvalidFile() {
        Path invalidFile = tempDir.resolve("non_existent.txt");
        FilterConfig config = createConfig(invalidFile);
        DataFilterEngine engine = new DataFilterEngine();

        assertThrows(RuntimeException.class, () -> engine.process(config));
    }

    @Test
    void process_handlesMultipleFilesWithSameContent() throws IOException {
        Path file1 = createTempFile("file1.txt", "10\n20\n");
        Path file2 = createTempFile("file2.txt", "30\n40\n");
        FilterConfig config = createConfig(file1, file2);
        DataFilterEngine engine = new DataFilterEngine();
        engine.process(config);

        TotalStats stats = engine.getTotalStats();
        assertEquals(4, stats.getIntegerStatistic().getTypeCnt());
        assertEquals(10, stats.getIntegerStatistic().getMin());
        assertEquals(40, stats.getIntegerStatistic().getMax());
        assertEquals(100, stats.getIntegerStatistic().getSum());

        assertEquals("10203040", readOutputFile("integers.txt").replaceAll("\\R", ""));
    }
}
