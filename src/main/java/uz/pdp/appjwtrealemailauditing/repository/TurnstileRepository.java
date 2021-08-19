package uz.pdp.appjwtrealemailauditing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.pdp.appjwtrealemailauditing.entity.Turnstile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TurnstileRepository extends JpaRepository<Turnstile, Integer> {

    Optional<Turnstile> findByUserAndStatus(UUID userId, boolean status);

    @Query("select tur from Turnstile tur " +
            "where tur.user = :employeeId and (tur.cameAt >= :start or tur.leftAt   <= :finish)")
    List<Turnstile> findAllByCreatedByAndEnterDateTimeAndExitDateTimeBefore(UUID employeeId, LocalDateTime start, LocalDateTime finish);
}
