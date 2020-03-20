package com.synergetica.mapper;

import com.synergetica.dto.EventsRanks;
import com.synergetica.dto.PercentileBoundaries;
import com.synergetica.dto.RoadEvents;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@Setter
public class Mapper {

    @Value("${application.percentile.first}")
    private Integer firstPercentile;
    @Value("${application.percentile.second}")
    private Integer secondPercentile;
    @Value("${application.percentile.third}")
    private Integer thirdPercentile;
    private static final Map<String, Function<RoadEvents, BigDecimal>> ROAD_EVENTS_GETTERS_MAP = new HashMap<String, Function<RoadEvents, BigDecimal>>() {{
        put("event1", RoadEvents::getEvent1);
        put("event2", RoadEvents::getEvent2);
        put("event3", RoadEvents::getEvent3);
        put("event4", RoadEvents::getEvent4);
    }};

    public EventsRanks toEventsRanks(RoadEvents roadEvents) {
        return EventsRanks.builder()
                .name(roadEvents.getName())
                .event1(getRank(roadEvents.getEvent1()))
                .event2(getRank(roadEvents.getEvent2()))
                .event3(getRank(roadEvents.getEvent3()))
                .event4(getRank(roadEvents.getEvent4()))
                .build();
    }

    private Integer getRank(BigDecimal eventValue) {
        int percent = eventValue.multiply(BigDecimal.valueOf(100L))
                .setScale(0, RoundingMode.HALF_UP)
                .intValue();

        if (percent <= firstPercentile) {
            return 1;
        }

        if (percent <= secondPercentile) {
            return 2;
        }

        if (percent <= thirdPercentile) {
            return 3;
        }

        return 4;
    }

    public PercentileBoundaries toPercentileBoundaries(String event, RoadEvents firstPercentileBoundary, RoadEvents secondPercentileBoundary, RoadEvents thirdPercentileBoundary) {
        Function<RoadEvents, BigDecimal> getter = ROAD_EVENTS_GETTERS_MAP.get(event);
        return PercentileBoundaries.builder()
                .firstPercentile(PercentileBoundaries.Boundary.builder().lowBoundary(BigDecimal.ZERO).highBoundary(getter.apply(firstPercentileBoundary)).build())
                .secondPercentile(PercentileBoundaries.Boundary.builder().lowBoundary(getter.apply(firstPercentileBoundary)).highBoundary(getter.apply(secondPercentileBoundary)).build())
                .thirdPercentile(PercentileBoundaries.Boundary.builder().lowBoundary(getter.apply(secondPercentileBoundary)).highBoundary(getter.apply(thirdPercentileBoundary)).build())
                .fourthPercentile(PercentileBoundaries.Boundary.builder().lowBoundary(getter.apply(thirdPercentileBoundary)).highBoundary(BigDecimal.ONE).build())
                .build();
    }
}
