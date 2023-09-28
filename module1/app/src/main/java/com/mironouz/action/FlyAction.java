package com.mironouz.action;

import com.mironouz.model.Aircraft;
import com.mironouz.model.Direction;
import com.mironouz.model.Location;

import java.util.logging.Logger;

public final class FlyAction implements Action {
    private final Direction direction;
    private final Logger logger = Logger.getLogger(FlyAction.class.getName());

    public FlyAction(Direction direction) {
        this.direction = direction;
    }

    public Aircraft act(Aircraft aircraft) {
        var currentLocation = aircraft.location();
        var newLocation = switch (direction) {
            case UP -> new Location(currentLocation.x(), currentLocation.y() + 1, currentLocation.z());
            case DOWN -> new Location(currentLocation.x(), currentLocation.y() - 1, currentLocation.z());
            case RIGHT -> new Location(currentLocation.x(), currentLocation.y(), currentLocation.z() + 1);
            case LEFT -> new Location(currentLocation.x(), currentLocation.y(), currentLocation.z() - 1);
        };
        logger.info("Aircraft moved %s from %s to %s".formatted(direction, currentLocation, newLocation));
        return new Aircraft(newLocation, aircraft.id());
    }
}
