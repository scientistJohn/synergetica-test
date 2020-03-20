package com.synergetica.service;

import com.synergetica.dto.EventsRanks;
import com.synergetica.dto.PercentileBoundaries;
import com.synergetica.dto.RoadEvents;
import com.synergetica.mapper.Mapper;
import com.synergetica.model.Road;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EventService {

    MongoTemplate mongoTemplate;
    Mapper mapper;
    @NonFinal
    @Value("${application.percentile.first}")
    private Integer firstPercentile;
    @NonFinal
    @Value("${application.percentile.second}")
    private Integer secondPercentile;
    @NonFinal
    @Value("${application.percentile.third}")
    private Integer thirdPercentile;

    public List<String> getRoadNames() {
        Query query = new Query();
        query.fields().include("name");
        List<Road> roads = mongoTemplate.find(query, Road.class);
        return roads.stream()
                .map(Road::getName)
                .collect(Collectors.toList());
    }

    public EventsRanks getEventsRanks(String roadName, LocalDate startDate, LocalDate endDate) {
        List<AggregationOperation> pipeLine = new ArrayList<>();

        pipeLine.add(measurementDateBetweenAndNameIs(startDate, endDate, roadName));
        pipeLine.add(byNameSumAmounts());
        pipeLine.add(nameEvent1Event2());
        pipeLine.add(addFieldEvent3Event4());

        RoadEvents roadEvents = mongoTemplate.aggregate(Aggregation.newAggregation(pipeLine), "v_unwinded_roads", RoadEvents.class)
                .getUniqueMappedResult();

        return mapper.toEventsRanks(roadEvents);
    }

    private MatchOperation measurementDateBetweenAndNameIs(LocalDate startDate, LocalDate endDate, String roadName) {
        Criteria betweenDates = Criteria.where("measurements.measurementDate").gt(startDate).lte(endDate);
        Criteria name = Criteria.where("name").is(roadName);

        return Aggregation.match(new Criteria().andOperator(betweenDates, name));
    }

    private GroupOperation byNameSumAmounts() {
        return Aggregation.group("name")
                .sum("measurements.amountOfCalls").as("amountOfCalls")
                .sum("measurements.amountOfCallDrops").as("amountOfCallDrops")
                .sum("measurements.amountOfDataEvent").as("amountOfDataEvent")
                .sum("measurements.amountOfLeakedDataEvents").as("amountOfLeakedDataEvents");
    }

    private ProjectionOperation nameEvent1Event2() {
        return Aggregation.project()
                .andExpression("_id").as("name")
                .andExpression("amountOfCallDrops / amountOfCalls").as("event1")
                .andExpression("amountOfLeakedDataEvents / amountOfDataEvent").as("event2");
    }

    private ProjectionOperation addFieldEvent3Event4() {
        return Aggregation.project("name", "event1", "event2")
                .andExpression("1.0 - event1").as("event3")
                .andExpression("1.0 - event2").as("event4");
    }

    public List<PercentileBoundaries> getPercentileBoundaries(LocalDate startDate, LocalDate endDate) {
        return Stream.of("event1", "event2", "event3", "event4")
                .map(event -> getPercentileBoundaries(startDate, endDate, event))
                .collect(Collectors.toList());
    }

    public PercentileBoundaries getPercentileBoundaries(LocalDate startDate, LocalDate endDate, String event) {
        List<AggregationOperation> pipeLine = new ArrayList<>();

        pipeLine.add(Aggregation.match(Criteria.where("measurements.measurementDate").gt(startDate).lte(endDate)));
        pipeLine.add(byNameSumAmounts());
        pipeLine.add(nameEvent1Event2());
        pipeLine.add(addFieldEvent3Event4());
        pipeLine.add(Aggregation.sort(Sort.Direction.ASC, event));

        List<RoadEvents> events = mongoTemplate.aggregate(Aggregation.newAggregation(pipeLine), "v_unwinded_roads", RoadEvents.class)
                .getMappedResults();

        RoadEvents firstPercentileBoundary = events.get((int) ((double) events.size() * firstPercentile / 100));
        RoadEvents secondPercentileBoundary = events.get((int) ((double) events.size() * secondPercentile / 100));
        RoadEvents thirdPercentileBoundary = events.get((int) ((double) events.size() * thirdPercentile / 100));

        return mapper.toPercentileBoundaries(event, firstPercentileBoundary, secondPercentileBoundary, thirdPercentileBoundary);
    }

}
