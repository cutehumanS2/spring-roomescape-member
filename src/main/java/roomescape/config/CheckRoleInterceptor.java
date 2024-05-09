package roomescape.config;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import roomescape.domain.Role;
import roomescape.dto.MemberLoginResponse;
import roomescape.infrastructure.AuthorizationExtractor;
import roomescape.service.MemberService;

@Component
public class CheckRoleInterceptor implements HandlerInterceptor {

    private final MemberService memberService;
    private final AuthorizationExtractor authorizationExtractor;

    public CheckRoleInterceptor(final MemberService memberService) {
        this.memberService = memberService;
        this.authorizationExtractor = new AuthorizationExtractor();
    }

        @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) {
        final Cookie[] cookies = request.getCookies();
        final String accessToken = authorizationExtractor.extractTokenFromCookie(cookies);
        final MemberLoginResponse memberLoginResponse = memberService.findMemberByToken(accessToken);

        if (!memberLoginResponse.role().equals(Role.ADMIN)) {
            response.setStatus(401);

            return false;
        }
        return true;
    }
}
