package pjatk.tpo.tpo6_fa_s30395.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pjatk.tpo.tpo6_fa_s30395.dto.BetRequest;
import pjatk.tpo.tpo6_fa_s30395.dto.HorseRankingDto;
import pjatk.tpo.tpo6_fa_s30395.model.User;
import pjatk.tpo.tpo6_fa_s30395.model.Horse;
import pjatk.tpo.tpo6_fa_s30395.model.Bet;
import pjatk.tpo.tpo6_fa_s30395.service.UserService;
import pjatk.tpo.tpo6_fa_s30395.service.HorseService;
import pjatk.tpo.tpo6_fa_s30395.service.BetService;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/data")
public class DataController {

    @Autowired
    private UserService userService;
    @Autowired
    private HorseService horseService;
    @Autowired
    private BetService betService;

    // --- USER ENDPOINTS ---
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/users")
    public ResponseEntity<User> addUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.saveUser(user));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        return userService.getUserById(id).map(user -> {
            user.setUsername(updatedUser.getUsername());
            user.setBalance(updatedUser.getBalance());
            return ResponseEntity.ok(userService.saveUser(user));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (userService.getUserById(id).isPresent()) {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/users/login")
    public ResponseEntity<User> login(@RequestParam String username, @RequestParam String password) {
        Optional<User> user = userService.findByUsername(username);
        if (user.isPresent() && user.get().getPassword().equals(password)) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.status(401).build();
        }
    }

    // --- HORSE ENDPOINTS ---
    @GetMapping("/horse")
    public List<Horse> getAllHorses() {
        return horseService.getAllHorses();
    }

    @GetMapping("/horse/{id}")
    public ResponseEntity<Horse> getHorse(@PathVariable Long id) {
        return horseService.getHorseById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/horse")
    public ResponseEntity<Horse> addHorse(@RequestBody Horse horse) {
        return ResponseEntity.ok(horseService.saveHorse(horse));
    }

    @PutMapping("/horse/{id}")
    public ResponseEntity<Horse> updateHorse(@PathVariable Long id, @RequestBody Horse updated) {
        return horseService.getHorseById(id).map(horse -> {
            horse.setName(updated.getName());
            horse.setWeight(updated.getWeight());
            horse.setSpeed(updated.getSpeed());
            horse.setWins(updated.getWins());
            horse.setRaces(updated.getRaces());
            return ResponseEntity.ok(horseService.saveHorse(horse));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/horse/{id}")
    public ResponseEntity<Void> deleteHorse(@PathVariable Long id) {
        if (horseService.getHorseById(id).isPresent()) {
            horseService.deleteHorse(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/horse/ranking")
    public List<HorseRankingDto> getRanking() {
        return horseService.getAllHorses().stream()
                .map(h -> new HorseRankingDto(h.getName(), h.getWins(), h.getRaces()))
                .sorted(Comparator.comparingInt(HorseRankingDto::getWins).reversed())
                .collect(Collectors.toList());
    }

    // --- BET ENDPOINTS ---
    @GetMapping("/bet")
    public List<Bet> getAllBets() {
        return betService.getAllBets();
    }

    @GetMapping("/bet/{id}")
    public ResponseEntity<Bet> getBet(@PathVariable Long id) {
        return betService.getBetById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/bet/{id}")
    public ResponseEntity<Bet> updateBet(@PathVariable Long id, @RequestBody Bet updated) {
        return betService.getBetById(id).map(bet -> {
            bet.setHorse(updated.getHorse());
            bet.setUser(updated.getUser());
            bet.setAmount(updated.getAmount());
            bet.setWon(updated.isWon());
            return ResponseEntity.ok(betService.saveBet(bet));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/bet/{id}")
    public ResponseEntity<Void> deleteBet(@PathVariable Long id) {
        if (betService.getBetById(id).isPresent()) {
            betService.deleteBet(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/bet/history/{userId}")
    public List<Bet> getBetHistory(@PathVariable Long userId) {
        return betService.getBetHistoryByUserId(userId);
    }

    @GetMapping("/bet/history/{userId}/sorted")
    public List<Bet> getSortedBetHistory(@PathVariable Long userId) {
        return betService.getBetHistoryByUserId(userId).stream()
                .sorted((b1, b2) -> {
                    String n1 = b1.getHorse() != null && b1.getHorse().getName() != null ? b1.getHorse().getName() : "";
                    String n2 = b2.getHorse() != null && b2.getHorse().getName() != null ? b2.getHorse().getName() : "";
                    return n1.compareToIgnoreCase(n2);
                })
                .collect(Collectors.toList());
    }

    @DeleteMapping("/bet/history/{userId}")
    public ResponseEntity<Void> deleteBetHistory(@PathVariable Long userId) {
        betService.deleteBetsByUserId(userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/bet/request")
    public ResponseEntity<Bet> addBetViaRequest(@RequestBody BetRequest request) {
        Optional<User> userOpt = userService.getUserById(request.getUserId());
        Optional<Horse> horseOpt = horseService.getHorseById(request.getHorseId());

        if (userOpt.isPresent() && horseOpt.isPresent()) {
            Bet bet = new Bet();
            bet.setUser(userOpt.get());
            bet.setHorse(horseOpt.get());
            bet.setAmount(request.getAmount());
            return ResponseEntity.ok(betService.saveBet(bet));
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

}