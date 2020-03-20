package com.synergetica.dto;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class PercentileBoundaries {

    Boundary firstPercentile;
    Boundary secondPercentile;
    Boundary thirdPercentile;
    Boundary fourthPercentile;

    @Value
    @Builder
    public static class Boundary {
        BigDecimal lowBoundary;
        BigDecimal highBoundary;
    }
}
