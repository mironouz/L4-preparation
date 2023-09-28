package com.mironouz.action;

import com.mironouz.model.Aircraft;
import com.mironouz.model.Location;

import java.util.List;
import java.util.logging.Logger;

public final class LocateAction implements Action {
    private final Logger logger = Logger.getLogger(LocateAction.class.getName());

    public List<Location> act(List<Aircraft> aircrafts) {
        logger.info("Locate action is executed");
        return aircrafts.stream().map(Aircraft::location).toList();
    }
}
