# Lucene Cranfield Search Engine

An Information Retrieval system built with Apache Lucene that indexes and searches the [Cranfield collection](https://ir.dcs.gla.ac.uk/resources/test_collections/cran/) — a classic IR benchmark of 1400 aerodynamics research abstracts. The system evaluates retrieval performance across multiple analyzer and similarity model combinations using `trec_eval`.

## Project Structure

```
.
├── cran/
│   ├── cran.all.1400       # 1400 Cranfield documents
│   ├── cran.qry            # 225 queries
│   ├── cranqrel            # Relevance judgments (qrels)
│   └── cranqrel.readme     # Format description for qrels
├── trec_eval-9.0.7/        # TREC evaluation tool (pre-compiled)
├── src/main/java/com/informationretrieval/
│   ├── Starter.java                        # Main entry point
│   ├── config/
│   │   └── ConfigLoader.java               # Reads config.properties
│   ├── document/
│   │   ├── CranDocument.java               # Document model
│   │   └── CranDocumentParser.java         # Parses Cranfield document format
│   ├── query/
│   │   ├── CranQuery.java                  # Query model
│   │   └── QueryParser.java                # Parses Cranfield query format
│   ├── searchengine/
│   │   ├── CustomAnalyzer.java             # Custom Lucene analyzer pipeline
│   │   ├── Indexer.java                    # Builds Lucene index
│   │   └── Searcher.java                   # Runs queries, writes TREC results
│   ├── factory/
│   │   ├── AnalyzerFactory.java            # Creates analyzer instances
│   │   └── SimilarityFactory.java          # Creates similarity model instances
│   └── evaluate/
│       ├── EvalResult.java                 # Holds per-run evaluation metrics
│       └── Result.java                     # Runs trec_eval and shows results table
├── config.properties       # Configuration file
└── pom.xml
```

## Requirements

- Java 21+
- Maven 3.6+
- macOS/Linux (the included `trec_eval` binary is compiled for macOS ARM64; rebuild if needed — see below)

## Configuration

All runtime settings are controlled via `config.properties` in the project root:

| Property | Default | Description |
|---|---|---|
| `analyzers` | `standard` | Comma-separated list of analyzers to run |
| `similarities` | `bm25` | Comma-separated list of similarity models |
| `indexPath` | `./index` | Directory for the Lucene index |
| `cranDocumentsPath` | `./cran/cran.all.1400` | Path to document collection |
| `cranQueryPath` | `./cran/cran.qry` | Path to query file |
| `cranqrelFilePath` | `./cran/cranqrel` | Path to relevance judgments |
| `outputDir` | `./output` | Directory for TREC-format result files |
| `trecEvalPath` | `./trec_eval-9.0.7` | Path to trec_eval directory |
| `topKDocuments` | `50` | Number of top documents to retrieve per query |
| `trecEvalMetrics` | `map,P.5,iprec_at_recall.00` | Metrics to evaluate |

### Available Analyzers

| Name | Description |
|---|---|
| `standard` | Lucene StandardAnalyzer |
| `english` | EnglishAnalyzer with Porter stemming |
| `simple` | Splits on non-letters, lowercases |
| `whitespace` | Splits on whitespace only |
| `stop` | StandardAnalyzer + English stop words |
| `custom` | Custom pipeline: StandardTokenizer → LowerCase → EnglishPossessive → StopFilter → PorterStem → ASCIIFolding → LengthFilter(3–20) |

### Available Similarity Models

| Name | Description |
|---|---|
| `bm25` | BM25 (default Lucene ranking) |
| `classic` | Classic TF-IDF (Vector Space Model) |
| `boolean` | Boolean similarity (no scoring, binary match) |

## How to Run

### 1. Build

```bash
mvn clean package
```

### 2. Run from the project root

```bash
java -jar target/IRAssignment1-1.0-SNAPSHOT.jar
```

The program must be run from the project root directory so it can find `config.properties`.

### 3. What happens

For each analyzer × similarity combination configured in `config.properties`:

1. **Indexes** all 1400 Cranfield documents into a Lucene index
2. **Searches** all 225 queries using a multi-field query with field boosts:
   - Title: `3.0x`
   - Words (abstract): `1.5x`
   - Author: `0.1x`
   - Bibliography: `0.1x`
3. **Writes** results in TREC format to `output/<AnalyzerName>_<SimilarityName>.txt`
4. **Evaluates** each result file using `trec_eval` and displays a summary table

### 4. Example: run all combinations

Ensure `config.properties` has:
```properties
analyzers=standard,english,simple,whitespace,stop,custom
similarities=bm25,classic,boolean
trecEvalMetrics=map,P.5,P.10,iprec_at_recall.00
```

This produces 18 result files and a comparison table of all runs.

## Rebuilding trec_eval

If the included binary does not work on your system, rebuild it from source:

```bash
cd trec_eval-9.0.7
make
```

## Output Format

Result files follow the standard TREC format:

```
<query_id> 0 <doc_id> <rank> <score> STANDARD
```

Example:
```
1 0 486 1 29.837606 STANDARD
1 0 51  2 28.955748 STANDARD
```

## Dependencies

- [Apache Lucene 8.4](https://lucene.apache.org/) — indexing and search
- [Google AutoValue 1.11](https://github.com/google/auto/tree/main/value) — immutable model classes
- [Lombok](https://projectlombok.org/) — boilerplate reduction
- [trec_eval 9.0.7](https://trec.nist.gov/trec_eval/) — standard IR evaluation tool