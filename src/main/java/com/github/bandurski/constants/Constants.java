package com.github.bandurski.constants;

public class Constants {

    public static final int MAX_CHARS_PER_REQUEST = 100_000;

    public static final String LINE_SEPARATOR = "\n---\n";

    public static final String RESULT_LINE_SEPARATOR = "\n------------------------------\n";

    public static final String ILLEGAL_CHARACTERS_REGEX = "[/:*?\"<>|]";

    public static final String PROMPT = """
            Here is a list of book quotes. Each quote is separated by the delimiter: '---' (three dashes).
            Please improve each quote to make it more readable — fix spacing issues, typos, punctuation, and formatting.
            Do not change the meaning of the quotes. Keep the exact separation and formatting, and do not add any comments, titles, or extra text.
            Only return the corrected quotes in the exact same format, using the same '---' delimiter between them.

            Your response will be saved directly to a file by a program, so it is important to follow this format exactly — just with corrected quote content.

            Example:

            Input:
            ---
            T h i s   i s   a   q u o t e   w i t h   b a d   s p a c i n g .
            ---
            T h i s   t e x t   i s   a l s o   h a r d   t o   r e a d .

            Expected output:
            ---
            This is a quote with bad spacing.
            ---
            This text is also hard to read.

            Now do the same for the following quotes:
            """;


    private Constants() {
    }
}
