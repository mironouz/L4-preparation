package com.mironouz.action;

import com.mironouz.model.Aircraft;
import com.mironouz.model.Direction;
import com.mironouz.model.Location;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FlyActionTest {
    private final Aircraft aircraft = new Aircraft(new Location(5, 5, 5), "testAircraft");
    @Test
    void testFlyUp() {
        var result = new FlyAction(Direction.UP).act(aircraft);

        assertEquals(new Location(5, 6, 5), result.location());
    }

    @Test
    void testFlyDown() {
        var result = new FlyAction(Direction.DOWN).act(aircraft);

        assertEquals(new Location(5, 4, 5), result.location());
    }

    @Test
    void testFlyRight() {
        var result = new FlyAction(Direction.RIGHT).act(aircraft);

        assertEquals(new Location(5, 5, 6), result.location());
    }

    @Test
    void testFlyLeft() {
        var result = new FlyAction(Direction.LEFT).act(aircraft);

        assertEquals(new Location(5, 5, 4), result.location());
    }
}