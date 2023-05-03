package com.kailoslab.ai4x.commons.service;

import com.kailoslab.ai4x.commons.exception.Ai4xException;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class ValidatorService {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();

    public <T> void validate(T target) {
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<T>> violations = validator.validate(target);
        List<String> errorMessages = new ArrayList<>(violations.size());
        for (ConstraintViolation<T> violation : violations) {
            errorMessages.add(violation.getMessage());
        }

        if(!errorMessages.isEmpty()) {
            throw new Ai4xException(errorMessages);
        }
    }
}
