package com.joaogoncalves.feedback.controller;

import lombok.experimental.UtilityClass;

import java.util.UUID;
import java.util.function.Supplier;

@UtilityClass
public class TestUtils {

    public static Supplier<String> generateBigString = () ->
            UUID.randomUUID().toString().replace("-", "").substring(0, 31);
}
