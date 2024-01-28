package com.kanevsky.config;

import lombok.Getter;

import java.util.Set;

public record UpdatePlan(@Getter Set<String> changed, @Getter Set<String> unchanged) {
}