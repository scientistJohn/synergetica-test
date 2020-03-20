package com.synergetica.controller;

import com.synergetica.dto.EventsRanks;
import com.synergetica.service.EventService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@RunWith(SpringRunner.class)
public class EventControllerTest {

    private MockMvc mockmvc;

    @Mock
    private EventService eventService;

    @Before
    public void setup() throws Exception {
        EventController eventController = new EventController(eventService);

        mockmvc = MockMvcBuilders
                .standaloneSetup(eventController)
                .build();
    }

    @Test
    public void testGetRoadNames() throws Exception {
        when(eventService.getRoadNames()).thenReturn(Arrays.asList("road_1", "road_2"));

        mockmvc.perform(get("/roads"))
                .andExpect(MockMvcResultMatchers.jsonPath("[0]").value("road_1"))
                .andExpect(MockMvcResultMatchers.jsonPath("[1]").value("road_2"));

        verify(eventService, times(1)).getRoadNames();
    }

    @Test
    public void testGetEventsRanks() throws Exception {
        EventsRanks ranks = EventsRanks.builder()
                .name("road")
                .event1(1)
                .event2(2)
                .event3(3)
                .event4(4)
                .build();
        when(eventService.getEventsRanks(any(), any(), any())).thenReturn(ranks);

        mockmvc.perform(get("/eventRanks").param("roadName", "test")
                .param("startDate", LocalDate.MIN.toString())
                .param("endDate", LocalDate.MIN.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(ranks.getName()));

        verify(eventService, times(1)).getEventsRanks(any(), any(), any());
    }
}
