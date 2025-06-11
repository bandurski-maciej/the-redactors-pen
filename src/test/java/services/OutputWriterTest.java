package services;

import com.github.bandurski.dto.Quote;
import com.github.bandurski.services.OutputWriter;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class OutputWriterTest {

    private OutputWriter writer;

    @BeforeEach
    void setup() {
        writer = new OutputWriter();
    }

    @Test
    void testWriteToFile_callsFilesWriteWithCorrectArgs() throws Exception {
        List<Quote> quotes = List.of(new Quote("text", false));
        String authorTitle = "author - title";
        Path destDir = Path.of("someDir");

        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            OutputWriter outputWriter = new OutputWriter();

            outputWriter.writeToFile(quotes, authorTitle, destDir);

            mockedFiles.verify(() ->
                    Files.write(
                            eq(destDir.resolve("author - title.txt")),
                            any(List.class)
                    )
            );
        }
    }

    @Test
    void testWriteToFile_doesNotWriteWhenAuthorTitleNull() throws Exception {
        Path mockPath = Path.of("/fake/path");
        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            writer.writeToFile(List.of(), null, mockPath);
            // Should never call Files.write if authorTitle is null
            mockedFiles.verifyNoInteractions();
        }
    }
}
