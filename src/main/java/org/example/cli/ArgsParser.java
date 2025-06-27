package org.example.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.example.dto.FilterConfig;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Нужен для преобразования args в объект FilterConfig
 */
public class ArgsParser { // Command Line Interface Parser

    public static FilterConfig parse(String[] args) throws ParseException {
        Options options = new Options();
        options.addOption("o", "output", true,  "Output directory");
        options.addOption("p", "prefix", true,  "Filename prefix");
        options.addOption("a", false, "Append mode");
        options.addOption("s", false, "Short statistics");
        options.addOption("f", false, "Full statistics");

        CommandLine cmd = new DefaultParser().parse(options, args);

        // Получаем значения опций (null — если не задано)
        String outVal    = cmd.getOptionValue("o", null);
        String prefixVal = cmd.getOptionValue("p", null);

        // Преобразуем путь, или передаём null, чтобы record подставил Path.of("")
        Path outputPath = outVal != null ? Path.of(outVal) : null;

        // Получаем файлы
        List<Path> inputFiles = new ArrayList<>();
        for (String arg : cmd.getArgs()) {
            inputFiles.add(Path.of(arg));
        }

        // Строим и возвращаем FilterConfig
        return new FilterConfig(
                outputPath,         // будет "" по умолчанию, если null
                prefixVal,          // будет "" по умолчанию, если null
                cmd.hasOption("a"), // appendMode
                cmd.hasOption("s"), // shortStats
                cmd.hasOption("f"), // fullStats
                inputFiles          // Будет List.of() по умолчанию
        );
    }
}
