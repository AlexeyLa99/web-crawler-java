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
import webcrawler.output.OutputWriter;
import webcrawler.output.OutputWriterFactory;
import webcrawler.result.CrawlResult;
import webcrawler.result.PageData;

/**
 * Entry point – wires CLI → InputReader → WebCrawler → Analyses → OutputWriter.
 *
 * @author Alexey Laikov
 * @author Talia Barzilai
 */
public class Main {

    public static void main(String[] args) throws InterruptedException {
        CrawlConfig config = CliParser.parse(args);

        InputReader inputReader = config.getInputFile() != null
                ? new FileInputReader(config.getInputFile())
                : new ConsoleInputReader();

        WebCrawler crawler = new WebCrawler(config, inputReader);
        List<PageData> pages;
        try {
            pages = crawler.crawl();
        } catch (IOException e) {
            System.err.println("error reading seeds");
            return;
        }

        Map<String, Object> analysisMap = new LinkedHashMap<>();
        for (String name : config.getAnalyses()) {
            AnalysisStrategy strategy = AnalysisFactory.createStrategy(name);
            if (strategy != null) {
                analysisMap.put(name, strategy.analyze(pages));
            }
        }

        CrawlResult result = new CrawlResult(pages, analysisMap);

        OutputWriter writer = OutputWriterFactory.createWriter(config.getFormat());
        try {
            writer.write(result, config.getOutputFile());
        } catch (IOException e) {
            System.err.println("error saving report");
        }
    }
}
