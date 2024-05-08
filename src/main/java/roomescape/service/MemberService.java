package roomescape.service;

import org.springframework.stereotype.Service;
import roomescape.dao.MemberDao;
import roomescape.domain.Member;
import roomescape.dto.MemberLoginResponse;
import roomescape.dto.MemberResponse;
import roomescape.dto.TokenRequest;
import roomescape.dto.TokenResponse;
import roomescape.infrastructure.JwtTokenProvider;
import java.util.Objects;

@Service
public class MemberService {

    private final MemberDao memberDao;
    private final JwtTokenProvider jwtTokenProvider;

    public MemberService(final MemberDao memberDao, final JwtTokenProvider jwtTokenProvider) {
        this.memberDao = memberDao;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public TokenResponse createToken(final TokenRequest request) {
        final Member member = memberDao.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException(request.email() + "에 해당하는 사용자가 없습니다"));
        if (!Objects.equals(member.getPassword(), request.password())) {
            throw new IllegalArgumentException("잘못된 비밀번호입니다.");
        }
        final String accessToken = jwtTokenProvider.createToken(member);
        return new TokenResponse(accessToken);
    }

    public MemberResponse findById(final Long id) {
        final Member member = memberDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(id + "에 해당하는 사용자가 없습니다"));
        return new MemberResponse(member.getId(), member.getNameString(), member.getEmail(), member.getPassword());
    }

    public MemberLoginResponse findMemberByToken(final String accessToken) {
        final Long memberId = jwtTokenProvider.getMemberIdByToken(accessToken);
        final MemberResponse memberResponse = findById(memberId);
        return new MemberLoginResponse(memberResponse.name());
    }
}
