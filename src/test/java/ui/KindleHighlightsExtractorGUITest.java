package ui;

import com.github.bandurski.services.FileProcessingService;
import com.github.bandurski.ui.KindleHighlightsExtractorGUI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class KindleHighlightsExtractorGUITest {

    private KindleHighlightsExtractorGUI gui;
    private FileProcessingService mockService;

    @BeforeEach
    public void setUp() {
        mockService = mock(FileProcessingService.class);
        gui = new KindleHighlightsExtractorGUI() {
            @Override
            protected FileProcessingService createFileProcessingService() {
                return mockService;
            }
        };
    }

    @Test
    public void testProcessFileInBackground_shouldCallProcessFile() throws IOException, InterruptedException {
        File dummyFile = new File("dummy.txt");

        JFrame frame = new JFrame();
        JLabel statusLabel = new JLabel();
        JProgressBar progressBar = new JProgressBar();
        final JLabel loadingLabel = new JLabel();

        gui.processFileInBackground(dummyFile, frame, statusLabel, progressBar, loadingLabel);

        // wait a bit for the thread to start
        Thread.sleep(200);

        verify(mockService, times(1)).processFile(eq(dummyFile), eq(progressBar), eq(statusLabel));
    }

    @Test
    public void testShow_shouldRenderGui() {
        SwingUtilities.invokeLater(() -> {
            gui.show();
            // Just verifies that window shows up - no exception thrown
        });
    }
}
