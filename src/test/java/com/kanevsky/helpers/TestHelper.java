package com.kanevsky.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestHelper {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static <T> T loadJsonFromFile(String fileName, Class<T> targetType) throws IOException, URISyntaxException {
        // Get the file path using the class loader
        ClassLoader classLoader = TestHelper.class.getClassLoader();
        var filePath = Paths.get(classLoader.getResource(fileName).toURI());

        // Read the content of the file into a String
        String jsonContent = new String(Files.readAllBytes(filePath), StandardCharsets.UTF_8);

        // Parse the String into an instance of the target class
        return objectMapper.readValue(jsonContent, targetType);
    }
}