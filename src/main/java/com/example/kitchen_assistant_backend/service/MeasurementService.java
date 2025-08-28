package com.example.kitchen_assistant_backend.service;

import com.example.kitchen_assistant_backend.model.ConversionRequest;
import com.example.kitchen_assistant_backend.model.ConversionResponse;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class MeasurementService {

    private static final Map<String, Double> ratesToMl = new HashMap<>();
    private static final Map<String, Double> ratesToGrams = new HashMap<>();

    // A single static block to initialize all our maps
    static {
        // Volume Units (base: ml)
        ratesToMl.put("milliliter", 1.0);
        ratesToMl.put("ml", 1.0);
        ratesToMl.put("liter", 1000.0);
        ratesToMl.put("l", 1000.0);
        ratesToMl.put("teaspoon", 4.92892);
        ratesToMl.put("tsp", 4.92892);
        ratesToMl.put("tablespoon", 14.7868);
        ratesToMl.put("tbsp", 14.7868);
        ratesToMl.put("cup", 236.588);
        ratesToMl.put("cups", 236.588);
        ratesToMl.put("fl oz", 29.5735);
        ratesToMl.put("pt", 473.176);
        ratesToMl.put("qt", 946.353);
        ratesToMl.put("gal", 3785.41);

        // Weight Units (base: g)
        ratesToGrams.put("gram", 1.0);
        ratesToGrams.put("g", 1.0);
        ratesToGrams.put("ounce", 28.3495);
        ratesToGrams.put("oz", 28.3495);
        ratesToGrams.put("pound", 453.592);
        ratesToGrams.put("lb", 453.592);
        ratesToGrams.put("kilogram", 1000.0);
        ratesToGrams.put("kg", 1000.0);
    }

    public ConversionResponse convert(ConversionRequest request) {
        String type = request.getType();
        String sourceUnit = request.getSourceUnit().toLowerCase();
        String targetUnit = request.getTargetUnit().toLowerCase();

        Map<String, Double> ratesMap;

        // Step 1: Check the conversion type and select the correct map
        if ("weight".equalsIgnoreCase(type)) {
            ratesMap = ratesToGrams;
        } else if ("volume".equalsIgnoreCase(type)) {
            ratesMap = ratesToMl;
        } else {
            return new ConversionResponse("error", 0, null, "Invalid conversion type provided.");
        }

        // Step 2: Check if the units are valid for the selected type
        if (!ratesMap.containsKey(sourceUnit) || !ratesMap.containsKey(targetUnit)) {
            return new ConversionResponse("error", 0, null, "Invalid unit provided for the selected type.");
        }

        // Step 3: Perform the calculation using the selected map
        double amountInBaseUnit = request.getQuantity() * ratesMap.get(sourceUnit);
        double result = amountInBaseUnit / ratesMap.get(targetUnit);

        String message = String.format("%.2f %s is equal to %.2f %s.",
                request.getQuantity(), sourceUnit, result, targetUnit);

        return new ConversionResponse("success", result, targetUnit, message);
    }
}