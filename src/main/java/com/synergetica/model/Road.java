package com.synergetica.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document("roads")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Road {
    @Id
    String id;
    String name;
    List<Measurement> measurements;

    @Data
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Measurement {
        Integer amountOfCalls;
        Integer amountOfCallDrops;
        Integer amountOfDataEvent;
        Integer amountOfLeakedDataEvents;
        LocalDateTime startMeasurementTime;
        LocalDateTime finishMeasurementTime;
    }
}
