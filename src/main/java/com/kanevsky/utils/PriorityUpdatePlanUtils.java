package com.kanevsky.utils;

import com.kanevsky.config.UpdatePlan;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class PriorityUpdatePlanUtils {
    public static UpdatePlan getUpdatePlan(Map<String, String> currentResourcesToMD5, Map<String, String> latestFileToMD5) {
        Set<String> changedResources = new LinkedHashSet<>();
        Set<String> unchangedResources = new LinkedHashSet<>();
        currentResourcesToMD5.forEach((file, currentMD5) -> {
            final String oldMd5 = latestFileToMD5.get(file);
            if (Objects.equals(oldMd5, currentMD5)) {
                unchangedResources.add(file);
            } else {
                changedResources.add(file);
            }
        });
        return new UpdatePlan(changedResources, unchangedResources);
    }
}
