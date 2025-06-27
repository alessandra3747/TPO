package pjatk.tpo.tpo6_fa_s30395.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pjatk.tpo.tpo6_fa_s30395.model.Bet;
import java.util.List;

@Repository
public interface BetRepository extends JpaRepository<Bet, Long> {
    List<Bet> findByUserId(Long userId);
    void deleteByUser_Id(Long userId);
}