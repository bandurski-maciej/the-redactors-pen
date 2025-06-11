package com.github.bandurski.ui;

import com.github.bandurski.services.FileProcessingService;
import com.github.bandurski.services.KindleFileParser;
import com.github.bandurski.services.OutputWriter;
import com.github.bandurski.services.QuoteRedactor;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

public class KindleHighlightsExtractorGUI {

    private final FileProcessingService fileProcessingService;

    public KindleHighlightsExtractorGUI() {
        fileProcessingService = createFileProcessingService();
    }

    protected FileProcessingService createFileProcessingService() {
        return new FileProcessingService(new KindleFileParser(), new QuoteRedactor(), new OutputWriter());
    }

    // Launches the GUI window
    public void show() {
        final JFrame frame = new JFrame("Kindle Highlights Extractor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 250);

        final JPanel panel = new JPanel(new BorderLayout());
        final JButton chooseFileButton = new JButton("Select .txt file from Kindle");
        final JLabel statusLabel = new JLabel("Select a file to process...");
        final JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);

        final JLabel loadingLabel = new JLabel();
        loadingLabel.setVisible(false);

        final JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerPanel.add(progressBar);
        centerPanel.add(loadingLabel);

        setupChooseFileAction(chooseFileButton, frame, statusLabel, progressBar, loadingLabel);

        panel.add(chooseFileButton, BorderLayout.NORTH);
        panel.add(progressBar, BorderLayout.CENTER);
        panel.add(statusLabel, BorderLayout.SOUTH);

        frame.add(panel);
        frame.setLocationRelativeTo(null); // Center the window on the screen
        frame.setVisible(true);
    }

    // Adds the action listener to the "Choose File" button
    private void setupChooseFileAction(final JButton chooseFileButton, final JFrame frame, final JLabel statusLabel,
                                       final JProgressBar progressBar, final JLabel loadingLabel) {
        chooseFileButton.addActionListener((ActionEvent e) -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(FileSystemView.getFileSystemView().getHomeDirectory());
            int result = fileChooser.showOpenDialog(frame);

            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                processFileInBackground(selectedFile, frame, statusLabel, progressBar, loadingLabel);
            }
        });
    }


    // Processes the file in a background thread
    public void processFileInBackground(final File file, final JFrame frame, final JLabel statusLabel,
                                        final JProgressBar progressBar, final JLabel loadingLabel) {
        new Thread(() -> {
            try {
                SwingUtilities.invokeLater(() -> {
                    progressBar.setIndeterminate(true);
                    loadingLabel.setVisible(true);
                    statusLabel.setText("Processing file...");
                });

                fileProcessingService.processFile(file, progressBar, statusLabel);

                SwingUtilities.invokeLater(() -> {
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(100);
                    loadingLabel.setVisible(false);
                    statusLabel.setText("Highlights saved from file: " + file.getName());
                    JOptionPane.showMessageDialog(frame, "Highlights were successfully saved!", "Success", JOptionPane.INFORMATION_MESSAGE);
                });

            } catch (IOException ex) {
                ex.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    progressBar.setIndeterminate(false);
                    loadingLabel.setVisible(false);
                    statusLabel.setText("Error while processing the file.");
                    JOptionPane.showMessageDialog(frame, "An error occurred while processing the file.", "Error", JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();
    }

}
