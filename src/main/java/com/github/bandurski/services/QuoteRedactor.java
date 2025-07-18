package com.github.bandurski.services;

import com.github.bandurski.constants.Constants;
import com.github.bandurski.dto.Quote;

import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class QuoteRedactor {

    private final AIHelperService aiHelperService;

    public QuoteRedactor() {
        aiHelperService = new AIHelperService(HttpClient.newHttpClient());
    }

    public QuoteRedactor(AIHelperService aiHelperService) {
        this.aiHelperService = aiHelperService;
    }

    public void redactQuotes(final List<Quote> quotes, final Consumer<Integer> onProgress) {
        final List<Quote> toRedact = quotes.stream().filter(Quote::isNeedsRedaction).collect(Collectors.toList());
        final List<List<Quote>> batches = splitByCharLimitQuotes(toRedact);

        for (int i = 0; i < batches.size(); i++) {
            final List<Quote> batch = batches.get(i);

            final String response = aiHelperService.askAItoRedactQuotes(Constants.PROMPT + joinQuotes(batch));
            setRedactedQuotes(response, batch);

            onProgress.accept(calculateProgress(i, batches));
        }
    }

    private List<List<Quote>> splitByCharLimitQuotes(final List<Quote> quotes) {
        final List<List<Quote>> batches = new ArrayList<>();
        List<Quote> currentBatch = new ArrayList<>();
        int currentCount = 0;

        for (final Quote q : quotes) {
            int length = q.getText().length();
            if (currentCount + length > Constants.MAX_CHARS_PER_REQUEST && !currentBatch.isEmpty()) {
                batches.add(currentBatch);
                currentBatch = new ArrayList<>();
                currentCount = 0;
            }
            currentBatch.add(q);
            currentCount += length;
        }
        if (!currentBatch.isEmpty()) batches.add(currentBatch);

        return batches;
    }

    private String joinQuotes(final List<Quote> batch) {
        return batch.stream().map(Quote::getText).collect(Collectors.joining(Constants.LINE_SEPARATOR));
    }

    private void setRedactedQuotes(final String response, final List<Quote> batch) {
        final String[] splitRedacted = response.split(Constants.LINE_SEPARATOR);

        for (int j = 0; j < splitRedacted.length && j < batch.size(); j++) {
            batch.get(j).setRedactedText(splitRedacted[j].trim());
        }
    }

    private int calculateProgress(int i, List<List<Quote>> batches) {
        return (int) (((i + 1) / (double) batches.size()) * 100);
    }
}