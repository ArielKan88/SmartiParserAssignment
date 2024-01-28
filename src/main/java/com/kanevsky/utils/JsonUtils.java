package com.kanevsky.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class JsonUtils {
    public static JsonNode getFieldValue(JsonNode currentNode, List<String> segments) {
        for (String segment : segments) {
            if (currentNode == null || !currentNode.has(segment)) {
                return null; //segment not found
            }
            currentNode = currentNode.get(segment);
        }
        return currentNode;
    }

    public static void setValueAtPath(ObjectNode rootNode, List<String> segments, JsonNode value) {
        // Traverse the path, creating nodes as needed
        for (int i = 0; i < segments.size() - 1; i++) {
            String segment = segments.get(i);

            if (!rootNode.has(segment) || !rootNode.get(segment).isObject()) {
                // If the node does not exist or is not an object, create a new object node
                rootNode.putObject(segment);
            }

            // Move to the next level in the hierarchy
            rootNode = (ObjectNode) rootNode.get(segment);
        }

        final String lastSegment = segments.get(segments.size() - 1);
        rootNode.set(lastSegment, value);
    }

    public static JsonNode readJsonFile(Resource resource) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        try (InputStream inputStream = resource.getInputStream()) {
            return objectMapper.readTree(inputStream);
        }
    }
}
