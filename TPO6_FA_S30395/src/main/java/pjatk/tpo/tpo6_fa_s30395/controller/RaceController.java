package pjatk.tpo.tpo6_fa_s30395.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pjatk.tpo.tpo6_fa_s30395.model.Bet;
import pjatk.tpo.tpo6_fa_s30395.model.Horse;
import pjatk.tpo.tpo6_fa_s30395.service.BetService;
import pjatk.tpo.tpo6_fa_s30395.service.HorseService;

import java.util.*;

@RestController
@RequestMapping("/data/race")
public class RaceController {

    private final HorseService horseService;
    private final BetService betService;

    public RaceController(HorseService horseService, BetService betService) {
        this.horseService = horseService;
        this.betService = betService;
    }

    // Simulate race
    @PostMapping("/start")
    public ResponseEntity<Map<String, Object>> startRace() {
        List<Horse> horses = horseService.getAllHorses();
        if (horses.size() < 2) {
            return ResponseEntity.badRequest().body(Map.of("error", "Not enough horses to start a race"));
        }

        Horse winner = simulateRace(horses);

        for (Horse horse : horses) {
            horse.setRaces(horse.getRaces() + 1);
            if (horse.getId().equals(winner.getId())) {
                horse.setWins(horse.getWins() + 1);
            }
            horseService.saveHorse(horse);
        }

        List<Bet> allBets = betService.getAllBets();
        List<Bet> winningBets = new ArrayList<>();
        double totalWon = 0;

        for (Bet bet : allBets) {
            if (bet.getHorse().getId().equals(winner.getId())) {
                winningBets.add(bet);
                totalWon += bet.getAmount();
            }
        }

        for (Bet bet : allBets) {
            bet.setWon(bet.getHorse().getId().equals(winner.getId()));
            betService.saveBet(bet);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("winner", winner.getName());
        result.put("winningHorseId", winner.getId());
        result.put("totalBets", allBets.size());
        result.put("betsWon", winningBets.size());
        result.put("totalWon", totalWon);
        result.put("message", winningBets.isEmpty() ? "No winning bets this time!" : "Congratulations to the winners!");

        return ResponseEntity.ok(result);
    }

    private Horse simulateRace(List<Horse> horses) {
        Random rand = new Random();
        return horses.stream()
                .max(Comparator.comparingDouble(h -> calculatePerformance(h, rand)))
                .orElseThrow();
    }

    private double calculatePerformance(Horse h, Random rand) {
        double luck = 0.7 + rand.nextDouble() * 1.6;
        return (h.getSpeed() * luck) - (h.getWeight() / 100);
    }
}