package com.synergetica.mapper;

import com.synergetica.dto.EventsRanks;
import com.synergetica.dto.RoadEvents;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@Setter
public class Mapper {

    @Value("${application.percentile.first}")
    private Integer firstPercentile;
    @Value("${application.percentile.second}")
    private Integer secondPercentile;
    @Value("${application.percentile.third}")
    private Integer thirdPercentile;

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
}
