package com.electricity.monitor.controller;

import com.electricity.monitor.dto.*;
import com.electricity.monitor.entity.*;
import com.electricity.monitor.service.ElectricityService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins="*")
public class ElectricityController {
    private final ElectricityService service;
    public ElectricityController(ElectricityService service){this.service=service;}

    @PostMapping("/readings")
    @ResponseStatus(HttpStatus.CREATED)
    public ElectricityReading create(@Valid @RequestBody ReadingRequest req){
        return service.saveReading(req);
    }

    @GetMapping("/readings")
    public List<ElectricityReading> readings(
        @RequestParam String meterId,
        @RequestParam(required=false) LocalDate date){
        return service.getReadings(meterId,date!=null?date:LocalDate.now());
    }

    @GetMapping("/statistics/daily")
    public DailyStatistic daily(
        @RequestParam String meterId,
        @RequestParam(required=false) LocalDate date){
        return service.getDailyStatistic(meterId,date!=null?date:LocalDate.now());
    }

    @GetMapping("/notifications")
    public List<Notification> notifications(
        @RequestParam(defaultValue="false") boolean unreadOnly){
        return service.getNotifications(unreadOnly);
    }

    @PatchMapping("/notifications/{id}/read")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void read(@PathVariable Long id){service.markRead(id);}

    @GetMapping("/health")
    public String health(){return "OK";}
}
