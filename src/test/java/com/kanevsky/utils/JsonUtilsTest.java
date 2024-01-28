package com.kanevsky.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class JsonUtilsTest {
    @Test
    void testGetFieldValue_ExistingField() throws Exception {
        String jsonString = "{\"person\":{\"name\":\"John\",\"age\":30}}";
        JsonNode rootNode = new ObjectMapper().readTree(jsonString);
        List<String> segments = List.of("person", "name");
        JsonNode result = JsonUtils.getFieldValue(rootNode, segments);
        assertEquals("John", result.asText());
    }

    @Test
    void testGetFieldValue_NonExistingField() throws Exception {
        String jsonString = "{\"person\":{\"name\":\"John\"}}";
        JsonNode rootNode = new ObjectMapper().readTree(jsonString);
        List<String> segments = List.of("person", "address");
        JsonNode result = JsonUtils.getFieldValue(rootNode, segments);
        assertNull(result);
    }

    @Test
    void testGetFieldValue_NullNode() {
        List<String> segments = List.of("person", "name");
        JsonNode result = JsonUtils.getFieldValue(null, segments);
        assertNull(result);
    }

    @Test
    void testSetValueAtPath_ExistingPath() throws Exception {
        String jsonString = "{\"person\":{\"name\":\"John\"}}";
        ObjectNode rootNode = (ObjectNode) new ObjectMapper().readTree(jsonString);
        List<String> segments = List.of("person", "age");
        JsonNode value = new ObjectMapper().valueToTree(30);
        JsonUtils.setValueAtPath(rootNode, segments, value);
        assertEquals(30, rootNode.get("person").get("age").asInt());
    }

    @Test
    void testSetValueAtPath_NonExistingPath() throws Exception {
        String jsonString = "{\"person\":{\"name\":\"John\"}}";
        ObjectNode rootNode = (ObjectNode) new ObjectMapper().readTree(jsonString);
        List<String> segments = List.of("person", "address", "city");
        JsonNode value = new ObjectMapper().valueToTree("New York");
        JsonUtils.setValueAtPath(rootNode, segments, value);
        assertEquals("New York", rootNode.get("person").get("address").get("city").asText());
    }
}