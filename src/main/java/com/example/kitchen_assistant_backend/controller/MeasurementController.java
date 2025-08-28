package com.example.kitchen_assistant_backend.controller;

import com.example.kitchen_assistant_backend.model.ConversionRequest;
import com.example.kitchen_assistant_backend.model.ConversionResponse;
import com.example.kitchen_assistant_backend.service.MeasurementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/measurement")
public class MeasurementController {

    private final MeasurementService measurementService;

    @Autowired
    public MeasurementController(MeasurementService measurementService) {
        this.measurementService = measurementService;
    }

    @PostMapping("/convert")
    public ResponseEntity<ConversionResponse> convertMeasurement(@RequestBody ConversionRequest request) {
        ConversionResponse response = measurementService.convert(request);
        if ("error".equals(response.getStatus())){
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }
}