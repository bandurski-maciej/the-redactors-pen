package com.github.bandurski.services;

import com.github.bandurski.configuration.ConfigLoader;
import org.json.*;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class AIHelperService {

    private final HttpClient httpClient;

    public AIHelperService(final HttpClient client) {
        this.httpClient = client;
    }

    public String askAItoRedactQuotes(final String prompt) {
        try {
            logPromptPreview(prompt);

            final JSONObject requestBody = buildRequestBody(prompt);
            final HttpRequest request = buildHttpRequest(requestBody);
            final HttpResponse<String> response = sendRequest(request);

            return handleResponse(response);

        } catch (Exception e) {
            e.printStackTrace();
            return "API Error: " + e.getMessage();
        }
    }

    private void logPromptPreview(final String prompt) {
        System.out.println("API Call: " + prompt.substring(0, Math.min(100, prompt.length())) + "...");
    }

    private JSONObject buildRequestBody(final String prompt) {
        final JSONObject message = new JSONObject();
        message.put("role", "user");
        message.put("content", prompt);

        final JSONArray messages = new JSONArray();
        messages.put(message);

        final String modelName = ConfigLoader.getModelName();
        final JSONObject jsonBody = new JSONObject();
        jsonBody.put("model", modelName);
        jsonBody.put("messages", messages);

        return jsonBody;
    }

    private HttpRequest buildHttpRequest(final JSONObject jsonBody) {
        final String apiKey = ConfigLoader.getApiToken();

        return HttpRequest.newBuilder()
                .uri(URI.create(ConfigLoader.getApiUrl()))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody.toString(), StandardCharsets.UTF_8))
                .build();
    }

    private HttpResponse<String> sendRequest(final HttpRequest request) throws Exception {
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
    }

    private String handleResponse(HttpResponse<String> response) {
        if (response.statusCode() != 200) {
            return "API Error: " + response.statusCode() + " - " + response.body();
        }

        final JSONObject responseJson = new JSONObject(response.body());
        final JSONArray choices = responseJson.getJSONArray("choices");
        final JSONObject firstChoice = choices.getJSONObject(0);
        final JSONObject messageContent = firstChoice.getJSONObject("message");
        final String content = messageContent.getString("content");

        System.out.println("Received API response.");
        return content;
    }

}
