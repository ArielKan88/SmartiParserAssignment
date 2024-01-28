package com.kanevsky.config;

import lombok.Getter;

import java.util.List;
import java.util.Map;

public record UpdateResult(@Getter Map<String, Map<List<String>, List<String>>> calculatedEntityTypeToPriorities,
                           @Getter Map<String, String> calculatedFileToEntityType) {
}
