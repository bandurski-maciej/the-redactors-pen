package com.github.bandurski.dto;

// Helper class to store quotes
public class Quote {
    private String text;
    private boolean needsRedaction;
    private String redactedText;

    public Quote(String text, boolean needsRedaction) {
        this.text = text;
        this.needsRedaction = needsRedaction;
        this.redactedText = null;
    }

    public Quote() {
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isNeedsRedaction() {
        return needsRedaction;
    }

    public void setNeedsRedaction(boolean needsRedaction) {
        this.needsRedaction = needsRedaction;
    }

    public String getRedactedText() {
        return redactedText;
    }

    public void setRedactedText(String redactedText) {
        this.redactedText = redactedText;
    }
}
