# EX1 – Final Implementation Plan (sourced from ex1.pdf)

> This file summarizes all confirmed requirements from the official PDF.
> See the implementation plan in `.cursor/plans/` for granular task breakdown.



## What Are We Building?

We are building a concurrent Java web crawler that:

- Starts from seed URLs in an input file
- Crawls pages up to a maximum depth
- Uses multiple threads through a thread pool
- Extracts page data
- Runs requested analyses
- Writes the result as JSON to an output file

## Main Idea

This assignment is not sequential.

Instead of:

`URL1 -> URL2 -> URL3`

we should have multiple worker threads processing pages at the same time.

## Basic Crawl Flow

1. Read seed URLs from the input file.
2. Put them into the crawl process with depth `0`.
3. Fetch a page.
4. Extract its data and outgoing links.
5. If depth allows, enqueue new links.
6. Avoid visiting the same URL twice.
7. Continue until the crawl is complete.

## Threading / Concurrency Requirements

The PDF clearly emphasizes concurrency quality:

- Use a thread pool
- Keep shared state thread-safe
- Avoid race conditions
- Avoid unnecessary global locking
- Do not busy-wait
- Do not fall back to a sequential implementation

The critical shared structures will likely include:

- Visited URLs
- Pending work queue
- Collected page results

## CLI Contract

The readable PDF lines show this expected interface:

```bash
java Main --analysis <strategies> --poolsize <n> --depth <d> --input <file> --output <file>
```

Confirmed flags:

- `--analysis <strategies>`
  - Required
  - Comma-separated list
  - Confirmed values in the PDF include `WORD_COUNT` and `BROKEN_LINKS`
- `--poolsize <n>`
  - Required
  - Must be greater than `0`
- `--depth <d>`
  - Max crawl depth
  - Validation examples show it must not be negative
- `--input <file>`
  - Required
  - Seed URL file
- `--output <file>`
  - Required
  - JSON report destination

Optional flags visible in the PDF:

- `--format json`
  - Optional
  - JSON appears to be the default
- `--domains <d1,d2,...>`
  - Optional
  - Hostname whitelist

## What To Extract From Each Page

The PDF clearly shows these page fields:

- `url`
- `title`
- `wordCount`
- `outgoingLinks`
- `domain`
- `status`
- `depth`

So each crawled page should be represented with those fields in the output.

## JSON Output

The output must be a single JSON object with two top-level keys:

- `pages`
- `analysis`

`pages`:

- Array of page objects
- Stored in first-discovery order

`analysis`:

- Object containing only the analyses requested by the user

Important PDF detail:

- Pages with non-`200` status codes should still appear in the `pages` array
- For such pages, `title`, `wordCount`, and `outgoingLinks` may be `null` or empty

If `BROKEN_LINKS` is requested and no broken links exist, the PDF explicitly shows:

```json
"BROKEN_LINKS": []
```

## Analyses

Confirmed from the PDF:

- `WORD_COUNT`
- `BROKEN_LINKS`

The current explanation file previously listed:

- `MOST_LINKED_DOMAIN`
- `KEYWORD_FREQUENCY`

These are plausible, but I could not confirm them with high confidence from the PDF extraction, so they should be treated as unverified until we inspect the original PDF visually or implement only what is explicitly required.

## Validation / Error Cases Seen In The PDF

The readable parts of the PDF clearly mention checks for:

- Invalid analysis name
- No valid analysis left after filtering
- Invalid pool size
- Invalid depth
- Input file failure
- Output file creation failure
- Unreachable URL / fetch failure

Visible examples include messages equivalent to:

- `Invalid pool size`
- `Invalid depth`
- `No valid analysis`

The exact wording should still be verified if strict output matching is required.

## Design Expectations

The assignment also mentions design patterns. The readable list includes:

- Strategy
- Factory
- Observer
- Singleton
- Chain of Responsibility
- Decorator
- Visitor
- Template Method
- Builder

That does not necessarily mean all of them must be used, but the README should list the patterns actually used in the solution.

## README / Submission Requirements

The PDF clearly requires the README to include:

- Student emails
- Exact compile instructions
- Exact run instructions
- List of design patterns used
- List of implemented extensions

It also mentions Git requirements:

- Meaningful commit history
- At least one branch/merge

## Comparison Against The Earlier Explanation

The earlier `webcrawler_explanation.md` got these parts right:

- It correctly describes the crawler at a high level
- It correctly stresses concurrency and race conditions
- It correctly explains depth conceptually
- It correctly notes the use of a thread-safe visited structure

But it missed or understated:

- Full CLI requirements
- Required input/output file flags
- JSON schema details
- Non-`200` page handling
- Validation/error cases
- Optional flags like `--format` and `--domains`
- README/submission requirements
- Exact confirmed page fields in output

It also included unverified analysis names that are not confirmed by the extracted PDF text.

## Bottom Line

The assignment is broader than just "make a threaded crawler".

We need to deliver:

- A concurrent crawler
- Safe shared-state coordination
- CLI parsing and validation
- Structured JSON output
- Requested analyses
- Proper README and submission details
