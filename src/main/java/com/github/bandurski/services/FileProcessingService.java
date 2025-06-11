package com.github.bandurski.services;

import com.github.bandurski.dto.ParsedHighlights;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class FileProcessingService {

    private final KindleFileParser parser;
    private final QuoteRedactor redactor;
    private final OutputWriter writer;

    public FileProcessingService(final KindleFileParser parser, final QuoteRedactor redactor, final OutputWriter writer) {
        this.parser = parser;
        this.redactor = redactor;
        this.writer = writer;
    }
    
    public void processFile(final File inputFile, final JProgressBar progressBar, final JLabel statusLabel) throws IOException {
        final ParsedHighlights parsed = parser.parse(inputFile);
        statusLabel.setText("Loaded " + parsed.quotes().size() + " highlights");

        redactor.redactQuotes(parsed.quotes(), percent -> SwingUtilities.invokeLater(() -> progressBar.setValue(percent)));

        writer.writeToFile(parsed.quotes(), parsed.authorTitle(), inputFile.toPath().getParent());
        statusLabel.setText("Saved redacted quotes for: " + inputFile.getName());
    }
}