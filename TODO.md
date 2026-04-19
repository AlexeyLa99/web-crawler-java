# רשימת משימות – EX1 Concurrent WebCrawler

> **סמלים:** ✅ הושלם | 🔵 A = אלכסיי | 🟣 T = טליה
> כל branch נסגר עם PR review של הצד השני לפני merge.
> אף אחד לא עובר לספרינט הבא לפני שה-PR של השני נסגר.

---

## ✅ בסיס – הושלם (שניהם)

- [x] יצירת `pom.xml` עם Java 25, Jsoup 1.18.3, Jackson 2.17.2, maven-assembly-plugin
- [x] יצירת עץ תיקיות `src/main/java/webcrawler/{cli,crawler,analysis,output,input,filter,observer,result}`
- [x] `PageData.java` – url, title, bodyText, wordCount, outgoingLinks, domain, status, depth
- [x] `CrawlResult.java` – מחזיק `List<PageData>` + `Map<String,Object>`

---

## ✅ Sprint 1 – CLI (branch: `feature/cli-complete`) – **הושלם**

> 🔵 **אלכסיי** כתב | 🟣 **טליה** ביצעה review לפני merge

- [x] `CrawlConfig.java` – שדות `private final` + inner `Builder` עם כל ה-setters + `build()`
- [x] `CliParser.java` – פרסור כל 7 הדגלים עם ולידציה מלאה:
  - [x] `--analysis` – פיצול, סינון unknowns, יציאה אם ריק
  - [x] `--poolsize` – בדיקת > 0
  - [x] `--depth` – בדיקת >= 0
  - [x] `--input` – בדיקת קיום קובץ
  - [x] `--output` – שמירת נתיב
  - [x] `--format` – ברירת מחדל `json`
  - [x] `--domains` – פיצול לפי פסיק ל-Set

---

## ✅ Sprint 2 – Analysis + Infrastructure (מוזג ל-main)

> שני branches רצים במקביל, כל אחד עושה review של השני לפני merge.

### 🔵 אלכסיי – `feature/analysis-al` ✅

- [x] `analysis/AnalysisStrategy.java` – ממשק: `Object analyze(List<PageData>)`
- [x] `analysis/AbstractAnalysis.java` – Template Method: `analyze()` קורא ל-`doAnalyze()`
- [x] `analysis/WordCountAnalysis.java` – סכום מילים בדפי HTTP 200
- [x] `analysis/BrokenLinksAnalysis.java` – URLs עם status != 200, בסדר גילוי
- [x] `analysis/AnalysisFactory.java` – `createStrategy(String name)` → מימוש או null

### 🟣 טליה – `feature/infrastructure-tb`

- [x] `output/OutputWriter.java` – ממשק: `void write(CrawlResult, String filePath)`
- [x] `output/JsonOutputWriter.java` – Jackson ObjectMapper, pretty-print, מבנה `pages` + `analysis`
- [x] `output/OutputWriterFactory.java` – `createWriter(String format)`, ברירת מחדל json
- [x] `input/InputReader.java` – ממשק: `List<String> readSeeds()`
- [x] `input/FileInputReader.java` – קריאת שורות, דילוג על ריקות ו-`#`
- [x] `observer/CrawlObserver.java` – ממשק: `void onPageCrawled(PageData)`
- [x] `observer/CrawlSubject.java` – ממשקים: `addObserver()`, `notifyObservers()`
- [x] `filter/DomainFilter.java` – `isAllowed(url, allowed)`: ריקה = הכל מותר

---

## Sprint 3 – More Analyses + PageFetcher

### 🔵 אלכסיי – `feature/analyses-ext-al` ✅ (PR #1)

- [x] `analysis/MostLinkedDomainAnalysis.java` – דומיין עם הכי הרבה קישורים, שוויון = לקסיקוגרפי
- [x] `analysis/KeywordFrequencyAnalysis.java` – ספירת `java`, `thread`, `pattern` (case-insensitive)
- [x] `analysis/AverageWordCountAnalysis.java` – **(הרחבה)** ממוצע מילים לדף HTTP 200
- [x] `output/CsvOutputWriter.java` – **(הרחבה)** header + שורה לכל דף + ניתוחים

### 🟣 טליה – `feature/page-fetcher-tb` ✅

- [x] `crawler/PageFetcher.java`:
  - [x] Jsoup connection עם timeout=3000ms, followRedirects=true
  - [x] שמירת HTTP status code
  - [x] חילוץ `<title>` (ריק אם status != 200)
  - [x] ספירת מילים מ-`body().text()` (0 אם status != 200)
  - [x] חילוץ קישורים יוצאים `<a href>` כ-absolute URLs
  - [x] חילוץ hostname מה-URL
  - [x] URL לא תקין / לא http(s) → `<url> malformed` ל-stderr (מקביל ל-MalformedURLException)
  - [x] `IOException` → `<url> failed` ל-stderr
  - [x] **⚠️ לברר עם המרצה:** timeout – System.out או System.err? (כרגע: כמו שאר כשלי רשת → stderr)
- [x] `input/ConsoleInputReader.java` – **(הרחבה)** קריאת URLs מ-stdin

---

## Sprint 4 – Crawler Core (מקביל – API מוסכם מראש!)

> **לפני שמתחילים:** לשבת יחד ולהסכים:
> - `WebCrawler` מחזיק: `pool`, `visited`, `results`, `pendingTasks`, `doneLatch`
> - `CrawlTask` מקבל ב-constructor: `url`, `depth`, ref ל-`WebCrawler`

### 🔵 אלכסיי – `feature/webcrawler-al`

- [ ] `crawler/WebCrawler.java`:
  - [ ] שדות: `ExecutorService pool`, `ConcurrentHashMap<String,Integer> visited`, `AtomicInteger orderCounter`, `AtomicInteger pendingTasks`, `CountDownLatch doneLatch`, `List<PageData> results` (synchronizedList)
  - [ ] `crawl()` – טעינת זרעים, submit של `CrawlTask` ראשוניות (increment לפני submit)
  - [ ] `crawl()` – `doneLatch.await()` + `pool.shutdown()`

### 🟣 טליה – `feature/crawltask-tb` ✅ (ב־PR – מיזוג ל־`main` אחרי review)

- [x] `crawler/CrawlTask.java`:
  - [x] בדיקה אטומית: `visited.putIfAbsent(url, order)` – אם כבר קיים → return מיידי
  - [x] קריאה ל-`PageFetcher.fetch()`
  - [x] הכנסת `PageData` לרשימה בסדר גילוי (לפי order value)
  - [x] אם depth < maxDepth: submit child tasks (אחרי DomainFilter)
  - [x] `finally`: `pendingTasks.decrementAndGet()`, אם הגיע ל-0 → `doneLatch.countDown()`
  - [x] קריאה ל-`subject.notifyObservers(pageData)`

---

## Sprint 5 – Integration & Docs (שניהם)

### 🔵 אלכסיי – `feature/main-al`

- [x] `Main.java` – חיבור הכל: `CliParser` → `InputReader` → `WebCrawler` → ניתוחים → `CrawlResult` → `OutputWriter`
  - [x] טיפול ב-`IOException` עם `error saving report`
- [x] `seeds.txt` – 5+ URLs שיניבו **100+ דפים בסך הכל**

### 🟣 טליה – `feature/docs-tb`

- [ ] `README.md`:
  - [x] מיילים בפורמט `edu.jmc.ac.il` (**חובה! מייל חסר = -5 נקודות**)
  - [x] הוראות קומפילציה מדויקות
  - [x] הוראות הרצה מדויקות
  - [x] רשימת Design Patterns בשימוש
  - [x] רשימת הרחבות שמומשו
- [ ] בדיקות:
  - [ ] `poolSize=1`
  - [ ] `poolSize=4`
  - [ ] `depth=0`
  - [ ] `depth=2`
  - [ ] URLs כפולים בזרעים
  - [ ] מעגלים (cycles) – לא נתקעים
  - [ ] לפחות 100 דפים בסך הכל

---

## Git – דרישות הגשה

- [ ] commits משמעותיים לאורך כל הפיתוח
- [x] לפחות branch אחד עם merge (**לא למחוק branches לפני ההגשה!**) – `feature/analyses-ext-al` → `main` (PR #1)
- branches שנוצרו עד כה: `feature/cli-complete` ✅ | `feature/analyses-ext-al` ✅ (merged) | `feature/page-fetcher-tb` ✅ (merged PR #2) | `feature/crawltask-tb` (PR פתוח)

---

## סיכום התקדמות

| ספרינט | אחראי | משימות | הושלמו |
|--------|-------|--------|--------|
| בסיס (Maven + מודל) | שניהם | 4 | 4 ✅ |
| Sprint 1 – CLI | אלכסיי | 9 | 9 ✅ |
| Sprint 2 – Analysis | אלכסיי | 5 | 5 ✅ |
| Sprint 2 – Infrastructure | טליה | 8 | 8 ✅ |
| Sprint 3 – Analyses ext | אלכסיי | 4 | 4 ✅ |
| Sprint 3 – PageFetcher | טליה | 10 | 10 ✅ |
| Sprint 4 – WebCrawler | אלכסיי | 3 | 0 |
| Sprint 4 – CrawlTask | טליה | 6 | 6 ✅ |
| Sprint 5 – Main | אלכסיי | 3 | 3 ✅ |
| Sprint 5 – Docs & Tests | טליה | 10 | 5 (חלקי: README; בדיקות ידניות נשארות) |
| **סה"כ** | | **62** | **54** |
