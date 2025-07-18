package com.github.bandurski.dto;

import java.util.List;

public record ParsedHighlights(List<Quote> quotes, String authorTitle) { }
