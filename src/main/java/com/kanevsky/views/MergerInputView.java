package com.kanevsky.views;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;

import java.util.Map;

public record MergerInputView(@Getter Map<String, JsonNode> inputs) {
}
