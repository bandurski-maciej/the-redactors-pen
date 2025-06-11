package services;

import com.github.bandurski.dto.ParsedHighlights;
import com.github.bandurski.dto.Quote;
import com.github.bandurski.services.FileProcessingService;
import com.github.bandurski.services.KindleFileParser;
import com.github.bandurski.services.OutputWriter;
import com.github.bandurski.services.QuoteRedactor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class FileProcessingServiceTest {

    private KindleFileParser parserMock;
    private QuoteRedactor redactorMock;
    private OutputWriter writerMock;

    private FileProcessingService service;

    private final JLabel statusLabel = new JLabel();
    private final JProgressBar progressBar = new JProgressBar();

    @BeforeEach
    void setUp() {
        parserMock = mock(KindleFileParser.class);
        redactorMock = mock(QuoteRedactor.class);
        writerMock = mock(OutputWriter.class);

        service = new FileProcessingService(parserMock, redactorMock, writerMock);
    }

    @Test
    void processFile_shouldCallAllComponentsAndUpdateStatus() throws IOException {
        // Arrange
        File dummyFile = new File("test_file.txt");
        List<Quote> quotes = List.of(new Quote("Quote 1", false), new Quote("Quote 2", false));
        String authorTitle = "Author - Title";

        ParsedHighlights parsedHighlights = new ParsedHighlights(quotes, authorTitle);

        when(parserMock.parse(dummyFile)).thenReturn(parsedHighlights);

        // Act
        service.processFile(dummyFile, progressBar, statusLabel);

        // Assert
        verify(parserMock).parse(dummyFile);
        verify(redactorMock).redactQuotes(eq(quotes), any());
        verify(writerMock).writeToFile(eq(quotes), eq(authorTitle), eq(dummyFile.toPath().getParent()));

        assertEquals("Saved redacted quotes for: test_file.txt", statusLabel.getText());
    }

    @Test
    void processFile_shouldSetInitialStatusWithQuoteCount() throws IOException {
        File dummyFile = new File("dummy.txt");
        List<Quote> quotes = List.of(new Quote("Quote 1", false), new Quote("Quote 2", false));
        ParsedHighlights parsedHighlights = new ParsedHighlights(quotes, "Some Author");

        when(parserMock.parse(dummyFile)).thenReturn(parsedHighlights);

        service.processFile(dummyFile, progressBar, statusLabel);

        assertEquals("Saved redacted quotes for: dummy.txt", statusLabel.getText());
    }

    @Test
    void processFile_shouldThrowIOException_ifParserFails() throws IOException {
        File file = new File("bad.txt");

        when(parserMock.parse(file)).thenThrow(new IOException("Parsing failed"));

        try {
            service.processFile(file, progressBar, statusLabel);
        } catch (IOException ex) {
            assertEquals("Parsing failed", ex.getMessage());
        }
    }
}
