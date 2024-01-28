package com.kanevsky.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.kanevsky.utils.MD5Utils;
import com.kanevsky.utils.PriorityUpdatePlanUtils;
import jakarta.annotation.PostConstruct;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.kanevsky.utils.JsonUtils.readJsonFile;

@Slf4j
@Component
@NoArgsConstructor
public class PrioritiesManager implements IPrioritiesManager {

    @Autowired
    private ResourceLoader resourceLoader;
    @Autowired
    private JsonConfigIngestionHelper ingestionHelper;
    @Value("#{'${merger.json.files}'.split(',')}")
    private List<String> mergerJsonFiles;
    @Value("${merger.json.reload_seconds:0}")
    private int reloadFrequency;

    private Map<String, Map<List<String>, List<String>>> entityTypeToPriorities = new HashMap<>();
    private Map<String, String> fileToMD5 = new HashMap<>();
    private Map<String, String> fileToEntityType = new HashMap<>();
    private ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1);


    @PostConstruct
    public void init() {
        reload();
        if (reloadFrequency > 0) {
            scheduledExecutorService.scheduleWithFixedDelay(this::reload, reloadFrequency, reloadFrequency, TimeUnit.SECONDS);
        }
    }

    public Map<List<String>, List<String>> getPriorities(String entityType) {
        return entityTypeToPriorities.get(entityType);
    }

    /**
     * Initializes the PrioritiesManager by calculating MD5 hashes of specified JSON files,
     * comparing them with the previously stored hashes, and updating priorities accordingly.
     * <p>
     * The initialization process involves checking for changes in JSON files' MD5 hashes,
     * processing the updated and unchanged resources, and updating the internal representation
     * of entity types to their corresponding priorities. The initialization is triggered either
     * at application startup or periodically based on the configured fixed delay or rate.
     * <p>
     * If an error occurs during the resource processing or MD5 calculation, appropriate error
     * messages are logged, providing information on the failure.
     */
    private void reload() {
        Map<String, String> currentFileToMD5 = calculateCurrentFileToMD5();
        UpdatePlan updatePlan = PriorityUpdatePlanUtils.getUpdatePlan(currentFileToMD5, fileToMD5);
        this.fileToMD5 = currentFileToMD5;

        UpdateResult ongoingUpdateResult = copyUnchangedResources(updatePlan.getUnchanged());
        try {
            ongoingUpdateResult = processChangedResources(updatePlan.getChanged(), ongoingUpdateResult);
            this.fileToEntityType = ongoingUpdateResult.calculatedFileToEntityType();
            this.entityTypeToPriorities = ongoingUpdateResult.calculatedEntityTypeToPriorities();
        } catch (IOException e) {
            log.error("Failed to read resource", e);
        }
    }

    private UpdateResult copyUnchangedResources(Collection<String> unchangedFiles) {
        Map<String, Map<List<String>, List<String>>> newEntityTypeToPriorities = new HashMap<>();
        final HashMap<String, String> newFileToEntityType = new HashMap<>();
        for (String file : unchangedFiles) {
            final String entityType = fileToEntityType.get(file);
            Map<List<String>, List<String>> priorities = entityTypeToPriorities.get(entityType);
            if (priorities != null) {
                newFileToEntityType.put(file, entityType);
                newEntityTypeToPriorities.put(entityType, priorities);
            }
        }
        return new UpdateResult(newEntityTypeToPriorities, newFileToEntityType);
    }

    private UpdateResult processChangedResources(Collection<String> changedFiles, UpdateResult ongoingUpdateResult) throws IOException {
        var currentEntityTypeToPriorities = ongoingUpdateResult.getCalculatedEntityTypeToPriorities();
        var currentFileToEntityType = ongoingUpdateResult.getCalculatedFileToEntityType();
        for (String file : changedFiles) {
            final Resource resource = resourceLoader.getResource(file);
            JsonNode rootNode = readJsonFile(resource);

            if (!ingestionHelper.isValid(rootNode)) {
                log.warn("Ignoring {}: unreadable or mandatory fields absent.", file);
                continue;
            }

            final String entityType = ingestionHelper.extractEntityType(rootNode);
            if (currentEntityTypeToPriorities.containsKey(entityType)) {
                log.warn("Ignoring {}: {} is already mapped.", file, entityType);
                continue;
            }

            currentFileToEntityType.put(file, entityType);
            var priorities = ingestionHelper.ingest(rootNode);
            currentEntityTypeToPriorities.put(entityType, priorities);
            log.info("Processed {} that describes {}.", file, entityType);
        }

        return new UpdateResult(currentEntityTypeToPriorities, currentFileToEntityType);
    }

    private Map<String, String> calculateCurrentFileToMD5() {
        Map<String, String> currentResourcesToMD5 = new HashMap<>();
        for (String file : mergerJsonFiles) {
            Resource resource = resourceLoader.getResource(file);
            if (resource.exists() && resource.isReadable()) {
                try {
                    final String hash = MD5Utils.calculateMD5(resource);
                    currentResourcesToMD5.put(file, hash);
                } catch (IOException e) {
                    log.warn("Failed to calculate md5 for file {}, skipping.", file);
                }
            } else {
                log.warn("File {} not readable or absent, skipping.", file);
            }
        }
        return currentResourcesToMD5;
    }
}