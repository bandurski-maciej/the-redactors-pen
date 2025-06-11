package com.github.bandurski;

import com.github.bandurski.ui.KindleHighlightsExtractorGUI;

import javax.swing.*;

// Application main entry point. Invokes GUI component.
public class Main {
    public static void main(String[] args) {
        final KindleHighlightsExtractorGUI extractorGUI = new KindleHighlightsExtractorGUI();
        SwingUtilities.invokeLater(extractorGUI::show);
    }
}