package com.kanevsky.controllers;

import com.kanevsky.helpers.TestHelper;
import com.kanevsky.views.ErrorView;
import com.kanevsky.views.MergerInputView;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
public class MergerController_IT {
    @Autowired
    private MergerController mergerController;

    @Test
    @SneakyThrows
    public void validPersonInputValidPersonConfigurationReturnsMergedResponse() {
        MergerInputView inputView = TestHelper.loadJsonFromFile("request/person/valid/validPersonRequest.json", MergerInputView.class);
        String actual = mergerController.merge(inputView).getBody().toString();
        String expected = "{\"age\":42,\"address\":{\"region\":\"IL\",\"city\":\"Jerusalem\"},\"name\":\"David\"}";
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    @SneakyThrows
    public void rejectsInvalidPersonRequestCompletelyMissingEntityTypesReturnsErrorMessage() {
        MergerInputView inputView = TestHelper.loadJsonFromFile("request/invalid/invalidPersonRequestCompletelyMissingEntityTypes.json", MergerInputView.class);
        ErrorView actual = (ErrorView) mergerController.merge(inputView).getBody();
        ErrorView expected = new ErrorView("Inputs are not of the same entityType.");
        assertEquals(expected, actual);
    }
}