package services;

import com.github.bandurski.constants.Constants;
import com.github.bandurski.dto.Quote;
import com.github.bandurski.services.AIHelperService;
import com.github.bandurski.services.QuoteRedactor;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class QuoteRedactorTest {

    @Test
    void testRedactQuotes_setsRedactedTextAndReportsProgress() {
        // Arrange
        AIHelperService aiHelperMock = mock(AIHelperService.class);
        QuoteRedactor quoteRedactor = new QuoteRedactor(aiHelperMock);

        // Prepare quotes - some need redaction, some don't
        Quote q1 = new Quote();
        q1.setText("Hello");
        q1.setNeedsRedaction(true);

        Quote q2 = new Quote();
        q2.setText("World");
        q2.setNeedsRedaction(true);

        Quote q3 = new Quote();
        q3.setText("NoRedact");
        q3.setNeedsRedaction(false);

        List<Quote> quotes = List.of(q1, q2, q3);

        // Mock AI response for redaction
        // AI returns redacted texts separated by line separator
        String redactedResponse = "Hi " + Constants.LINE_SEPARATOR + " Earth";

        when(aiHelperMock.askAItoRedactQuotes(anyString())).thenReturn(redactedResponse);

        // Capture progress callback invocations
        Consumer<Integer> progressConsumer = mock(Consumer.class);

        // Act
        quoteRedactor.redactQuotes(quotes, progressConsumer);

        // Assert
        // Redacted texts set correctly
        assertEquals("Hi", q1.getRedactedText());
        assertEquals("Earth", q2.getRedactedText());
        assertNull(q3.getRedactedText());

        // Progress called at least once with 100 at end (if batches > 0)
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        verify(progressConsumer, atLeastOnce()).accept(captor.capture());

        List<Integer> progressValues = captor.getAllValues();
        assertTrue(progressValues.stream().anyMatch(p -> p == 100));

        // Verify AIHelperService called once with prompt + quotes joined by line separator
        verify(aiHelperMock, times(1)).askAItoRedactQuotes(contains("Hello" + Constants.LINE_SEPARATOR + "World"));
    }
}
