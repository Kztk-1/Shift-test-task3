package org.example.cli;

import org.example.dto.FilterConfig;
import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ArgsParserTest {

    @Test
    void parse_NoArguments_ShouldUseDefaults() throws ParseException {
        // Given
        String[] args = {"input.txt"};  // Добавляем входной файл

        // When
        FilterConfig config = ArgsParser.parse(args);

        // Then
        assertEquals(Path.of(""), config.outputPath(), "Default output path should be empty path");
        assertEquals("", config.filePrefix(), "Default file prefix should be empty string");
        assertFalse(config.appendMode(), "Append mode should be false by default");
        assertFalse(config.shortStats(), "Short statistics should be false by default");
        assertFalse(config.fullStats(), "Full statistics should be false by default");
        assertNotNull(config.inputFiles(), "Input files list should not be null");
        assertEquals(1, config.inputFiles().size(), "Input files should contain added file");
        assertEquals(Path.of("input.txt"), config.inputFiles().get(0));
    }

    @Test
    void parse_WithOutputAndPrefix_ShouldSetValues() throws ParseException {
        // Given
        String[] args = {"-o", "/tmp/output", "-p", "myprefix", "data.txt.txt"};

        // When
        FilterConfig config = ArgsParser.parse(args);

        // Then
        assertEquals(Path.of("/tmp/output"), config.outputPath(), "Output path should match provided value");
        assertEquals("myprefix", config.filePrefix(), "File prefix should match provided value");
        assertFalse(config.appendMode(), "Append mode should remain false when not set");
        assertFalse(config.shortStats(), "Short statistics should remain false when not set");
        assertFalse(config.fullStats(), "Full statistics should remain false when not set");
        assertNotNull(config.inputFiles(), "Input files list should not be null");
        assertEquals(1, config.inputFiles().size());
        assertEquals(Path.of("data.txt.txt"), config.inputFiles().get(0));
    }

    @Test
    void parse_WithFlags_ShouldEnableCorrespondingModes() throws ParseException {
        // Given
        String[] args = {"-a", "-s", "-f", "input.log"};

        // When
        FilterConfig config = ArgsParser.parse(args);

        // Then
        assertTrue(config.appendMode(), "Append mode should be enabled when -a is provided");
        assertTrue(config.shortStats(), "Short statistics should be enabled when -s is provided");
        assertTrue(config.fullStats(), "Full statistics should be enabled when -f is provided");
        // Defaults for path and prefix
        assertEquals(Path.of(""), config.outputPath(), "Output path should be default when not set");
        assertEquals("", config.filePrefix(), "File prefix should be default when not set");
        assertNotNull(config.inputFiles(), "Input files list should not be null");
        assertEquals(1, config.inputFiles().size());
        assertEquals(Path.of("input.log"), config.inputFiles().get(0));
    }

    @Test
    void parse_CombinedOptions_ShouldHandleAll() throws ParseException {
        // Given
        String[] args = {"-o", "outDir", "-p", "pre", "-a", "-s", "file1.csv", "file2.csv"};

        // When
        FilterConfig config = ArgsParser.parse(args);

        // Then
        assertEquals(Path.of("outDir"), config.outputPath());
        assertEquals("pre", config.filePrefix());
        assertTrue(config.appendMode());
        assertTrue(config.shortStats());
        assertFalse(config.fullStats(), "Full statistics should be false when -f is not provided");
        assertNotNull(config.inputFiles(), "Input files list should not be null");
        assertEquals(2, config.inputFiles().size());
        assertEquals(Path.of("file1.csv"), config.inputFiles().get(0));
        assertEquals(Path.of("file2.csv"), config.inputFiles().get(1));
    }
    @Test
    void parse_WithInputFiles_ShouldListThem() throws ParseException {
        // Given
        String[] args = {"input1.txt", "input2.log"};

        // When
        FilterConfig config = ArgsParser.parse(args);

        // Then
        List<Path> files = config.inputFiles();
        assertEquals(2, files.size(), "Should parse two input files");
        assertEquals(Path.of("input1.txt"), files.get(0));
        assertEquals(Path.of("input2.log"), files.get(1));
        // Other defaults
        assertEquals(Path.of(""), config.outputPath(), "Default output path should be empty path");
        assertEquals("", config.filePrefix(), "Default file prefix should be empty string");
        assertFalse(config.appendMode());
        assertFalse(config.shortStats());
        assertFalse(config.fullStats());
    }

    @Test
    void parse_InvalidOption_ShouldThrowParseException() {
        // Given
        String[] args = {"-x"};

        // When & Then
        assertThrows(ParseException.class, () -> ArgsParser.parse(args),
                "Parsing unknown option should throw ParseException");
    }
}

