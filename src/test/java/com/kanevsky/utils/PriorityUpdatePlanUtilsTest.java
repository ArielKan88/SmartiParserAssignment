package com.kanevsky.utils;

import com.kanevsky.config.UpdatePlan;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PriorityUpdatePlanUtilsTest {

    @Test
    void getUpdatePlan_noChanges() {
        Map<String, String> currentResourcesToMD5 = new HashMap<>();
        currentResourcesToMD5.put("file1", "md5_1");
        currentResourcesToMD5.put("file2", "md5_2");

        Map<String, String> latestFileToMD5 = new HashMap<>();
        latestFileToMD5.put("file1", "md5_1");
        latestFileToMD5.put("file2", "md5_2");

        UpdatePlan updatePlan = PriorityUpdatePlanUtils.getUpdatePlan(currentResourcesToMD5, latestFileToMD5);

        assertEquals(Set.of(), updatePlan.changed());
        assertEquals(Set.of("file1", "file2"), updatePlan.unchanged());
    }

    @Test
    void getUpdatePlan_someChanges() {
        Map<String, String> currentResourcesToMD5 = new HashMap<>();
        currentResourcesToMD5.put("file1", "md5_1");
        currentResourcesToMD5.put("file2", "md5_2_new"); // Changed
        currentResourcesToMD5.put("file3", "md5_3");

        Map<String, String> latestFileToMD5 = new HashMap<>();
        latestFileToMD5.put("file1", "md5_1");
        latestFileToMD5.put("file2", "md5_2");
        latestFileToMD5.put("file3", "md5_3");

        UpdatePlan updatePlan = PriorityUpdatePlanUtils.getUpdatePlan(currentResourcesToMD5, latestFileToMD5);

        assertEquals(Set.of("file2"), updatePlan.changed());
        assertEquals(Set.of("file1", "file3"), updatePlan.unchanged());
    }

    @Test
    void getUpdatePlan_newFile() {
        Map<String, String> currentResourcesToMD5 = new HashMap<>();
        currentResourcesToMD5.put("file1", "md5_1");
        currentResourcesToMD5.put("file4", "md5_4"); // New file

        Map<String, String> latestFileToMD5 = new HashMap<>();
        latestFileToMD5.put("file1", "md5_1");

        UpdatePlan updatePlan = PriorityUpdatePlanUtils.getUpdatePlan(currentResourcesToMD5, latestFileToMD5);

        assertEquals(Set.of("file4"), updatePlan.changed());
        assertEquals(Set.of("file1"), updatePlan.unchanged());
    }

    @Test
    void getUpdatePlan_missingFile() {
        Map<String, String> currentResourcesToMD5 = new HashMap<>();
        currentResourcesToMD5.put("file1", "md5_1");

        Map<String, String> latestFileToMD5 = new HashMap<>();
        latestFileToMD5.put("file1", "md5_1");
        latestFileToMD5.put("file2", "md5_2"); // Missing in current

        UpdatePlan updatePlan = PriorityUpdatePlanUtils.getUpdatePlan(currentResourcesToMD5, latestFileToMD5);

        assertEquals(Set.of(), updatePlan.changed());
        assertEquals(Set.of("file1"), updatePlan.unchanged());
    }
}