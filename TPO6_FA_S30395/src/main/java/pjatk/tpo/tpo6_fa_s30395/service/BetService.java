package pjatk.tpo.tpo6_fa_s30395.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pjatk.tpo.tpo6_fa_s30395.model.Bet;
import pjatk.tpo.tpo6_fa_s30395.repository.BetRepository;

import java.util.List;
import java.util.Optional;

@Service
public class BetService {

    @Autowired
    private BetRepository betRepository;

    public List<Bet> getAllBets() {
        return betRepository.findAll();
    }

    public Optional<Bet> getBetById(Long id) {
        return betRepository.findById(id);
    }

    public Bet saveBet(Bet bet) {
        return betRepository.save(bet);
    }

    public void deleteBet(Long id) {
        betRepository.deleteById(id);
    }

    public List<Bet> getBetHistoryByUserId(Long userId) {
        return betRepository.findByUserId(userId);
    }

    @Transactional
    public void deleteBetsByUserId(Long userId) {
        betRepository.deleteByUser_Id(userId);
    }
}