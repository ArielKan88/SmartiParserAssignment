package com.kanevsky.config;


import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;


@Component
public class JsonConfigIngestionHelper {
    private String prioritiesStr;
    private String entityTypeStr;

    @Value("${PRIORITIES_STR}")
    public void setPrioritiesStr(String prioritiesStr) {
        this.prioritiesStr = prioritiesStr;
    }

    @Value("${ENTITY_TYPE_STR}")
    public void setEntityTypeStr(String entityTypeStr) {
        this.entityTypeStr = entityTypeStr;
    }

    public boolean isValid(JsonNode rootNode) {
        return rootNode != null && rootNode.has(prioritiesStr) && rootNode.has(entityTypeStr);
    }

    public String extractEntityType(JsonNode rootNode) {
        return rootNode.get(entityTypeStr).textValue();
    }

    /**
     * Converts the given JSON node to a Map&lt;List&lt;String&gt;, List&lt;String&gt;&gt; structure.
     *
     * @param rootNode The JSON node to convert.
     * @return The resulting Map structure.
     */
    public Map<List<String>, List<String>> ingest(JsonNode rootNode) {
        JsonNode prioritiesNode = rootNode.get(prioritiesStr);
        Map<List<String>, List<String>> resultMap = new LinkedHashMap<>();
        iterateJsonNode(prioritiesNode, new LinkedList<>(), resultMap);
        return resultMap;
    }

    /**
     * Recursively iterates over a JSON node and processes each node based on its type.
     *
     * @param jsonNode        The current JSON node.
     * @param currentSegments The current path in the JSON structure.
     * @param resultMap       The map to store the result.
     */
    private void iterateJsonNode(JsonNode jsonNode, List<String> currentSegments, Map<List<String>, List<String>> resultMap) {
        if (jsonNode.isObject()) {
            iterateObjectNode(jsonNode, currentSegments, resultMap);
        } else if (jsonNode.isArray()) {
            iterateSourcesArrayNode(jsonNode, currentSegments, resultMap);
        }
    }

    /**
     * Iterates over an object node in the JSON structure.
     * for example: "address": {"city": "Rafah","region": "Palestine"}
     *
     * @param jsonNode    The object node to iterate.
     * @param currentPath The current path in the JSON structure.
     * @param resultMap   The map to store the result.
     */
    private void iterateObjectNode(JsonNode jsonNode, List<String> currentPath, Map<List<String>, List<String>> resultMap) {
        Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            LinkedList<String> segmentsForNextStep = new LinkedList<>(currentPath);
            segmentsForNextStep.add(field.getKey());
            iterateJsonNode(field.getValue(), segmentsForNextStep, resultMap);
        }
    }

    /**
     * Iterates over an array node in the JSON structure.
     * for example: ["webint", "c2"]
     *
     * @param jsonNode    The array node to iterate.
     * @param currentPath The current path in the JSON structure.
     * @param resultMap   The map to store the result.
     */
    private static void iterateSourcesArrayNode(JsonNode jsonNode, List<String> currentPath, Map<List<String>, List<String>> resultMap) {
        final Iterator<JsonNode> iterator = jsonNode.iterator();
        List<String> sources = new LinkedList<>();
        while (iterator.hasNext()) {
            sources.add(iterator.next().textValue());
        }

        if (!sources.isEmpty()) {
            resultMap.put(currentPath, sources);
        }
    }
}
