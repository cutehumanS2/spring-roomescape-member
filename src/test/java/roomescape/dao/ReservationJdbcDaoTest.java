package roomescape.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import roomescape.domain.Name;
import roomescape.domain.Reservation;
import roomescape.domain.ReservationTime;
import roomescape.domain.Theme;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static roomescape.TestFixture.*;

class ReservationJdbcDaoTest extends DaoTest {

    @Autowired
    private ReservationDao reservationDao;

    @BeforeEach
    void setUp() {
        final String insertTimeSql = "INSERT INTO reservation_time (start_at) VALUES (?)";
        jdbcTemplate.update(insertTimeSql, Time.valueOf(LocalTime.parse(MIA_RESERVATION_TIME)));
        final String insertThemeSql = "INSERT INTO theme (name, description, thumbnail) VALUES (?, ?, ?)";
        jdbcTemplate.update(insertThemeSql, WOOTECO_THEME_NAME, WOOTECO_THEME_DESCRIPTION, THEME_THUMBNAIL);
    }

    @Test
    @DisplayName("예약을 저장한다.")
    void save() {
        // given
        final Long timeId = 1L;
        final Reservation reservation = MIA_RESERVATION(new ReservationTime(timeId, MIA_RESERVATION_TIME), WOOTECO_THEME());

        // when
        final Reservation savedReservation = reservationDao.save(reservation);

        // then
        assertThat(savedReservation.getId()).isNotNull();
    }

    @Test
    @DisplayName("동일 시간대의 예약 목록을 조회한다.")
    void findAllByDateAndTime() {
        // given
        final Long timeId = 1L;
        final Long themeId = 1L;
        final String insertSql = "INSERT INTO reservation (name, date, time_id, theme_id) VALUES (?, ?, ?, ?), (?, ?, ?, ?)";
        jdbcTemplate.update(
                insertSql,
                USER_MIA, Date.valueOf(MIA_RESERVATION_DATE), timeId, themeId,
                USER_TOMMY, Date.valueOf(MIA_RESERVATION_DATE), timeId, themeId
        );

        // when
        final List<Reservation> reservations = reservationDao.findAllByDateAndTimeAndThemeId(
                LocalDate.parse(MIA_RESERVATION_DATE), new ReservationTime(MIA_RESERVATION_TIME), themeId);

        // then
        assertThat(reservations).hasSize(2)
                .extracting(Reservation::getName)
                .containsExactly(new Name(USER_MIA), new Name(USER_TOMMY));
    }

    @Test
    @DisplayName("모든 예약 목록을 조회한다.")
    void findAll() {
        // given
        final Long timeId = 1L;
        final Long themeId = 1L;
        final String insertSql = "INSERT INTO reservation (name, date, time_id, theme_id) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(insertSql, USER_MIA, MIA_RESERVATION_DATE, timeId, themeId);

        // when
        final List<Reservation> reservations = reservationDao.findAll();

        // then
        final Integer count = jdbcTemplate.queryForObject("SELECT count(1) from reservation", Integer.class);
        assertAll(() -> {
            assertThat(reservations.size()).isEqualTo(count);
            assertThat(reservations).extracting(Reservation::getTheme)
                    .extracting(Theme::getName)
                    .containsExactly(WOOTECO_THEME_NAME);
            assertThat(reservations).extracting(Reservation::getTime)
                    .extracting(ReservationTime::getStartAt)
                    .containsExactly(LocalTime.parse(MIA_RESERVATION_TIME));
        });
    }

    @Test
    @DisplayName("Id로 예약이 존재하면 true를 반환한다.")
    void findById() {
        // given
        long timeId = 1L;
        long themeId = 1L;
        final String insertSql = "INSERT INTO reservation (name, date, time_id, theme_id) VALUES (?, ?, ?, ?)";
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(insertSql, new String[]{"id"});
            ps.setString(1, USER_MIA);
            ps.setDate(2, Date.valueOf(MIA_RESERVATION_DATE));
            ps.setLong(3, timeId);
            ps.setLong(4, themeId);
            return ps;
        }, keyHolder);
        final Long id = keyHolder.getKey().longValue();

        // when
        final boolean isExist = reservationDao.existById(id);

        // then
        assertThat(isExist).isTrue();
    }

    @Test
    @DisplayName("Id에 해당하는 예약이 없다면 false를 반환한다.")
    void findByNotExistingId() {
        // given
        final Long id = 1L;

        // when
        final boolean isExist = reservationDao.existById(id);

        // then
        assertThat(isExist).isFalse();
    }

    @Test
    @DisplayName("Id로 예약을 삭제한다.")
    void deleteById() {
        // given
        final long timeId = 1L;
        final String insertSql = "INSERT INTO reservation (name, date, time_id) VALUES (?, ?, ?)";
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(insertSql, new String[]{"id"});
            ps.setString(1, USER_MIA);
            ps.setDate(2, Date.valueOf(MIA_RESERVATION_DATE));
            ps.setLong(3, timeId);
            return ps;
        }, keyHolder);
        final Long id = keyHolder.getKey().longValue();

        // when
        reservationDao.deleteById(id);

        // then
        final Integer count = jdbcTemplate.queryForObject("SELECT count(1) from reservation where id = ?", Integer.class, id);
        assertThat(count).isEqualTo(0);
    }

    @Test
    @DisplayName("timeId에 해당하는 예약 건수를 조회한다.")
    void countByTimeId() {
        // given
        final long timeId = 2L;

        // when
        final int count = reservationDao.countByTimeId(timeId);

        // then
        assertThat(count).isEqualTo(0);
    }

    @Test
    @DisplayName("날짜와 themeId로 예약 목록을 조회한다.")
    void findAllByDateAndThemeId() {
        // given
        final Long timeId = 1L;
        final Long themeId = 1L;
        final String insertSql = "INSERT INTO reservation (name, date, time_id, theme_id) VALUES (?, ?, ?, ?), (?, ?, ?, ?)";
        jdbcTemplate.update(
                insertSql,
                USER_MIA, Date.valueOf(MIA_RESERVATION_DATE), timeId, themeId,
                USER_TOMMY, Date.valueOf(MIA_RESERVATION_DATE), timeId, themeId
        );

        // when
        final List<Long> reservationsByDateAndThemeId = reservationDao.findAllTimeIdsByDateAndThemeId(LocalDate.parse(MIA_RESERVATION_DATE), themeId);

        // then
        assertThat(reservationsByDateAndThemeId).hasSize(2);
    }
}