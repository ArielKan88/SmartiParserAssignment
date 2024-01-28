package com.kanevsky.controllers;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.kanevsky.helpers.TestHelper;
import com.kanevsky.views.MergerInputView;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MergerInputValidatorTest {
    private MergerInputValidator validator = new MergerInputValidator("entityType");

    @Test
    public void supportsMapClass() {
        assertTrue(validator.supports(Map.class));
        assertFalse(validator.supports(String.class));
        assertFalse(validator.supports(List.class));
    }

    @Test
    public void rejectsNullInputs() {
        Errors errors = new BeanPropertyBindingResult(null, "inputView");
        validator.validate(null, errors);
        assertTrue(errors.hasErrors());
    }

    @Test
    public void rejectsEmptyMapInput() {
        MergerInputView inputView = new MergerInputView(Map.of());
        Errors errors = new BeanPropertyBindingResult(inputView, "inputView");
        validator.validate(inputView, errors);
        assertTrue(errors.hasErrors());
    }

    @Test
    public void rejectsEmptyEntityInput() {
        MergerInputView inputView = new MergerInputView(Map.of("c2", JsonNodeFactory.instance.objectNode()));
        Errors errors = new BeanPropertyBindingResult(inputView, "inputView");
        validator.validate(inputView, errors);
        assertTrue(errors.hasErrors());
    }

    @SneakyThrows
    @Test
    public void acceptsValidInput() {
        MergerInputView inputView = TestHelper.loadJsonFromFile("request/person/valid/validPersonRequest.json", MergerInputView.class);
        Errors errors = new BeanPropertyBindingResult(inputView, "inputView");
        validator.validate(inputView, errors);
        assertFalse(errors.hasErrors());
    }

    @SneakyThrows
    @Test
    public void rejectsInvalidPersonRequestCompletelyMissingEntityTypes() {
        MergerInputView inputView = TestHelper.loadJsonFromFile("request/invalid/invalidPersonRequestCompletelyMissingEntityTypes.json", MergerInputView.class);
        Errors errors = new BeanPropertyBindingResult(inputView, "inputView");
        validator.validate(inputView, errors);
        assertTrue(errors.hasErrors());
    }

    @SneakyThrows
    @Test
    public void rejectsInvalidPersonRequestDifferentEntityTypes() {
        MergerInputView inputView = TestHelper.loadJsonFromFile("request/invalid/invalidPersonRequestDifferentEntityTypes.json", MergerInputView.class);
        Errors errors = new BeanPropertyBindingResult(inputView, "inputView");
        validator.validate(inputView, errors);
        assertTrue(errors.hasErrors());
    }

    @SneakyThrows
    @Test
    public void rejectsInvalidPersonRequestSomeHaveMissingEntityTypes() {
        MergerInputView inputView = TestHelper.loadJsonFromFile("request/invalid/invalidPersonRequestSomeHaveMissingEntityTypes.json", MergerInputView.class);
        Errors errors = new BeanPropertyBindingResult(inputView, "inputView");
        validator.validate(inputView, errors);
        assertTrue(errors.hasErrors());
    }
}