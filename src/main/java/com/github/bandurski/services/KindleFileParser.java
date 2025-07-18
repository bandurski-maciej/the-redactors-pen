package com.github.bandurski.services;


import com.github.bandurski.dto.ParsedHighlights;
import com.github.bandurski.dto.Quote;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class KindleFileParser {

    public ParsedHighlights parse(final File inputFile) throws IOException {
        final List<String> lines = Files.readAllLines(inputFile.toPath());
        final List<Quote> allQuotes = new ArrayList<>();

        String currentAuthorTitle = null;
        final StringBuilder quoteBuilder = new StringBuilder();
        boolean isHighlight = false;

        for (final String line : lines) {
            if (line.startsWith("========")) {
                addQuote(isHighlight, quoteBuilder, allQuotes);
                quoteBuilder.setLength(0);
                isHighlight = false;
                continue;
            }

            if (line.contains("- Your Highlight")) {
                isHighlight = true;
                continue;
            } else if (line.contains("- Your Note")) {
                isHighlight = false;
                quoteBuilder.setLength(0);
                continue;
            }

            if (currentAuthorTitle == null && line.contains(" - ")) {
                currentAuthorTitle = line.trim();
            }

            appendLineIfHighlight(isHighlight, quoteBuilder, line);
        }

        addQuote(isHighlight, quoteBuilder, allQuotes);

        return new ParsedHighlights(allQuotes, currentAuthorTitle);
    }

    private boolean needsRedaction(final String text) {
        int spaceCount = 0;
        int charCount = 0;
        for (char c : text.toCharArray()) {
            if (c == ' ') spaceCount++;
            else if (!Character.isWhitespace(c)) charCount++;
        }
        if (charCount == 0) return false;
        double spaceRatio = (double) spaceCount / charCount;
        return spaceRatio > 0.2;
    }

    private void addQuote(final boolean isHighlight, final StringBuilder quoteBuilder, final List<Quote> allQuotes) {
        if (isHighlight && !quoteBuilder.isEmpty()) {
            String rawQuote = quoteBuilder.toString().trim();
            boolean needsRedact = needsRedaction(rawQuote);
            allQuotes.add(new Quote(rawQuote, needsRedact));
        }
    }

    private void appendLineIfHighlight(final boolean isHighlight, final StringBuilder quoteBuilder, final String line) {
        if (isHighlight) {
            quoteBuilder.append(line).append(" ");
        }
    }
}