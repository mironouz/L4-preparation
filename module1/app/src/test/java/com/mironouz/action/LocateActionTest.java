package com.mironouz.action;

import com.mironouz.model.Aircraft;
import com.mironouz.model.Location;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LocateActionTest {
    private final Aircraft aircraft = new Aircraft(new Location(5, 5, 5), "testAircraft");

    @Test
    void testLocate() {
        assertEquals(List.of(new Location(5, 5, 5)), new LocateAction().act(List.of(aircraft)));
    }
}