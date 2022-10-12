package xyz.aquinoa;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.nio.file.*;
import java.util.regex.Pattern;

/**
 * Download and save sutra pages from cttb.
 */
public class App {
    private static final Pattern FILE_PATTERN = Pattern.compile("[A-Za-z0-9_\\.]+\\.html$");

    public static void main(String[] args) throws IOException {
        if (args.length > 0) {
            save(args[0]);
        }
    }

    public static void save(String url) throws IOException {
        var response = Jsoup.connect(url)
            .method(Connection.Method.GET)
            .execute();

        try (var bs = response.bodyStream()) {
            var match = FILE_PATTERN.matcher(response.url().getFile());
            if (match.find()) {
                var fileName = match.group(0);
                Files.copy(bs, Path.of(fileName));
            }
        }
    }
}
