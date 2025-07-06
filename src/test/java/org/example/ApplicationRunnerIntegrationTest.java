package org.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationRunnerIntegrationTest {

    @Test
    void shouldProcessMultipleFilesWithFullStats(@TempDir Path tempDir) throws Exception {
        // Подготовка тестовых файлов
        Path inputFile1 = tempDir.resolve("data1.txt");
        Files.writeString(inputFile1, "123\nHello World\n45.67\n-42");

        Path inputFile2 = tempDir.resolve("data2.txt");
        Files.writeString(inputFile2, "3.14159\nAnother string\n9876543210\n0");

        // Аргументы командной строки
        String[] args = {
                "-o", tempDir.toString(),
                "-p", "output_",
                "-f",
                inputFile1.toString(),
                inputFile2.toString()
        };

        // Запуск приложения
        ApplicationRunner runner = new ApplicationRunner();
        int exitCode = runner.run(args);

        // Проверка кода завершения
        assertEquals(0, exitCode, "Программа должна завершиться успешно");

        // Проверка выходных файлов
        Path intFile = tempDir.resolve("output_integers.txt");
        Path floatFile = tempDir.resolve("output_floats.txt");
        Path stringFile = tempDir.resolve("output_strings.txt");

        assertTrue(Files.exists(intFile), "Файл целых чисел должен существовать");
        assertTrue(Files.exists(floatFile), "Файл дробных чисел должен существовать");
        assertTrue(Files.exists(stringFile), "Файл строк должен существовать");

        // Проверка содержимого файлов
        assertEqualsFileContent("123\n-42\n9876543210\n0", intFile);
        assertEqualsFileContent("45.67\n3.14159", floatFile);
        assertEqualsFileContent("Hello World\nAnother string", stringFile);
    }

    @Test
    void shouldHandleDifferentDataTypesWithShortStats(@TempDir Path tempDir) throws Exception {
        // Подготовка тестового файла с разными типами данных
        Path inputFile = tempDir.resolve("mixed.txt");
        Files.writeString(inputFile, "100\n"
                + "3.14\n"
                + "Test string\n"
                + "\n"  // Пустая строка
                + "-42\n"
                + "123ABC\n"  // Не число (строка)
                + "0.001\n"
                + "98765432109876543210\n"
                + "Last line");

        // Аргументы командной строки
        String[] args = {
                "-o", tempDir.toString(),
                "-s",
                inputFile.toString()
        };

        // Запуск приложения
        ApplicationRunner runner = new ApplicationRunner();
        int exitCode = runner.run(args);

        // Проверка кода завершения
        assertEquals(0, exitCode, "Программа должна завершиться успешно");

        // Проверка выходных файлов
        Path intFile = tempDir.resolve("integers.txt");
        Path floatFile = tempDir.resolve("floats.txt");
        Path stringFile = tempDir.resolve("strings.txt");

        assertTrue(Files.exists(intFile), "Файл целых чисел должен существовать");
        assertTrue(Files.exists(floatFile), "Файл дробных чисел должен существовать");
        assertTrue(Files.exists(stringFile), "Файл строк должен существовать");

        // Проверка содержимого файлов
        assertEqualsFileContent("100\n-42", intFile);
        assertEqualsFileContent("3.14\n0.001\n98765432109876543210", floatFile);
        assertEqualsFileContent("Test string\n\n123ABC\nLast line", stringFile);
    }

    @Test
    void shouldWorkWithDefaultParameters(@TempDir Path tempDir) throws Exception {
        // Подготовка тестовых файлов
        Path inputFile1 = tempDir.resolve("fileA.txt");
        Files.writeString(inputFile1, "42\n3.14\nFirst");

        Path inputFile2 = tempDir.resolve("fileB.txt");
        Files.writeString(inputFile2, "-100\n0.001\nSecond");

        // Аргументы командной строки (только имена файлов)
        String[] args = {
                inputFile1.toString(),
                inputFile2.toString()
        };

        // Запуск приложения
        ApplicationRunner runner = new ApplicationRunner();
        int exitCode = runner.run(args);

        // Проверка кода завершения
        assertEquals(0, exitCode, "Программа должна завершиться успешно");

        // Проверка выходных файлов в текущей директории (tempDir)
        Path intFile = tempDir.resolve("integers.txt");
        Path floatFile = tempDir.resolve("floats.txt");
        Path stringFile = tempDir.resolve("strings.txt");

        assertTrue(Files.exists(intFile), "Файл целых чисел должен существовать");
        assertTrue(Files.exists(floatFile), "Файл дробных чисел должен существовать");
        assertTrue(Files.exists(stringFile), "Файл строк должен существовать");

        // Проверка содержимого файлов
        assertEqualsFileContent("42\n-100", intFile);
        assertEqualsFileContent("3.14\n0.001", floatFile);
        assertEqualsFileContent("First\nSecond", stringFile);
    }

    @Test
    void shouldRespectAppendMode(@TempDir Path tempDir) throws Exception {
        // Подготовка тестовых файлов
        Path inputFile1 = tempDir.resolve("part1.txt");
        Files.writeString(inputFile1, "100\nText1\n3.14");

        Path inputFile2 = tempDir.resolve("part2.txt");
        Files.writeString(inputFile2, "200\nText2\n6.28");

        // Первый запуск - создание файлов
        String[] firstArgs = {
                "-o", tempDir.toString(),
                "-a",
                inputFile1.toString()
        };

        ApplicationRunner runner = new ApplicationRunner();
        runner.run(firstArgs);

        // Второй запуск - добавление в существующие файлы
        String[] secondArgs = {
                "-o", tempDir.toString(),
                "-a",
                inputFile2.toString()
        };

        runner.run(secondArgs);

        // Проверка содержимого файлов
        Path intFile = tempDir.resolve("integers.txt");
        Path floatFile = tempDir.resolve("floats.txt");
        Path stringFile = tempDir.resolve("strings.txt");

        assertEqualsFileContent("100\n200", intFile);
        assertEqualsFileContent("3.14\n6.28", floatFile);
        assertEqualsFileContent("Text1\nText2", stringFile);
    }


    void assertEqualsFileContent(String expected, Path file) {
        String actual;

        try {
            // Получение и приведение к общему виду
            actual = Files.readString(file)
                    .replaceAll("\r\n", "\n")
                    .replaceAll("\r", "\n");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        assertEquals(expected + "\n", actual);
    }
}