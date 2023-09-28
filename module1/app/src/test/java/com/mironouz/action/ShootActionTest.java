package com.mironouz.action;

import com.mironouz.model.Aircraft;
import com.mironouz.model.Location;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ShootActionTest {
    private final Aircraft aircraft = new Aircraft(new Location(5, 5, 5), "testAircraft");

    @Test
    void testHit() {
        var result = new ShootAction().act(List.of(aircraft), new Location(5, 5, 5));

        assertEquals(aircraft, result);
    }

    @Test
    void testMiss() {
        var result = new ShootAction().act(List.of(aircraft), new Location(5, 5, 4));

        assertNull(result);
    }
}