package com.mironouz.service;

import com.mironouz.action.Action;
import com.mironouz.action.FlyAction;
import com.mironouz.action.LocateAction;
import com.mironouz.action.ShootAction;
import com.mironouz.model.Aircraft;
import com.mironouz.model.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameService {
    private final List<Aircraft> redTeam;
    private final List<Aircraft> blueTeam;
    private final Logger logger = Logger.getLogger(GameService.class.getName());

    private static final String GAME_STATE = """
            
            ===========================================================================================================
            | RED TEAM:
            | %s
            | BLUE TEAM:
            | %s
            ===========================================================================================================
            """;

    public GameService(List<Aircraft> redTeam, List<Aircraft> blueTeam) {
        this.redTeam = new ArrayList<>(redTeam);
        this.blueTeam = new ArrayList<>(blueTeam);
        logger.log(Level.INFO, "State before the game");
        logger.log(Level.INFO, GAME_STATE.formatted(redTeam, blueTeam));
    }

    public boolean playRound(List<Action> redTeamActions, List<Action> blueTeamActions) {
        playTeamRound(redTeam, redTeamActions, blueTeam);
        playTeamRound(blueTeam, blueTeamActions, redTeam);

        logger.log(Level.INFO, "State after the round");
        logger.log(Level.INFO, GAME_STATE.formatted(redTeam, blueTeam));

        return redTeam.isEmpty() || blueTeam.isEmpty();
    }

    private void playTeamRound(List<Aircraft> team, List<Action> actions, List<Aircraft> oppositeTeam) {
        for (int i = 0; i < team.size(); i++) {
            var currentAircraft = team.get(i);
            switch (actions.get(i)) {
                case FlyAction flyAction -> {
                    var updatedAircraft = flyAction.act(currentAircraft);
                    team.set(i, updatedAircraft);
                }
                case LocateAction locateAction -> locateAction.act(oppositeTeam);
                case ShootAction shootAction -> {
                    var destroyed = shootAction.act(oppositeTeam, new Location(1,1,1));
                    if (destroyed != null) {
                        oppositeTeam.remove(destroyed);
                    }
                }
            }
        }
    }
}
