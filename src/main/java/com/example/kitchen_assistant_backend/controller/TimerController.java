package com.example.kitchen_assistant_backend.controller;

import com.example.kitchen_assistant_backend.model.TimerRequest;
import com.example.kitchen_assistant_backend.model.ActiveTimerInfo;
import com.example.kitchen_assistant_backend.service.TimerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/timer")
public class TimerController {

    private final TimerService timerService;

    @Autowired
    public TimerController(TimerService timerService) {
        this.timerService = timerService;
    }

    @PostMapping("/start")
    public ResponseEntity<String> startTimer(@RequestBody TimerRequest request) {
        String timerId = timerService.startTimer(request);
        String message = "Timer '" + request.getName() + "' started with ID: " + timerId;
        return ResponseEntity.ok(message);
    }

    @PostMapping("/cancel/{timerId}")
    public ResponseEntity<String> cancelTimer(@PathVariable String timerId) {
        boolean cancelled = timerService.cancelTimer(timerId);
        if (cancelled) {
            return ResponseEntity.ok("Timer " + timerId + " was successfully cancelled.");
        } else {
            return ResponseEntity.status(404).body("Timer " + timerId + " not found or already finished.");
        }
    }

    @GetMapping("/status")
    public ResponseEntity<List<ActiveTimerInfo>> getActiveTimers() {
        return ResponseEntity.ok(timerService.getActiveTimers());
    }
}