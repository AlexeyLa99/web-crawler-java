[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/QVsSPEN1)

# EX1 – Concurrent WebCrawler & Content Analyzer

## Student Emails

- alexayla@edu.jmc.ac.il
- taliabar@edu.jmc.ac.il

## How to Compile

Requirements: Java JDK 25, Maven 3.9+

```bash
mvn package -q
```

This produces `target/ex1-1.0-jar-with-dependencies.jar`.

## How to Run

```bash
java -jar target/ex1-1.0-jar-with-dependencies.jar \
  --analysis WORD_COUNT,BROKEN_LINKS,MOST_LINKED_DOMAIN,KEYWORD_FREQUENCY \
  --poolsize 4 \
  --depth 2 \
  --input seeds.txt \
  --output report.json
```

Optional flags:

```bash
  --format csv              # output format: json (default) or csv
  --domains example.com,foo.org   # restrict crawl to these hostnames
```

## Design Patterns Used

1. **Strategy** – `AnalysisStrategy` interface with 5 implementations; `OutputWriter` interface with 2 implementations
2. **Factory** – `AnalysisFactory` creates analysis strategies by name; `OutputWriterFactory` creates output writers by format string
3. **Builder** – `CrawlConfig.Builder` constructs the immutable configuration object
4. **Template Method** – `AbstractAnalysis` defines the `analyze()` skeleton; subclasses implement `doAnalyze()`
5. **Observer** – `CrawlObserver` / `CrawlSubject` interfaces notify registered analyses as pages are discovered

## Implemented Extensions

1. **CSV output format** (`--format csv`) – writes pages as CSV rows and analysis as key=value lines
2. **Domain filtering** (`--domains d1,d2,...`) – restricts crawling to a hostname whitelist
3. **AVERAGE_WORD_COUNT analysis** – additional analysis reporting average words per crawled page
