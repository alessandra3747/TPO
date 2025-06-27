package pjatk.tpo.tpo6_fa_s30395.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pjatk.tpo.tpo6_fa_s30395.model.Horse;

@Repository
public interface HorseRepository extends JpaRepository<Horse, Long> {
}
