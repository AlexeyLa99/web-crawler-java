# רשימת משימות – EX1 Concurrent WebCrawler

> סדר הביצוע: מלמטה למעלה – כל שלב בנוי על הקודם.
> סמן ✅ כאשר משימה הושלמה.

---

## שלב 1 – הגדרת הפרויקט (Maven) 🏗️

- [x] יצירת `pom.xml` עם groupId=`webcrawler`, artifactId=`ex1`, גרסה=`1.0`, Java 25
- [x] הוספת תלות Jsoup 1.18.3
- [x] הוספת תלות Jackson Databind 2.17.2
- [x] הגדרת `maven-assembly-plugin` עם Main-Class = `webcrawler.Main`
- [x] יצירת עץ התיקיות: `src/main/java/webcrawler/{cli,crawler,analysis,output,input,filter,observer,result}`

---

## שלב 2 – מודל הנתונים 📦

- [ ] `PageData.java` – שדות: url, title, wordCount, outgoingLinks, domain, status, depth (getters בלבד)
- [ ] `CrawlResult.java` – מחזיק `List<PageData>` ו-`Map<String,Object>` לניתוחים

---

## שלב 3 – פרסור שורת הפקודה (CLI) ⌨️

- [ ] `CrawlConfig.java` – כל השדות כ-`private final`
- [ ] `CrawlConfig.Builder` – inner class עם setters ו-`build()`
- [ ] `CliParser.java` – פרסור `--analysis`, ולידציה, הודעת `<name> is unknown`, יציאה אם אין תקף
- [ ] `CliParser` – פרסור `--poolsize`, ולידציה > 0, הודעת `invalid pool size`
- [ ] `CliParser` – פרסור `--depth`, ולידציה >= 0, הודעת `invalid depth`
- [ ] `CliParser` – פרסור `--input`, בדיקת קריאות, הודעת `invalid input file`
- [ ] `CliParser` – פרסור `--output` (שמירת הנתיב בלבד)
- [ ] `CliParser` – פרסור אופציונלי `--format` (ברירת מחדל `json`) ו-`--domains`

---

## שלב 4 – ניתוחים (Analysis Strategies) 📊

- [ ] `AnalysisStrategy.java` – ממשק: `Object analyze(List<PageData> pages)`
- [ ] `AbstractAnalysis.java` – מחלקה מופשטת (Template Method): `analyze()` קורא ל-`doAnalyze()`
- [ ] `WordCountAnalysis.java` – סכום מילים בכל דפי HTTP 200
- [ ] `MostLinkedDomainAnalysis.java` – דומיין עם הכי הרבה קישורים יוצאים אליו, שוויון = לקסיקוגרפי
- [ ] `BrokenLinksAnalysis.java` – URLs עם סטטוס != 200, בסדר גילוי
- [ ] `KeywordFrequencyAnalysis.java` – ספירת `java`, `thread`, `pattern` (case-insensitive)
- [ ] `AverageWordCountAnalysis.java` – **(הרחבה)** ממוצע מילים לדף HTTP 200
- [ ] `AnalysisFactory.java` – `createStrategy(String name)` מחזיר מימוש או null

---

## שלב 5 – כתיבת הפלט (Output Writers) 📄

- [ ] `OutputWriter.java` – ממשק: `void write(CrawlResult result, String filePath)`
- [ ] `JsonOutputWriter.java` – Jackson ObjectMapper, pretty-print, מבנה `pages` + `analysis`
- [ ] `CsvOutputWriter.java` – **(הרחבה)** שורת header + שורה לכל דף + ניתוחים כ-key=value
- [ ] `OutputWriterFactory.java` – `createWriter(String format)` מחזיר json/csv, ברירת מחדל json

---

## שלב 6 – קריאת קלט (Input Readers) 📥

- [ ] `InputReader.java` – ממשק: `List<String> readSeeds()`
- [ ] `FileInputReader.java` – קריאת שורות מקובץ, דילוג על שורות ריקות ו-`#`

---

## שלב 7 – סינון דומיינים (הרחבה) 🔍

- [ ] `DomainFilter.java` – `isAllowed(String url, Set<String> allowed)`: אם הרשימה ריקה → הכל מותר, אחרת בדוק hostname

---

## שלב 8 – Observer 👁️

- [ ] `CrawlObserver.java` – ממשק: `void onPageCrawled(PageData page)`
- [ ] `CrawlSubject.java` – ממשק: `void addObserver(CrawlObserver o)`, `void notifyObservers(PageData page)`

---

## שלב 9 – שליפת דפים (Page Fetcher) 🌐

- [ ] `PageFetcher.java` – בניית חיבור Jsoup עם timeout=3000ms, followRedirects=true
- [ ] שליפה ושמירת קוד סטטוס HTTP
- [ ] חילוץ `<title>` (null אם לא HTTP 200)
- [ ] ספירת מילים מ-`doc.body().text()` (0 אם לא HTTP 200)
- [ ] חילוץ קישורים יוצאים `<a href>` כ-absolute URLs (0 אם לא HTTP 200)
- [ ] חילוץ hostname מה-URL
- [ ] טיפול בשגיאות: `MalformedURLException` → `<url> malformed` ל-stderr; `IOException` → `<url> failed` ל-stderr

---

## שלב 10 – ליבת הסורק (Crawler Core) ⚙️

- [ ] `WebCrawler.java` – שדות: `ExecutorService`, `ConcurrentHashMap<String,Integer> visited`, `AtomicInteger orderCounter`, `AtomicInteger pendingTasks`, `CountDownLatch`, `List<PageData> results` (synchronizedList)
- [ ] `WebCrawler.crawl()` – טעינת זרעים, הגשת `CrawlTask` ראשוניות (increment pendingTasks לפני כל submit)
- [ ] `WebCrawler.crawl()` – המתנה ל-`doneLatch.await()`
- [ ] `WebCrawler.crawl()` – `pool.shutdown()` לאחר סיום
- [ ] `CrawlTask.java` – בדיקה אטומית: `visited.putIfAbsent(url, order)`, אם כבר קיים – חזרה מיידית
- [ ] `CrawlTask` – קריאה ל-`PageFetcher.fetch()`
- [ ] `CrawlTask` – הכנסת `PageData` לרשימה **בסדר הגילוי** (לפי ה-order value)
- [ ] `CrawlTask` – אם depth < maxDepth: submit של child tasks לכל קישור (אחרי DomainFilter)
- [ ] `CrawlTask` – ב-finally: `pendingTasks.decrementAndGet()`, אם הגיע ל-0 → `doneLatch.countDown()`
- [ ] `CrawlTask` – קריאה ל-`subject.notifyObservers(pageData)` לאחר שמירה

---

## שלב 11 – Main 🚀

- [ ] `Main.java` – חיבור הכל: `CliParser` → `WebCrawler` → ניתוחים → `CrawlResult` → `OutputWriter`; טיפול ב-IOException עם `error saving report`

---

## שלב 12 – בדיקות והגשה ✅

- [ ] יצירת `seeds.txt` עם לפחות 5 URLs שיניבו 100+ דפים בעומק 2
- [ ] בדיקה עם `poolSize=1`
- [ ] בדיקה עם `poolSize=4`
- [ ] בדיקה עם `depth=0`
- [ ] בדיקה עם `depth=2`
- [ ] בדיקה עם URLs כפולים בקובץ הזרעים
- [ ] בדיקה עם מעגלים (cycles) – וידוא שלא נתקעים
- [ ] עדכון `README.md` עם מיילים אמיתיים של הסטודנטים
- [ ] יצירת git branch `feature/implementation`
- [ ] commits משמעותיים לאורך הפיתוח
- [ ] merge חזרה ל-`main` (לא למחוק את ה-branch!)

---

## סיכום התקדמות

| שלב | כמה משימות | הושלמו |
|-----|-----------|--------|
| 1 – Maven | 5 | 5 ✅ |
| 2 – מודל נתונים | 2 | 0 |
| 3 – CLI | 8 | 0 |
| 4 – ניתוחים | 8 | 0 |
| 5 – פלט | 4 | 0 |
| 6 – קלט | 2 | 0 |
| 7 – סינון | 1 | 0 |
| 8 – Observer | 2 | 0 |
| 9 – Page Fetcher | 7 | 0 |
| 10 – Crawler Core | 10 | 0 |
| 11 – Main | 1 | 0 |
| 12 – בדיקות | 9 | 0 |
| **סה"כ** | **59** | **0** |
