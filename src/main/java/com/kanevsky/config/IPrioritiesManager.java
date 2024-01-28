package com.kanevsky.config;

import java.util.List;
import java.util.Map;

public interface IPrioritiesManager {
    void init();

    Map<List<String>, List<String>> getPriorities(String entityType);
}
