package com.mironouz.action;

import com.mironouz.model.Aircraft;
import com.mironouz.model.Location;

import java.util.List;
import java.util.logging.Logger;


public final class ShootAction implements Action {
    private final Logger logger = Logger.getLogger(ShootAction.class.getName());

    public Aircraft act(List<Aircraft> aircrafts, Location location) {
        for (var aircraft : aircrafts) {
            if (aircraft.location().equals(location)) {
                logger.info(aircraft + " is destroyed");
                return aircraft;
            }
        }
        logger.info("You have missed");
        return null;
    }
}
