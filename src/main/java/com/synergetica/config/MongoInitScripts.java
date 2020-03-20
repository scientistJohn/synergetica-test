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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@ChangeLog
@Slf4j
public class MongoInitScripts {

    @ChangeSet(order = "001", id = "init", author = "Andrii Liashenko")
    public void init(MongoTemplate mongoTemplate) {
        mongoTemplate.indexOps(Road.class).ensureIndex(new Index().on("name", Sort.Direction.ASC).unique());

        int[] createdRoads = {10};
        createRoads(createdRoads[0], 365).forEach(road -> {
            try {
                mongoTemplate.save(road);
            } catch (DataIntegrityViolationException e) {
                createdRoads[0]--;
            }
        });

        log.info("Created {} roads", createdRoads);
    }

    private List<Road> createRoads(int amount, int measurementsCount) {
        List<Road> roads = new ArrayList<>();

        for (int i = 0; i < amount; i++) {
            Road road = new Road();

            road.setName(String.format("Road_%d", RandomUtils.nextInt()));
            road.setMeasurements(createMeasurements(measurementsCount));

            roads.add(road);
        }

        return roads;
    }

    private List<Road.Measurement> createMeasurements(int measurementsCount) {
        List<Road.Measurement> measurements = new ArrayList<>();
        LocalDate firstMeasurementDate = LocalDate.now().minusDays(measurementsCount);

        for (int i = 0; i < measurementsCount; i++) {
            Road.Measurement measurement = new Road.Measurement();

            int amountOfCalls = RandomUtils.nextInt(1, 100);
            int amountOfCallDrops = RandomUtils.nextInt(0, amountOfCalls);
            int amountOfDataEvent = RandomUtils.nextInt(1, 100);
            int amountOfLeakedDataEvents = RandomUtils.nextInt(0, amountOfDataEvent);
            LocalDate measurementDate = firstMeasurementDate.plusDays(i);

            measurement.setAmountOfCalls(amountOfCalls);
            measurement.setAmountOfCallDrops(amountOfCallDrops);
            measurement.setAmountOfDataEvent(amountOfDataEvent);
            measurement.setAmountOfLeakedDataEvents(amountOfLeakedDataEvents);
            measurement.setMeasurementDate(measurementDate);

            measurements.add(measurement);
        }

        return measurements;
    }

    @ChangeSet(order = "002", id = "createUnwindedRoadsView", author = "Andrii Liashenko")
    public void createUnwindedRoadsView(MongoTemplate mongoTemplate) {
        String view = "v_unwinded_roads";
        String collection = "roads";
        String pipeLine = "[\n" +
                "  {\n" +
                "    '$unwind': {\n" +
                "      'path': '$measurements'\n" +
                "    }\n" +
                "  }\n" +
                "]";

        mongoTemplate.executeCommand(String.format("{ create: '%s', viewOn: '%s', pipeline: %s }",
                view,
                collection,
                pipeLine));
    }

}
