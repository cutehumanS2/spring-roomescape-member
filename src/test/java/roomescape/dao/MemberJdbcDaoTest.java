package roomescape.dao;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.domain.Member;
import roomescape.domain.Name;
import roomescape.domain.Role;

import java.util.List;
import java.util.Optional;

class MemberJdbcDaoTest extends DaoTest {

    @Autowired
    private MemberDao memberDao;

    @Test
    @DisplayName("사용자를 저장한다.")
    void saveMember() {
        // given
        final Member member = new Member(new Name("브라운"), "brown@email.com", "1234", Role.USER);

        // when
        final Member savedMember = memberDao.save(member);

        // then
        assertThat(savedMember.getId()).isNotNull();
    }

    @Test
    @DisplayName("Id에 해당하는 사용자를 조회한다.")
    void findMemberById() {
        // given
        final Member savedMember = memberDao.save(new Member(new Name("브라운"), "brown@email.com", "1234", Role.USER));

        // when
        final Optional<Member> member = memberDao.findById(savedMember.getId());

        // then
        assertThat(member).hasValue(savedMember);
    }

    @Test
    @DisplayName("Id에 해당하는 사용자가 없으면 빈 옵셔널을 조회한다.")
    void returnEmptyOptionalWhenFindMemberByNotExistingId() {
        // given
        final Long notExistingId = 3L;

        // when
        final Optional<Member> member = memberDao.findById(notExistingId);

        // then
        assertThat(member).isEmpty();
    }

    @Test
    @DisplayName("email에 해당하는 사용자를 조회한다.")
    void findMemberByEmail() {
        // given
        final Member savedMember = memberDao.save(new Member(new Name("브라운"), "brown@email.com", "1234", Role.USER));

        // when
        final Optional<Member> member = memberDao.findByEmail(savedMember.getEmail());

        // then
        assertThat(member).hasValue(savedMember);
    }

    @Test
    @DisplayName("email에 해당하는 사용자가 없으면 빈 옵셔널을 조회한다.")
    void returnEmptyOptionalWhenFindMemberByNotExistingEmail() {
        // given
        final String notExistingEmail = "odd@email.com";

        // when
        final Optional<Member> member = memberDao.findByEmail(notExistingEmail);

        // then
        assertThat(member).isEmpty();
    }

    @Test
    @DisplayName("사용자 전체 목록을 조회한다.")
    void findAllMembers() {
        // given
        memberDao.save(new Member(new Name("브라운"), "brown@email.com", "1234", Role.USER));

        // when
        final List<Member> members = memberDao.findAll();

        // then
        assertThat(members).hasSize(3);
    }
}
