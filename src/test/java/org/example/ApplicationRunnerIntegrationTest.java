package org.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

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

    @Test
    void shouldProcessMultipleFilesWithFullStats2(@TempDir Path tempDir) throws Exception {
        // Подготовка
        Path input1 = tempDir.resolve("in1.txt");
        Files.write(input1, Arrays.asList("42", "invalid", "3.14", "Hello"));

        Path input2 = tempDir.resolve("in2.txt");
        Files.write(input2, Arrays.asList("-5", "World", "2.71828E5", "0xFF"));

        String[] args = {
                "-o", tempDir.toString(),
                "-p", "result_",
                "-f",
                input1.toString(),
                input2.toString()
        };

        // Выполнение
        ApplicationRunner runner = new ApplicationRunner();
        int exitCode = runner.run(args);

        // Проверки
        assertEquals(0, exitCode);

        Path intFile = tempDir.resolve("result_integers.txt");
        Path floatFile = tempDir.resolve("result_floats.txt");
        Path stringFile = tempDir.resolve("result_strings.txt");

        assertTrue(Files.exists(intFile), "Файл целых чисел должен существовать");
        assertTrue(Files.exists(floatFile), "Файл дробных чисел должен существовать");
        assertTrue(Files.exists(stringFile), "Файл строк должен существовать");

        assertEqualsFileContent("42\n-5", intFile);
        assertEqualsFileContent("3.14\n2.71828E5", floatFile);
        assertEqualsFileContent("invalid\nHello\nWorld\n0xFF", stringFile);
    }

    @Test
    void shouldAppendToExistingFiles(@TempDir Path tempDir) throws Exception {
        ApplicationRunner runner = new ApplicationRunner();

        // Первый запуск
        Path input1 = tempDir.resolve("data.txt");
        Files.write(input1, List.of("100"));
        runner.run(new String[]{"-o", tempDir.toString(), input1.toString()});

        Path intFile = tempDir.resolve("integers.txt");
        assertTrue(Files.exists(intFile), "Файл целых чисел должен существовать после первого запуска");
        assertEqualsFileContent("100", intFile);

        // Второй запуск
        Path input2 = tempDir.resolve("new.txt");
        Files.write(input2, List.of("200"));
        runner.run(new String[]{"-a", "-o", tempDir.toString(), input2.toString()});

        assertTrue(Files.exists(intFile), "Файл целых чисел должен существовать после второго запуска");
        assertEqualsFileContent("100\n200", intFile);

        // Проверяем что другие файлы не созданы
        assertFalse(Files.exists(tempDir.resolve("floats.txt")), "Файл дробных чисел не должен создаваться");
        assertFalse(Files.exists(tempDir.resolve("strings.txt")), "Файл строк не должен создаваться");
    }

    @Test
    void shouldSkipInvalidData(@TempDir Path tempDir) throws Exception {
        Path input = tempDir.resolve("corrupted.txt");
        Files.write(input, Arrays.asList("123", "abc", "45.6", "789L"));

        ApplicationRunner runner = new ApplicationRunner();
        runner.run(new String[]{input.toString()});

        Path intFile = tempDir.resolve("integers.txt");
        Path floatFile = tempDir.resolve("floats.txt");
        Path stringFile = tempDir.resolve("strings.txt");

        assertTrue(Files.exists(intFile), "Файл целых чисел должен существовать");
        assertTrue(Files.exists(floatFile), "Файл дробных чисел должен существовать");
        assertTrue(Files.exists(stringFile), "Файл строк должен существовать");

        assertEqualsFileContent("123", intFile);
        assertEqualsFileContent("45.6", floatFile);
        assertEqualsFileContent("abc\n789L", stringFile);
    }

    @Test
    void shouldNotCreateFilesForEmptyInput(@TempDir Path tempDir) throws Exception {
        Path input = tempDir.resolve("empty.txt");
        Files.createFile(input);

        ApplicationRunner runner = new ApplicationRunner();
        runner.run(new String[]{input.toString()});

        // Явные проверки отсутствия файлов
        assertFalse(Files.exists(tempDir.resolve("integers.txt")), "Файл целых чисел не должен создаваться");
        assertFalse(Files.exists(tempDir.resolve("floats.txt")), "Файл дробных чисел не должен создаваться");
        assertFalse(Files.exists(tempDir.resolve("strings.txt")), "Файл строк не должен создаваться");
    }

    @Test
    void shouldCalculateFullStatsForNumbers(@TempDir Path tempDir) throws Exception {
        // Подготовка тестового файла
        Path input = tempDir.resolve("numbers.txt");
        Files.write(input, Arrays.asList("10", "-20", "3.5", "15.0", "text"));

        // Перенаправляем вывод для проверки
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(out));

        // Запуск приложения
        ApplicationRunner runner = new ApplicationRunner();
        int exitCode = runner.run(new String[]{"-f", input.toString()});

        // Восстановление System.out
        System.setOut(originalOut);

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
        assertEqualsFileContent("10\n-20", intFile);
        assertEqualsFileContent("3.5\n15.0", floatFile);
        assertEqualsFileContent("text", stringFile);

        // Проверка статистики
        String consoleOutput = out.toString();
        assertTrue(consoleOutput.contains("Integers: count=2, min=-20, max=10, sum=-10, avg=-5.0"),
                "Должна отображаться полная статистика для целых чисел");
        assertTrue(consoleOutput.contains("Floats: count=2, min=3.5, max=15.0, sum=18.5, avg=9.25"),
                "Должна отображаться полная статистика для дробных чисел");
    }

    @Test
    void shouldShowShortStats(@TempDir Path tempDir) throws Exception {
        // Подготовка тестового файла
        Path input = tempDir.resolve("data.txt.txt");
        Files.write(input, List.of("Test"));

        // Перенаправляем вывод для проверки
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(out));

        // Запуск приложения
        ApplicationRunner runner = new ApplicationRunner();
        int exitCode = runner.run(new String[]{"-s", input.toString()});

        // Восстановление System.out
        System.setOut(originalOut);

        // Проверка кода завершения
        assertEquals(0, exitCode, "Программа должна завершиться успешно");

        // Проверка выходных файлов
        Path stringFile = tempDir.resolve("strings.txt");
        assertTrue(Files.exists(stringFile), "Файл строк должен существовать");

        // Проверка отсутствия файлов для чисел
        assertFalse(Files.exists(tempDir.resolve("integers.txt")),
                "Файл целых чисел не должен создаваться");
        assertFalse(Files.exists(tempDir.resolve("floats.txt")),
                "Файл дробных чисел не должен создаваться");

        // Проверка содержимого файла
        assertEqualsFileContent("Test", stringFile);

        // Проверка статистики
        assertTrue(out.toString().contains("Всего элементов: 1 (int: 0, float: 0, string: 1)"),
                "Должна отображаться краткая статистика для строк");
    }

    @Test
    void shouldHandleExtremeValues(@TempDir Path tempDir) throws Exception {
        // Подготовка тестовых данных
        String bigInt = "9223372036854775807"; // Long.MAX_VALUE
        String bigFloat = "1.7976931348623157E308"; // Double.MAX_VALUE
        String longStr = "a".repeat(10000);

        // Создание входного файла
        Path input = tempDir.resolve("big.txt");
        Files.write(input, Arrays.asList(bigInt, bigFloat, longStr));

        // Запуск приложения
        ApplicationRunner runner = new ApplicationRunner();
        int exitCode = runner.run(new String[]{"-o", tempDir.toString(), input.toString()});

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
        assertEqualsFileContent(bigInt, intFile);
        assertEqualsFileContent(bigFloat, floatFile);
        assertEqualsFileContent(longStr, stringFile);
    }

    /*
    @Test
    void shouldHandlePermissionDenied(@TempDir Path tempDir) throws Exception {
        // Создание защищенной директории
        Path outputDir = tempDir.resolve("protected");
        Files.createDirectory(outputDir);

        // Установка прав только на чтение
        outputDir.toFile().setReadOnly();

        // Создание входного файла
        Path input = tempDir.resolve("data.txt.txt");
        Files.write(input, List.of("test"));

        // Запуск приложения
        ApplicationRunner runner = new ApplicationRunner();
        int exitCode = runner.run(new String[]{"-o", outputDir.toString(), input.toString()});

        // Проверка кода завершения
        assertEquals(2, exitCode, "Программа должна вернуть код ошибки 2");

        // Проверка отсутствия файлов в защищенной директории
        assertFalse(Files.exists(outputDir.resolve("integers.txt")),
                "Файл целых чисел не должен создаваться");
        assertFalse(Files.exists(outputDir.resolve("floats.txt")),
                "Файл дробных чисел не должен создаваться");
        assertFalse(Files.exists(outputDir.resolve("strings.txt")),
                "Файл строк не должен создаваться");
    }
     */

    @Test
    void shouldParseSpecialNumberFormats(@TempDir Path tempDir) throws Exception {
        // Подготовка тестового файла
        Path input = tempDir.resolve("data.txt.txt");
        Files.write(input, Arrays.asList(
                "0x10", // не число (строка)
                "1e-10",
                "0b101", // строка
                ".5"
        ));

        // Запуск приложения
        ApplicationRunner runner = new ApplicationRunner();
        int exitCode = runner.run(new String[]{input.toString()});

        // Проверка кода завершения
        assertEquals(0, exitCode, "Программа должна завершиться успешно");

        // Проверка выходных файлов
        Path floatFile = tempDir.resolve("floats.txt");
        Path stringFile = tempDir.resolve("strings.txt");

        assertTrue(Files.exists(floatFile), "Файл дробных чисел должен существовать");
        assertTrue(Files.exists(stringFile), "Файл строк должен существовать");

        // Проверка отсутствия файла целых чисел
        assertFalse(Files.exists(tempDir.resolve("integers.txt")),
                "Файл целых чисел не должен создаваться");

        // Проверка содержимого файлов
        assertEqualsFileContent("1e-10\n.5", floatFile);
        assertEqualsFileContent("0x10\n0b101", stringFile);
    }

    void assertEqualsFileContent(String expected, Path file) {
        String actual;

        try {
            // Получение и приведение к общему виду
            actual = Files.readString(file)
                    .replaceAll("\r\n", "\n")
                    .replaceAll("\r", "\n")
                    .replaceAll("\t", "\n");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertEquals(expected + "\n", actual);
    }

}