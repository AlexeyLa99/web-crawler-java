package webcrawler;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import webcrawler.analysis.AnalysisFactory;
import webcrawler.analysis.AnalysisStrategy;
import webcrawler.cli.CliParser;
import webcrawler.cli.CrawlConfig;
import webcrawler.crawler.WebCrawler;
import webcrawler.input.ConsoleInputReader;
import webcrawler.input.FileInputReader;
import webcrawler.input.InputReader;
import webcrawler.output.OutputWriterFactory;
import webcrawler.result.CrawlResult;
import webcrawler.result.PageData;

/**
 * Entry point: parses CLI, runs the concurrent crawl, executes analyses, writes the report.
 *
 * @author Alexey Laikov
 * @author Talia Barzilai
 */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        try {
            run(args);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.exit(1);
        }
    }

    private static void run(String[] args) throws InterruptedException {
        CrawlConfig config = CliParser.parse(args);
        validate(config);

        InputReader inputReader = "-".equals(config.getInputFile())
                ? new ConsoleInputReader()
                : new FileInputReader(config.getInputFile());

        WebCrawler crawler = new WebCrawler(config, inputReader);
        List<PageData> pages;
        try {
            pages = crawler.crawl();
        } catch (IOException e) {
            System.err.println("error reading seeds");
            return;
        }

        Map<String, Object> analysis = new LinkedHashMap<>();
        for (String name : config.getAnalyses()) {
            AnalysisStrategy strategy = AnalysisFactory.createStrategy(name);
            if (strategy != null) {
                analysis.put(name, strategy.analyze(pages));
            }
        }

        CrawlResult result = new CrawlResult(pages, analysis);
        try {
            OutputWriterFactory.createWriter(config.getFormat()).write(result, config.getOutputFile());
        } catch (IOException e) {
            System.err.println("error saving report");
        }
    }

    private static void validate(CrawlConfig config) {
        if (config.getAnalyses() == null || config.getAnalyses().isEmpty()) {
            System.err.println("no valid analysis");
            System.exit(1);
        }
        if (config.getPoolSize() <= 0) {
            System.err.println("invalid pool size");
            System.exit(1);
        }
        if (config.getMaxDepth() < 0) {
            System.err.println("invalid depth");
            System.exit(1);
        }
        if (config.getInputFile() == null || config.getInputFile().isBlank()) {
            System.err.println("invalid input file");
            System.exit(1);
        }
        if (config.getOutputFile() == null || config.getOutputFile().isBlank()) {
            System.err.println("missing --output path");
            System.exit(1);
        }
    }
}
