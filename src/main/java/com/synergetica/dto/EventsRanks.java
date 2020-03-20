package com.synergetica.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class EventsRanks {
    String name;
    Integer event1;
    Integer event2;
    Integer event3;
    Integer event4;
}
