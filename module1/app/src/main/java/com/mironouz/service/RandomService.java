package com.mironouz.service;

import com.mironouz.action.Action;
import com.mironouz.action.FlyAction;
import com.mironouz.model.Direction;
import com.mironouz.model.Location;

import java.util.concurrent.ThreadLocalRandom;

public class RandomService {
    public Direction getRandomDirection() {
        return Direction.values()[ThreadLocalRandom.current().nextInt(4)];
    }

    public Location getRandomLocation() {
        var threadLocalRandom = ThreadLocalRandom.current();
        return new Location(
                threadLocalRandom.nextInt(10),
                threadLocalRandom.nextInt(10),
                threadLocalRandom.nextInt(10)
        );
    }

    public Action getRandomAction() {
        var permittedSubclasses = Action.class.getPermittedSubclasses();
        var randomAction = permittedSubclasses[ThreadLocalRandom.current().nextInt(permittedSubclasses.length)];
        System.out.println(randomAction);
        Action action = null;
        try {
            if (randomAction == FlyAction.class) {
                action = (Action) randomAction.getDeclaredConstructor(Direction.class).newInstance(getRandomDirection());
            } else {
                action = (Action) randomAction.getDeclaredConstructor().newInstance();
            }
        } catch (Exception e) {

        }
        return action;
    }
}
