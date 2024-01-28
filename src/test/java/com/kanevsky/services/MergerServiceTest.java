package com.kanevsky.services;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kanevsky.config.IPrioritiesManager;
import com.kanevsky.helpers.TestHelper;
import com.kanevsky.views.MergerInputView;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.util.List;
import java.util.Map;

class MergerServiceTest {

    private MergerService mergerService;

    IPrioritiesManager getPriorityManagerAsInAssignment() {
        IPrioritiesManager prioritiesManager = Mockito.mock(IPrioritiesManager.class);
        Mockito.when(prioritiesManager.getPriorities(Mockito.matches("person")))
                .thenReturn(
                        Map.of(List.of("tz"), List.of("webint", "c2"),
                                List.of("name"), List.of("webint", "c2"),
                                List.of("age"), List.of("c2", "webint"),
                                List.of("address", "city"), List.of("c2", "webint"),
                                List.of("address", "region"), List.of("webint", "c2")));
        return prioritiesManager;
    }

    IPrioritiesManager getEmptyPriorityManager() {
        IPrioritiesManager prioritiesManager = Mockito.mock(IPrioritiesManager.class);
        Mockito.when(prioritiesManager.getPriorities(Mockito.matches("person")))
                .thenReturn(Map.of());
        return prioritiesManager;
    }

    @Test
    @SneakyThrows
    void validPersonInputValidPersonConfigurationMergesCorrectly() {
        mergerService = new MergerService(getPriorityManagerAsInAssignment(), "entityType");
        MergerInputView inputView = TestHelper.loadJsonFromFile("request/person/valid/validPersonRequest.json", MergerInputView.class);
        ObjectNode merge = mergerService.merge(inputView.getInputs());
        String expected = "{\"age\":42,\"address\":{\"region\":\"IL\",\"city\":\"Jerusalem\"},\"name\":\"David\"}";
        JSONAssert.assertEquals(expected, merge.toString(), JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    @SneakyThrows
    void validPersonInputWithAdditionalFieldsValidPersonConfigurationMergesCorrectly() {
        mergerService = new MergerService(getPriorityManagerAsInAssignment(), "entityType");
        MergerInputView inputView = TestHelper.loadJsonFromFile("request/person/valid/validPersonRequestAdditionalFields.json", MergerInputView.class);
        ObjectNode merge = mergerService.merge(inputView.getInputs());
        String expected = "{\"age\":42,\"address\":{\"region\":\"IL\",\"city\":\"Jerusalem\"},\"name\":\"David\"}";
        JSONAssert.assertEquals(expected, merge.toString(), JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    @SneakyThrows
    void validPersonInputWithAdditionalSourcesValidPersonConfigurationMergesCorrectly() {
        mergerService = new MergerService(getPriorityManagerAsInAssignment(), "entityType");
        MergerInputView inputView = TestHelper.loadJsonFromFile("request/person/valid/validPersonRequestAdditionalSources.json", MergerInputView.class);
        ObjectNode merge = mergerService.merge(inputView.getInputs());
        String expected = "{\"age\":42,\"address\":{\"region\":\"IL\",\"city\":\"Jerusalem\"},\"name\":\"David\"}";
        JSONAssert.assertEquals(expected, merge.toString(), JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    @SneakyThrows
    void validDogInputWithAdditionalSourcesValidPersonConfigurationMergeReturnsEmptyJson() {
        mergerService = new MergerService(getPriorityManagerAsInAssignment(), "entityType");
        MergerInputView inputView = TestHelper.loadJsonFromFile("request/dog/valid/validDogRequest.json", MergerInputView.class);
        ObjectNode merge = mergerService.merge(inputView.getInputs());
        String expected = "{}";
        JSONAssert.assertEquals(expected, merge.toString(), JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    @SneakyThrows
    void validPersonInputEmptyPersonConfigurationMergeReturnsEmptyJson() {
        mergerService = new MergerService(getEmptyPriorityManager(), "entityType");
        MergerInputView inputView = TestHelper.loadJsonFromFile("request/person/valid/validPersonRequest.json", MergerInputView.class);
        ObjectNode merge = mergerService.merge(inputView.getInputs());
        String expected = "{}";
        JSONAssert.assertEquals(expected, merge.toString(), JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    @SneakyThrows
    void validPersonRequestHigherPriorityNameIsNull_ValidPersonConfigurationMerge_ReturnsExplicitlyNullName() {
        mergerService = new MergerService(getPriorityManagerAsInAssignment(), "entityType");
        MergerInputView inputView = TestHelper.loadJsonFromFile("request/person/valid/validPersonRequestHigherPriorityNameIsNull.json", MergerInputView.class);
        ObjectNode merge = mergerService.merge(inputView.getInputs());
        String expected = "{\"age\":42,\"address\":{\"region\":\"IL\",\"city\":\"Jerusalem\"},\"name\":null}";
        JSONAssert.assertEquals(expected, merge.toString(), JSONCompareMode.NON_EXTENSIBLE);
    }


    @Test
    @SneakyThrows
    void validPersonRequestHigherPriorityNameIsNull_ValidPersonConfigurationMerge_ReturnsLowerPriorityName() {
        mergerService = new MergerService(getPriorityManagerAsInAssignment(), "entityType");
        MergerInputView inputView = TestHelper.loadJsonFromFile("request/person/valid/validPersonRequestHigherPriorityNameAbsent.json", MergerInputView.class);
        ObjectNode merge = mergerService.merge(inputView.getInputs());
        String expected = "{\"age\":42,\"address\":{\"region\":\"IL\",\"city\":\"Jerusalem\"},\"name\":\"Sarah\"}";
        JSONAssert.assertEquals(expected, merge.toString(), JSONCompareMode.NON_EXTENSIBLE);
    }
}