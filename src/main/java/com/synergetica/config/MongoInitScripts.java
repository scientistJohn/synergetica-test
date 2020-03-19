package com.synergetica.config;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.synergetica.model.Road;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@ChangeLog
@Slf4j
public class MongoInitScripts {

    @ChangeSet(order = "001", id = "init", author = "Andrii Liashenko")
    public void init(MongoTemplate mongoTemplate) {
        mongoTemplate.indexOps(Road.class).ensureIndex(new Index().on("name", Sort.Direction.ASC).unique());

        int[] createdRoads = {4};
        createRoads(createdRoads[0], 100, 30).forEach(road -> {
            try {
                mongoTemplate.save(road);
            } catch (DataIntegrityViolationException e) {
                createdRoads[0]--;
            }
        });

        log.info("Created {} roads", createdRoads);
    }

    private List<Road> createRoads(int amount, int measurementsCount, int lastNDays) {
        List<Road> roads = new ArrayList<>();

        for (int i = 0; i < amount; i++) {
            Road road = new Road();

            road.setName(String.format("Road_%d", RandomUtils.nextInt()));
            road.setMeasurements(createMeasurements(measurementsCount, lastNDays));

            roads.add(road);
        }

        return roads;
    }

    private List<Road.Measurement> createMeasurements(int measurementsCount, int lastNDays) {
        List<Road.Measurement> measurements = new ArrayList<>();
        LocalDateTime firstMeasurementTime = LocalDateTime.now().minusDays(lastNDays);
        long timeGap = firstMeasurementTime.until(LocalDateTime.now(), ChronoUnit.SECONDS) / measurementsCount;

        for (int i = 0; i < measurementsCount; i++) {
            Road.Measurement measurement = new Road.Measurement();

            int amountOfCalls = RandomUtils.nextInt(0, 100);
            int amountOfCallDrops = RandomUtils.nextInt(0, amountOfCalls);
            int amountOfDataEvent = RandomUtils.nextInt(0, 100);
            int amountOfLeakedDataEvents = RandomUtils.nextInt(0, amountOfDataEvent);
            LocalDateTime startMeasurementTime = firstMeasurementTime.plusSeconds(i * timeGap);
            LocalDateTime finishMeasurementTime = firstMeasurementTime.plusSeconds((i + 1) * timeGap);

            measurement.setAmountOfCalls(amountOfCalls);
            measurement.setAmountOfCallDrops(amountOfCallDrops);
            measurement.setAmountOfDataEvent(amountOfDataEvent);
            measurement.setAmountOfLeakedDataEvents(amountOfLeakedDataEvents);
            measurement.setStartMeasurementTime(startMeasurementTime);
            measurement.setFinishMeasurementTime(finishMeasurementTime);

            measurements.add(measurement);
        }

        return measurements;
    }
}
