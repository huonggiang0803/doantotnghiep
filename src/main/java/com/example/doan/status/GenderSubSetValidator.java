package com.example.doan.status;

import java.util.Arrays;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class GenderSubSetValidator implements ConstraintValidator<GenderSubset, GenderEnum> {
    private GenderEnum[] genders;

    @Override
    public void initialize(GenderSubset constraint) {
        this.genders = constraint.anyOf();
    }
    @Override
    public boolean isValid(GenderEnum value, ConstraintValidatorContext context) {
        return value == null || Arrays.asList(genders).contains(value);
    }
}
