package roomescape.repository;

import roomescape.domain.Reservation;
import roomescape.domain.ReservationTime;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository {

    Reservation save(Reservation reservation);

    List<Reservation> findAllByDateAndTimeAndThemeId(LocalDate date, ReservationTime time, Long themeId);

    List<Reservation> findAll();

    Optional<Reservation> findById(Long id);

    void deleteById(Long id);

    int countByTimeId(Long timeId);

    List<Reservation> findAllByDateAndThemeId(LocalDate date, Long themeId);
}
