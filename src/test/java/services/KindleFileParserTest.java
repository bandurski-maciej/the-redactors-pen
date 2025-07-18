package services;

import com.github.bandurski.dto.ParsedHighlights;
import com.github.bandurski.dto.Quote;
import com.github.bandurski.services.KindleFileParser;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class KindleFileParserTest {

    private final KindleFileParser parser = new KindleFileParser();

    @Test
    void testParseSingleHighlight() throws IOException {
        File file = createTempFileWithContent(
                """
                        Book Title - Author Name
                        - Your Highlight on page 1 | location 10-11 | Added on Sunday, June 1, 2025 8:00:00 AM
                        This is a test quote that should be parsed.
                        ========
                        """
        );

        ParsedHighlights result = parser.parse(file);
        List<Quote> quotes = result.quotes();

        assertEquals(1, quotes.size());
        assertEquals("Book Title - Author Name", result.authorTitle());

        Quote q = quotes.get(0);
        assertEquals("This is a test quote that should be parsed.", q.getText());
        assertTrue(q.isNeedsRedaction());
    }

    @Test
    void testParseSkipsNote() throws IOException {
        File file = createTempFileWithContent(
                """
                        Some Book - Some Author
                        - Your Note on page 2 | location 15-16 | Added on Sunday
                        This is a note and should be skipped.
                        ========
                        """
        );

        ParsedHighlights result = parser.parse(file);
        assertEquals(0, result.quotes().size());
    }

    @Test
    void testMultipleQuotesParsedCorrectly() throws IOException {
        File file = createTempFileWithContent(
                """
                        Sample Book - John Doe
                        - Your Highlight on page 1 | location 1
                        First quote.
                        ========
                        - Your Highlight on page 2 | location 2
                        Second quote.
                        ========
                        """
        );

        ParsedHighlights result = parser.parse(file);
        List<Quote> quotes = result.quotes();

        assertEquals(2, quotes.size());
        assertEquals("First quote.", quotes.get(0).getText());
        assertEquals("Second quote.", quotes.get(1).getText());
    }

    @Test
    void testNeedsRedactionFalseWhenTextIsCompact() throws IOException {
        File file = createTempFileWithContent(
                """
                        Book - Author
                        - Your Highlight
                        Compacttextwithoutspaces.
                        ========
                        """
        );

        ParsedHighlights result = parser.parse(file);
        assertEquals(1, result.quotes().size());
        assertFalse(result.quotes().get(0).isNeedsRedaction());
    }

    @Test
    void testThrowsIOExceptionOnMissingFile() {
        File file = new File("non_existent_file.txt");

        assertThrows(IOException.class, () -> parser.parse(file));
    }

    // Helper: create a temporary file with given content
    private File createTempFileWithContent(String content) throws IOException {
        File tempFile = File.createTempFile("kindle_test_", ".txt");
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write(content);
        }
        tempFile.deleteOnExit();
        return tempFile;
    }
}
