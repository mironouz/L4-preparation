package com.mironouz;

import com.mironouz.model.Aircraft;
import com.mironouz.model.Location;
import com.mironouz.service.GameService;
import com.mironouz.service.RandomService;

import java.util.List;

public class App {
    private static final int MAX_ROUNDS = 5;

    public static void main(String[] args) {
        Aircraft redAircraft1 = new Aircraft(new Location(0, 4, 0), "red1");
        Aircraft redAircraft2 = new Aircraft(new Location(0, 5, 0), "red2");
        Aircraft redAircraft3 = new Aircraft(new Location(0, 6, 0), "red3");


        Aircraft blueAircraft1 = new Aircraft(new Location(9, 4, 0), "blue1");
        Aircraft blueAircraft2 = new Aircraft(new Location(9, 5, 1), "blue2");
        Aircraft blueAircraft3 = new Aircraft(new Location(9, 6, 1), "blue3");


        RandomService randomService = new RandomService();

        GameService gameService = new GameService(
                List.of(redAircraft1, redAircraft2, redAircraft3),
                List.of(blueAircraft1, blueAircraft2, blueAircraft3)
        );

        for (int i = 0; i < MAX_ROUNDS; i++) {
            gameService.playRound(
                    List.of(randomService.getRandomAction(), randomService.getRandomAction(), randomService.getRandomAction()),
                    List.of(randomService.getRandomAction(), randomService.getRandomAction(), randomService.getRandomAction())
            );
        }
    }
}
