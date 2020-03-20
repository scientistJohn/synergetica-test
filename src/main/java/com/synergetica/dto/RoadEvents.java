package com.synergetica.dto;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class RoadEvents {
    String name;
    BigDecimal event1;
    BigDecimal event2;
    BigDecimal event3;
    BigDecimal event4;
}
