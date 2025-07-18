package com.github.bandurski.services;

import com.github.bandurski.constants.Constants;
import com.github.bandurski.dto.Quote;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class OutputWriter {

    public void writeToFile(final List<Quote> quotes, final String authorTitle, final Path destinationDir) throws IOException {
        if (authorTitle != null) {
            final Path outputPath = destinationDir.resolve(getTitle(authorTitle) + ".txt");
            Files.write(outputPath, getQuotes(quotes));
        }
    }

    private String getTitle(final String authorTitle) {
        return authorTitle.replaceAll(Constants.ILLEGAL_CHARACTERS_REGEX, "_");
    }

    private List<String> getQuotes(List<Quote> quotes) {
        final List<String> finalOutput = new ArrayList<>();
        for (final Quote q : quotes) {
            finalOutput.add(getQuoteText(q));
            finalOutput.add(Constants.RESULT_LINE_SEPARATOR);
        }
        return finalOutput;
    }

    private String getQuoteText(final Quote q) {
        return q.isNeedsRedaction() && q.getRedactedText() != null ? q.getRedactedText() : q.getText();
    }
}
