package com.kanevsky.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kanevsky.config.IPrioritiesManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.kanevsky.utils.JsonUtils.getFieldValue;
import static com.kanevsky.utils.JsonUtils.setValueAtPath;

@Service
public class MergerService {

    private IPrioritiesManager prioritiesManager;

    private String entityTypeStr;

    public MergerService(@Autowired IPrioritiesManager prioritiesManager,
                         @Value("${ENTITY_TYPE_STR}") String entityTypeStr) {
        this.prioritiesManager = prioritiesManager;
        this.entityTypeStr = entityTypeStr;
    }

    private String getAnyEntityType(Map<String, JsonNode> inputs) {
        return inputs.values().iterator().next().get(entityTypeStr).textValue();
    }

    public ObjectNode merge(Map<String, JsonNode> inputs) {
        ObjectNode result = new ObjectNode(new JsonNodeFactory(true));

        final String entityType = getAnyEntityType(inputs);
        Map<List<String>, List<String>> priorities = prioritiesManager.getPriorities(entityType);
        if (priorities != null) {
            for (Map.Entry<List<String>, List<String>> entry : priorities.entrySet()) {
                final List<String> segments = entry.getKey();
                final List<String> sources = entry.getValue();

                for (String source : sources) {
                    JsonNode sourceJson = inputs.get(source);
                    if (sourceJson != null) {
                        final JsonNode value = getFieldValue(sourceJson, segments);
                        if (value != null) {
                            setValueAtPath(result, segments, value);
                            break;
                        }
                    }
                }
            }
        }

        return result;
    }
}
