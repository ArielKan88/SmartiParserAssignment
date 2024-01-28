package com.kanevsky.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.kanevsky.views.MergerInputView;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Map;
import java.util.Optional;

@Component
public class MergerInputValidator implements Validator {

    private String entityTypeStr;

    public MergerInputValidator(@Value("${ENTITY_TYPE_STR}") String entityTypeStr) {
        this.entityTypeStr = entityTypeStr;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Map.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        MergerInputView inputView = (MergerInputView) target;

        if (isNullOrEmpty(inputView)) {
            errors.reject("0", "null or empty");
            return;
        }

        Map<String, JsonNode> inputs = inputView.getInputs();
        mandatoryFieldsPresent(errors, inputs);
        inputsOfSameEntityType(errors, inputs);
    }

    private boolean isNullOrEmpty(MergerInputView inputView) {
        return inputView == null || inputView.getInputs() == null || inputView.getInputs().isEmpty();
    }

    private void mandatoryFieldsPresent(Errors errors, Map<String, JsonNode> inputs) {
        for (Map.Entry<String, JsonNode> entry : inputs.entrySet()) {
            JsonNode jsonNode = entry.getValue();
            if (!jsonNode.has(entityTypeStr)) {
                errors.reject("1", String.format("%s is absent", entityTypeStr));
            }
        }
    }

    private void inputsOfSameEntityType(Errors errors, Map<String, JsonNode> inputs) {
        final long distinctEntities = inputs.values().stream()
                .filter(jsonNode -> !jsonNode.isEmpty())
                .map(jsonNode -> Optional.ofNullable(jsonNode.get(entityTypeStr)).map(JsonNode::textValue).orElse(""))
                .distinct()
                .limit(2)
                .count();

        if (distinctEntities > 1) {
            errors.reject("2", "Inputs are not of the same entityType.");
        }
    }
}