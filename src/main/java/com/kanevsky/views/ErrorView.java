package com.kanevsky.views;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

@EqualsAndHashCode
public class ErrorView {
    public ErrorView(String errorMessage) {
        this.errorMessages = Set.of(errorMessage);
    }

    public ErrorView(Collection<String> errorMessages) {
        this.errorMessages = new LinkedHashSet(errorMessages);
    }

    @Getter
    private Set<String> errorMessages;
}