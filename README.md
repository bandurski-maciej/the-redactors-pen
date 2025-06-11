# Kindle Highlights Extractor

📚 A simple Java Swing application for processing Kindle `.txt` highlight files.

The app detects poorly formatted book highlights (e.g., excessive spacing), sends them in batches to an AI model for
cleaning, and writes the corrected quotes to a new file.

---

## ✨ Features

- 📂 GUI file picker (starts on Desktop)
- 🔍 Automatic detection of "broken" quotes (e.g., weird spacing)
- 🤖 Batch processing using Hugging Face inference API
- 💬 Customizable prompt to improve quotes without changing their meaning
- 📊 Progress bar + logging of each API call
- 🧠 Smart quote reinsertion – corrected quotes are saved in the same order and position as in the original file

---

## 📸 Example

### Input

```

T h i s   i s   a   s a m p l e   q u o t e .

```

### Output

```

This is a sample quote.

````

---

## 🧪 Prerequisites

- Java 17+
- Internet connection (for API calls)
- Valid Hugging Face API token (free tier available)

---

## 🚀 Getting Started

### 1. Clone the repo

```bash
git clone https://github.com/yourusername/kindle-highlights-extractor.git
cd kindle-highlights-extractor
````

### 2. Add your Hugging Face token

Open `application.conf` and fill this line:

```
API_TOKEN=
```

You can get your token from: [https://huggingface.co/settings/tokens](https://huggingface.co/settings/tokens)

### 3. Add required dependencies

This project uses `org.json` for JSON parsing and `dotenv-java`

If using Maven, add this to your `pom.xml`:

```xml

<dependencies>
    <dependency>
        <groupId>org.json</groupId>
        <artifactId>json</artifactId>
        <version>20240303</version>
    </dependency>

    <dependency>
        <groupId>org.json</groupId>
        <artifactId>json</artifactId>
        <version>20240303</version>
    </dependency>
</dependencies>
```

Or manually download the jar from [Maven Central](https://search.maven.org/artifact/org.json/json).

### 4. Run the app

You can run the app directly from your IDE (e.g. IntelliJ) or via command line:

```bash
javac KindleHighlightsExtractorGUI.java
java KindleHighlightsExtractorGUI
```

---

## ⚙️ Configuration

You can modify:

* The model: `deepseek/deepseek-v3-0324` (default) in `application.conf` file
* The separator between quotes (`---`)
* The heuristics for detecting malformed quotes

---

## 📁 Output

The program saves corrected quotes to a new `.txt` file, named after the original book title + author (extracted from
the Kindle file). The new file is saved in the same directory as the input file.

---

## 🧠 Prompt Used

```
Here is a list of book quotes. Each quote is separated by the delimiter: '---'.
Please improve each quote to make it more readable — fix spacing issues, typos, punctuation, and formatting.
Do not change the meaning of the quotes. Keep the exact separation and formatting, and do not add any comments, titles, or extra text.
Only return the corrected quotes in the exact same format, using the same '---' delimiter between them.
```

---

## 🛡️ License

This project is licensed under the MIT License. See [LICENSE](https://www.mit.edu/~amini/LICENSE.md) for details.

---

## 🙋‍♂️ Author

Created by [Maciej Bandurski](https://github.com/bandurskim)

---

## 💡 Future Ideas

* Export to Markdown or PDF
* Language detection and support for multilingual books
* Integration with ChatGPT or Claude APIs
* Drag & drop support for file selection