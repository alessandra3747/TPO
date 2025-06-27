package pjatk.tpo.tpo6_fa_s30395.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pjatk.tpo.tpo6_fa_s30395.model.Horse;
import pjatk.tpo.tpo6_fa_s30395.repository.HorseRepository;

import java.util.List;
import java.util.Optional;

@Service
public class HorseService {

    @Autowired
    private HorseRepository horseRepository;

    public List<Horse> getAllHorses() {
        return horseRepository.findAll();
    }

    public Optional<Horse> getHorseById(Long id) {
        return horseRepository.findById(id);
    }

    public Horse saveHorse(Horse horse) {
        return horseRepository.save(horse);
    }

    public void deleteHorse(Long id) {
        horseRepository.deleteById(id);
    }
}
