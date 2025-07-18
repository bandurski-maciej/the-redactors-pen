package services;

import com.github.bandurski.configuration.ConfigLoader;
import com.github.bandurski.services.AIHelperService;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AIHelperServiceTest {

    private HttpClient httpClient;
    private AIHelperService aiHelperService;

    @BeforeEach
    void setUp() {
        httpClient = mock(HttpClient.class);
        aiHelperService = new AIHelperService(httpClient);
    }

    @Test
    void testAskAItoRedactQuotes_returnsRedactedText() throws Exception {
        String prompt = "Please redact this text.";

        // Mock API response JSON
        JSONObject mockResponseJson = new JSONObject()
                .put("choices", new org.json.JSONArray().put(
                        new JSONObject().put("message", new JSONObject().put("content", "Redacted response"))
                ));

        // Mock HttpResponse
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn(mockResponseJson.toString());

        // Mock send call
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        // Mock static ConfigLoader methods
        try (MockedStatic<ConfigLoader> mockedConfig = mockStatic(ConfigLoader.class)) {
            mockedConfig.when(ConfigLoader::getApiUrl).thenReturn("https://api.fake.com/v1/chat");
            mockedConfig.when(ConfigLoader::getApiToken).thenReturn("fake-api-key");
            mockedConfig.when(ConfigLoader::getModelName).thenReturn("gpt-mock-model");

            // Act
            String result = aiHelperService.askAItoRedactQuotes(prompt);

            // Assert
            assertEquals("Redacted response", result);
        }
    }

    @Test
    void testAskAItoRedactQuotes_handlesNon200Response() throws Exception {
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(400);
        when(mockResponse.body()).thenReturn("Bad request");

        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        try (MockedStatic<ConfigLoader> mockedConfig = mockStatic(ConfigLoader.class)) {
            mockedConfig.when(ConfigLoader::getApiUrl).thenReturn("https://api.fake.com/v1/chat");
            mockedConfig.when(ConfigLoader::getApiToken).thenReturn("fake-api-key");
            mockedConfig.when(ConfigLoader::getModelName).thenReturn("gpt-mock-model");

            String result = aiHelperService.askAItoRedactQuotes("test");
            assertEquals("API Error: 400 - Bad request", result);
        }
    }

    @Test
    void testAskAItoRedactQuotes_handlesExceptionGracefully() throws Exception {
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenThrow(new RuntimeException("Simulated failure"));

        try (MockedStatic<ConfigLoader> mockedConfig = mockStatic(ConfigLoader.class)) {
            mockedConfig.when(ConfigLoader::getApiUrl).thenReturn("https://api.fake.com/v1/chat");
            mockedConfig.when(ConfigLoader::getApiToken).thenReturn("fake-api-key");
            mockedConfig.when(ConfigLoader::getModelName).thenReturn("gpt-mock-model");

            String result = aiHelperService.askAItoRedactQuotes("test");

            assertTrue(result.startsWith("API Error:"));
            assertTrue(result.contains("Simulated failure"));
        }
    }
}
