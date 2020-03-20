package com.synergetica.mapper;

import com.synergetica.dto.EventsRanks;
import com.synergetica.dto.RoadEvents;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class MapperTest {

    private Mapper mapper;

    @Before
    public void init() {
        mapper = new Mapper();

        mapper.setFirstPercentile(25);
        mapper.setSecondPercentile(50);
        mapper.setThirdPercentile(75);
    }

    @Test
    public void testToEventsRanks() {
        RoadEvents roadEvents = RoadEvents.builder()
                .name("road")
                .event1(BigDecimal.valueOf(0.1))
                .event2(BigDecimal.valueOf(0.3))
                .event3(BigDecimal.valueOf(0.6))
                .event4(BigDecimal.valueOf(0.8))
                .build();

        EventsRanks eventsRanks = mapper.toEventsRanks(roadEvents);

        assertEquals(eventsRanks.getName(), "road");
        assertEquals(eventsRanks.getEvent1(), Integer.valueOf(1));
        assertEquals(eventsRanks.getEvent2(), Integer.valueOf(2));
        assertEquals(eventsRanks.getEvent3(), Integer.valueOf(3));
        assertEquals(eventsRanks.getEvent4(), Integer.valueOf(4));
    }
}
