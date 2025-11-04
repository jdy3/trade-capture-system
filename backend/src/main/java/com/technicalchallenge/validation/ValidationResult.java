package com.technicalchallenge.validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ValidationResult {

    private final List<String> messages = new ArrayList<>();

    public static ValidationResult success() {
        return new ValidationResult();
    }

    public static ValidationResult failure(String message) {
        ValidationResult result = new ValidationResult();
        result.addError(message);
        return result;
    }

    public boolean isValid() {
        return messages.isEmpty();
    }

    public void addError(String message) {
        messages.add(message);
    }

    public List<String> getMessages() {
        return Collections.unmodifiableList(messages);
    }

    public String getMessage() {
        return String.join("; ", messages);
    }
    
}
