package com.github.kattlo.core.yaml;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import lombok.experimental.UtilityClass;

/**
 * @author fabiojose
 */
@UtilityClass
public class MigrationLoader {

    public static final Pattern VERSION_PATTERN =
        Pattern.compile("(v[0-9]{4})");

    public static final Pattern FILE_NAME_PATTERN =
        Pattern.compile("v[0-9]{4}_[\\w\\-]{0,246}\\.ya?ml");

    public static final Pattern VERSION_NUMBER_PATTERN =
        Pattern.compile("([0-9]{4})");

    public static final Pattern FILE_EXT_PATTERN =
        Pattern.compile(".*\\.ya?ml");
    /**
     * @throws IllegalArgumentException When the file name does not follow the pattern
     * {@link #FILE_NAME_PATTERN}
     */
    public static void matches(Path file) {
        if(!FILE_NAME_PATTERN.matcher(file.getFileName().toString()).matches()){
            throw new IllegalArgumentException(file.getFileName().toString());
        }
    }

    public static Optional<String> versionOf(Path file){

        final Matcher m =
            VERSION_PATTERN.matcher(file.getFileName().toString());

        return
            Optional.of(m.find())
                .filter(found -> found)
                .map(f -> m.group());

    }

    public static boolean greater(Path file, String currentVersion){
        return
            versionOf(file)
                .filter(version -> version.compareTo(currentVersion) > 0)
                .map(v -> Boolean.TRUE)
                .orElse(Boolean.FALSE);
    }

    public static Optional<String> versionNumberOf(String version){

        final Matcher m =
            VERSION_NUMBER_PATTERN.matcher(version);

        return
            Optional.of(m.find())
                .filter(found -> found)
                .map(f -> m.group());
    }

    public static Stream<Path> list(final Path directory) throws IOException {
        return Files.list(directory)
            .filter(f ->
                FILE_EXT_PATTERN.matcher(
                    f.getFileName().toString())
                        .matches());
    }

    public static Stream<Path> list(final File directory) throws IOException {
        return list(Path.of(directory.getAbsolutePath()));
    }
}
