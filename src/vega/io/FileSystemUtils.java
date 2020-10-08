package vega.io;

import polaris.Result;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author David Retzlaff
 * GitHub: https://github.com/Tyrrx}
 * Date: 02.10.2020, 18:06
 */

public class FileSystemUtils { 

    public static Path pathFromString(String path) {
        return Paths.get(path);
    }

    public static Result<Path> writeStringToPath(Path path, String str, StandardOpenOption openOption) {
        if (path.toFile().isFile()) {
            try {
                return Result.success(Files.writeString(path, str, openOption));
            } catch (IOException e) {
                return Result.failure(e.getMessage());
            }
        }
        return Result.failure(String.format("Path %s is not a file", path.toString()));
    }

    public static <T> Result<Path> writeLinesToPath(Path path, Collection<T> inputs, Function<T, String> lineMapper, StandardOpenOption openOption) {
        if (path.toFile().isFile()) {
            Optional<String> stringOptional = inputs.stream()
                .map(lineMapper)
                .reduce((a, b) ->
                    String.join(System.lineSeparator(), a, b));
            if (stringOptional.isPresent()) {
                try {
                    return Result.success(Files.writeString(path, stringOptional.get(), openOption));
                } catch (IOException e) {
                    return Result.failure(e.getMessage());
                }
            }
            return Result.success(path);
        }
        return Result.failure(String.format("Path %s is not a file", path.toString()));
    }

    public static <T> Result<List<T>> readLinesFromPath(Path path, Function<String, T> lineMapper) {
        if (path.toFile().isFile()) {
            try {
                return Result.success(Files.readAllLines(path).stream()
                    .map(lineMapper)
                    .collect(Collectors.toList()));
            } catch (IOException e) {
                return Result.failure(e.getMessage());
            }
        }
        return Result.failure(String.format("Path %s is not a file", path.toString()));
    }
}
