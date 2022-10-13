package xyz.aquinoa;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

/**
 * Download and save sutra pages from cttb.
 */
public class App {
    private static final Pattern FILE_PATTERN = Pattern.compile("[A-Za-z0-9_\\.]+\\.html$");

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.out.println("No start page url!");
            return;
        }

        var startUrl = args[0];
        downloadSutra(startUrl);
    }

    private static void downloadSutra(String startUrl) throws IOException {
        for (var currentUrl = startUrl; currentUrl != null; ) {
            var doc = Jsoup.connect(startUrl).get();
            var nextUrl = getNextUrl(doc);
            cleanPage(doc);
            save(doc);

            currentUrl = nextUrl;
        }
    }

    private static String getNextUrl(Document doc) {
        try {
            var previousUrl = doc.connection().response().url();
            var relativePath = doc
                .selectFirst("a:containsOwn(next)")
                .attr("href");

            var newUrl = new URL(previousUrl, relativePath);

            return newUrl.toString();
        } catch (Exception e) {
            System.out.println("No next url.");
            return null;
        }
    }

    private static void cleanPage(Document doc) {

        //remove header
        doc.selectFirst("#wrapper")
            .selectFirst("table")
            .remove();

        //remove images
        doc.getElementsByTag("img")
            .stream()
            .forEach(e -> e.remove());

        //remove next page buttons
        doc.select("p.style19[align=\"center\"]")
            .remove();

        //remove page numbers
        doc.getElementsByClass("style157")
            .remove();

    }

    private static void save(Document doc) throws IOException {
        var htmlPath = doc.connection().response().url().getFile();
        var match = FILE_PATTERN.matcher(htmlPath);
        if (match.find()) {
            var fileName = match.group(0);
            Files.write(Path.of(fileName), doc.html().getBytes());
        }
    }
}
