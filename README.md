# Concurrent WebCrawler & Content Analyzer

A multithreaded web crawler built in Java. Reads seed URLs, fetches pages in parallel using a thread pool, tracks visited URLs to avoid duplicates, runs pluggable content analyses, and writes a structured JSON or CSV report.

## Requirements

- **JDK 25** (matches `pom.xml`)
- **Maven 3.9+** *or* use the included Maven Wrapper (`mvnw` / `mvnw.cmd`)

## How to Compile

From the repository root:

```bash
mvn package -q
```

On Windows (PowerShell), if `mvn` is not on your PATH:

```powershell
.\mvnw.cmd package -q
```

Artifact produced:

- `target/ex1-1.0-jar-with-dependencies.jar`

## How to Run

### Required flags

| Flag | Meaning |
|------|---------|
| `--analysis` | Comma-separated strategy names (see below) |
| `--poolsize` | Thread-pool size (integer > 0) |
| `--depth` | Maximum crawl depth (integer ≥ 0; `0` = seeds only) |
| `--input` | Path to a seed URL file, or `-` to read seeds from **stdin** |
| `--output` | Output report path (JSON or CSV) |

### Optional flags

| Flag | Meaning |
|------|---------|
| `--format` | `json` (default) or `csv` |
| `--domains` | Comma-separated hostname whitelist (empty / omitted = all hosts allowed) |

### Recognized analysis names

- `WORD_COUNT`
- `BROKEN_LINKS`
- `MOST_LINKED_DOMAIN`
- `KEYWORD_FREQUENCY`
- `AVERAGE_WORD_COUNT` (extension)

### Example: file input + JSON output

```bash
java -jar target/ex1-1.0-jar-with-dependencies.jar \
  --analysis WORD_COUNT,BROKEN_LINKS,MOST_LINKED_DOMAIN,KEYWORD_FREQUENCY,AVERAGE_WORD_COUNT \
  --poolsize 4 \
  --depth 2 \
  --input seeds.txt \
  --output report.json
```

### Example: stdin seeds (`--input -`)

One URL per line; blank lines and lines starting with `#` are ignored. End stdin: **Ctrl+Z** then Enter on Windows, **Ctrl+D** on Linux/macOS.

```bash
java -jar target/ex1-1.0-jar-with-dependencies.jar \
  --analysis WORD_COUNT,BROKEN_LINKS \
  --poolsize 2 \
  --depth 1 \
  --input - \
  --output report.json
```

### Example: CSV output + domain filter

```bash
java -jar target/ex1-1.0-jar-with-dependencies.jar \
  --analysis WORD_COUNT,MOST_LINKED_DOMAIN \
  --poolsize 4 \
  --depth 2 \
  --input seeds.txt \
  --output report.csv \
  --format csv \
  --domains en.wikipedia.org,example.com
```

### Output shape (JSON)

The report contains top-level keys `pages` (array of page objects) and `analysis` (object keyed by strategy name). Pages appear in **first-discovery order**.

### Error messages (stdout / stderr)

| Situation | Message | Stream |
|-----------|---------|--------|
| Invalid / missing input file | `invalid input file` | stderr |
| Invalid pool size | `invalid pool size` | stderr |
| Invalid depth | `invalid depth` | stderr |
| Unknown analysis name | `<NAME> is unknown` | stderr |
| No valid analyses after filtering | `no valid analysis` | stderr |
| Bad URL shape / scheme | `<url> malformed` | stderr |
| Network / fetch failure | `<url> failed` | stderr |
| Output write failure | `error saving report` | stdout |

## Design Patterns Used

1. **Strategy** – `AnalysisStrategy` with multiple concrete analyses; `OutputWriter` with JSON/CSV implementations
2. **Factory** – `AnalysisFactory`, `OutputWriterFactory`
3. **Builder** – `CrawlConfig.Builder` for immutable configuration
4. **Template Method** – `AbstractAnalysis` (`analyze` / `doAnalyze`)
5. **Observer** – `CrawlObserver` / `CrawlSubject` for page-crawl notifications

## Implemented Extensions

1. **CSV output** – `--format csv` (`CsvOutputWriter`)
2. **Domain whitelist** – `--domains host1,host2,...` (`DomainFilter`)
3. **AVERAGE_WORD_COUNT** – extra analysis (`AverageWordCountAnalysis`)
4. **Console seed input** – `--input -` uses `ConsoleInputReader` (stdin)

## Seeds file

`seeds.txt` in the repo root lists HTTP(S) seed URLs (one per line, `#` comments supported).

## How to Run Tests

Unit tests are written with JUnit 5 (Jupiter) and live under `src/test/java`.

```bash
mvn test
```

On Windows (PowerShell), if `mvn` is not on your PATH:

```powershell
.\mvnw.cmd test
```
