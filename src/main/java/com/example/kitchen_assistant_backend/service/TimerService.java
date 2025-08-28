package com.example.kitchen_assistant_backend.service;

import com.example.kitchen_assistant_backend.model.ActiveTimerInfo;
import com.example.kitchen_assistant_backend.model.TimerRequest;
import com.example.kitchen_assistant_backend.websocket.KitchenAssistantHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
public class TimerService {

    private final Map<String, ScheduledFuture<?>> activeTimers = new ConcurrentHashMap<>();
    private final Map<String, String> timerNames = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public String startTimer(TimerRequest request) {
        long delay = convertToMilliseconds(request.getDuration(), request.getUnit());
        Runnable task = () -> {
            System.out.println("TIMER FINISHED: Timer '" + request.getName() + "' is done!");
            timerNames.remove(request.getName());
        };

        ScheduledFuture<?> scheduledTask = scheduler.schedule(task, delay, TimeUnit.MILLISECONDS);
        String timerId = UUID.randomUUID().toString();
        activeTimers.put(timerId, scheduledTask);
        timerNames.put(timerId, request.getName());
        System.out.println("Started timer " + timerId + " for " + delay + "ms.");
        return timerId;
    }

    public boolean cancelTimer(String timerId) {
        ScheduledFuture<?> scheduledTask = activeTimers.get(timerId);
        if (scheduledTask != null) {
            boolean wasCancelled = scheduledTask.cancel(true);
            activeTimers.remove(timerId);
            timerNames.remove(timerId);
            System.out.println("Cancelled timer " + timerId);
            return wasCancelled;
        }
        return false;
    }

    public List<ActiveTimerInfo> getActiveTimers() {
        return activeTimers.entrySet().stream()
                .filter(entry -> !entry.getValue().isDone() && !entry.getValue().isCancelled())
                .map(entry -> {
                    long remainingSeconds = entry.getValue().getDelay(TimeUnit.SECONDS);
                    String name = timerNames.get(entry.getKey());
                    return new ActiveTimerInfo(entry.getKey(), name, remainingSeconds);
                })
                .collect(Collectors.toList());
    }


    private long convertToMilliseconds(long duration, String unit) {
        if ("seconds".equalsIgnoreCase(unit)) {
            return duration * 1000;
        }
        if ("minutes".equalsIgnoreCase(unit)) {
            return duration * 60 * 1000;
        }
        if ("hours".equalsIgnoreCase(unit)) {
            return duration * 60 * 60 * 1000;
        }
        return 0;
    }

    @Scheduled(fixedRate = 1000)
    public void broadcastTimerUpdates() {
        if (activeTimers.isEmpty()) {
            return;
        }

        List<ActiveTimerInfo> timerInfos = getActiveTimers();

        try {
            ObjectMapper mapper = new ObjectMapper();
            String timersJson = mapper.writeValueAsString(timerInfos);
            String message = "{\"command\":\"TIMERS_UPDATE\", \"timers\":" + timersJson + "}";
            KitchenAssistantHandler.sendMessageToAll(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}