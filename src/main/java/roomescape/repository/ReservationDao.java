package roomescape.repository;

import roomescape.domain.Reservation;
import roomescape.domain.ReservationTime;

import java.time.LocalDate;
import java.util.List;

public interface ReservationDao {

    Reservation save(final Reservation reservation);

    List<Reservation> findAllByDateAndTimeAndThemeId(final LocalDate date, final ReservationTime time, final Long themeId);

    List<Reservation> findAll();

    boolean existById(final Long id);

    void deleteById(final Long id);

    int countByTimeId(final Long timeId);

    List<Long> findAllTimeIdsByDateAndThemeId(final LocalDate date, final Long themeId);
}
