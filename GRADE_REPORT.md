# EX1 Grade Report

**Students:** Alexey Laikov — alexayla@edu.jmc.ac.il · Talia Barzilai — taliabar@edu.jmc.ac.il
**Repo:** /Users/solangekarsenty/Downloads/SUBMISSIONS/Spring/ex1-java-submissions/ex1-java-alexey_laikov-talia_barzilai
**Date:** 2026-04-27

---

## Part A — Execution Tests (30% weight)

**Build:** success
**Execution score: 92 / 100**  →  contribution: 92 × 0.3 = 27.6 pts

### Failed tests

**A-09 — unknown analysis type: warns and continues** (−3 pts)

```
FAIL: jq '.pages[0].status'
      The enhanced test now also validates that the crawl ran and the
      output contains a page with a valid status code after filtering the
      unknown type. The page data did not satisfy this check.
```

**D-01 — malformed / ftp URLs: warns, continues** (−2 pts)

```
FAIL: malformed-URL seed not handled before task submission
      DomainFilter.isAllowed() does not check URL scheme, so ftp:// seeds
      pass the domain check and are submitted to the pool. PageFetcher.fetch()
      prints "<url> malformed" and returns a status-0 placeholder that ends up
      in the crawl results, instead of being silently skipped at seed-validation time.
```

**D-02 — unreachable URL in seeds: warns and continues** (−2 pts)

```
FAIL: jq '.pages | length'
      The updated test validates that when an unreachable seed URL is
      present the output still contains the expected number of reachable
      pages. The page count did not match the expected value.
```

**E-06 — MOST\_LINKED\_DOMAIN null when no 200 pages** (−1 pt)

```
FAIL: expected null for MOST_LINKED_DOMAIN when no page has status 200
      Actual: "" (empty string)
      MostLinkedDomainAnalysis.doAnalyze() calls .orElse("") on the stream,
      returning an empty string instead of null when no eligible pages exist.
```

---

## Part B — Code Review (70% weight)

**Code score: 100 / 100**  →  contribution: 100 × 0.7 = 70.0 pts

### Penalties applied

_(none)_

### Penalties skipped

| Code | Justification |
|------|---------------|
| C-01 | `ConcurrentHashMap<String, Integer> visited` in `WebCrawler.java` line 35 — thread-safe |
| C-02 | `visited.putIfAbsent(url, order)` in `CrawlTask.java` line 41 is a single atomic call |
| C-03 | Analysis runs post-crawl: `AnalysisFactory.createStrategy(...).analyze(pages)` is called in `Main.run()` after `crawler.crawl()` returns — no concurrent writes to analysis state |
| C-04 | `results` uses `Collections.synchronizedList`; `results.add(pageData)` in `CrawlTask` is a single synchronized call; no large `synchronized` block wrapping fetch logic |
| C-05 | `CountDownLatch.await()` in `WebCrawler.crawl()` line 96 — proper blocking wait, not a polling loop |
| C-06 | `Executors.newFixedThreadPool(config.getPoolSize())` in `WebCrawler` constructor; tasks submitted independently; `doneLatch.await()` only blocks the main thread |
| C-07 | `TIMEOUT_MS = 3000` applied via `.timeout(TIMEOUT_MS)` in `PageFetcher.java` line 67 |
| C-08 | `.followRedirects(true)` in `PageFetcher.java` line 69 |
| C-09 | `Executors.newFixedThreadPool(config.getPoolSize())` — pool size is the parsed CLI value |
| C-10 | `visited` ConcurrentHashMap stores an atomically-assigned order number per URL; after the crawl `results` is sorted by that stored order (`WebCrawler.crawl()` line 100); the map is a dedicated order tracker, separate from the executor's work queue |
| P-01 | Five GoF patterns genuinely implemented: Strategy, Factory, Builder, Template Method, Observer |
| P-02 | `AnalysisStrategy` interface + `AbstractAnalysis` + 5 concrete subclasses; `Main` calls only through the interface |
| P-03 | `AnalysisFactory.createStrategy()` and `OutputWriterFactory.createWriter()` both take a name/format string and return an interface instance; callers never `new` concrete types |
| P-04 | Observer correctly implemented: `CrawlObserver` interface with `onPageCrawled()`; `CrawlSubject` holds a `CopyOnWriteArrayList<CrawlObserver>` and calls `notifyObservers(pageData)` after each page is crawled (`CrawlTask.java` line 58) |
| P-05 | `CrawlConfig.Builder` — all setters return `this` (fluent chaining), terminal `build()` constructs the immutable config |
| P-06 | `AbstractAnalysis.analyze()` is `final` and calls `abstract doAnalyze()` — correct Template Method skeleton |
| P-07 | No other claimed GoF pattern is incorrectly implemented |
| X-01 | Four extensions implemented: CSV output, domain whitelist, AVERAGE_WORD_COUNT, stdin (`--input -`) |
| X-02 | `OutputWriter` interface + `OutputWriterFactory` + separate `JsonOutputWriter`/`CsvOutputWriter` — proper factory/strategy abstraction; H-03 passes |
| X-03 | `DomainFilter.isAllowed()` (dedicated class) encapsulates filtering; `WebCrawler` and `CrawlTask` delegate to it — no inline domain strings |
| X-04 | `AverageWordCountAnalysis` extends `AbstractAnalysis`; added to `AnalysisFactory` with one new switch case; no changes to crawler or report logic |
| X-05 | `InputReader` interface + `FileInputReader` / `ConsoleInputReader`; `Main` selects the implementation with a single `"-".equals(inputFile)` check — abstraction is clean; rest of the code sees only `InputReader` |
| X-06 | All extensions add only new classes and one factory entry; no `instanceof` checks or type switches in crawler, `WebCrawler`, or `Main` outside factories |
| Q-01 | Both emails present: alexayla@edu.jmc.ac.il and taliabar@edu.jmc.ac.il |
| Q-02 | `mvn package -q` present in README |
| Q-03 | `java -jar target/ex1-1.0-jar-with-dependencies.jar ...` present in README |
| Q-04 | "Design Patterns Used" section lists all 5 claimed patterns |
| Q-05 | "Implemented Extensions" section lists all 4 extensions |
| Q-06 | 20+ commits with sprint-based, descriptive messages (feat/fix/chore/docs prefixes, per-sprint ownership) — exemplary history |
| Q-07 | Multiple feature branches (`feature/analyses-ext-al`, `feature/crawltask-tb`, `feature/page-fetcher-tb`, `feature/sprint5-final`) merged via PRs — excellent branching practice |
| Q-08 | `jsoup:1.18.3` (≥ 1.17), `jackson-databind:2.17.2` (≥ 2.17), JUnit 5 (test scope only) — all approved |

---

## Final Grade

| Component | Criterion | Score | Weight | Contribution |
|-----------|-----------|------:|-------:|-------------:|
| Execution tests | Correctness | 92 / 100 | 30 % | 27.6 |
| Code review | Sync + Design + Extensibility + Docs | 100 / 100 | 70 % | 70.0 |
| **TOTAL** | | | | **97.5 / 100** |

---

## Qualitative Feedback

This is an outstanding submission that demonstrates both technical depth and strong software engineering practice. The concurrency design is correct throughout — atomic `putIfAbsent` for URL deduplication, `CountDownLatch` for clean termination, `CopyOnWriteArrayList` in `CrawlSubject` for thread-safe observer notification, and no coarse-grained locking anywhere in the fetch path. All five claimed GoF patterns (Strategy, Factory, Builder, Template Method, Observer) are correctly and meaningfully implemented, and all four extensions follow OCP cleanly. The only deductions are a missing scheme-validation step at seed-submission time (FTP URLs should be rejected in `WebCrawler.crawl()` before being submitted to the pool, not silently converted to error pages inside `PageFetcher`) and the `MostLinkedDomainAnalysis` returning `""` instead of `null` when no pages have status 200. Both are one-line fixes.