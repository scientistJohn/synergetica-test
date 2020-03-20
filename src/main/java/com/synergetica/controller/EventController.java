package com.synergetica.controller;

import com.synergetica.dto.EventsRanks;
import com.synergetica.service.EventService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EventController {
    EventService service;

    @GetMapping("/roads")
    public List<String> getRoadNames() {
        return service.getRoadNames();
    }

    @GetMapping("/eventRanks")
    public EventsRanks getEventsRanks(@RequestParam("roadName") String roadName,
                                      @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                      @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return service.getEventsRanks(roadName, startDate, endDate);
    }
}
