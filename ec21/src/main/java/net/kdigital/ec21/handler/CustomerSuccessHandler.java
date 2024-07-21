package net.kdigital.ec21.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import net.kdigital.ec21.dto.LoginUserDetails;
import net.kdigital.ec21.dto.check.YesOrNo;

@Slf4j
@Component
public class CustomerSuccessHandler implements AuthenticationSuccessHandler {
    private final RequestCache requestCache = new HttpSessionRequestCache();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        log.info("=========== Login Success");

        List<String> roleNames = new ArrayList<>();
        authentication.getAuthorities().forEach(authority -> {
            roleNames.add(authority.getAuthority());
        });
        log.info("ROLE NAMES : {}", roleNames);

        clearSession(request);

        SavedRequest savedRequest = requestCache.getRequest(request, response);

        String prevPage = (String) request.getSession().getAttribute("prevPage");
        log.info("==== prevPage : {}", prevPage);
        if (prevPage != null) {
            request.getSession().removeAttribute("prevPage");
        }
        String uri = "/";



        // 관리자인 경우
        if (roleNames.contains("ROLE_ADMIN")) {
            response.sendRedirect("/manager/manager_index");
            return;
        }
        // 사용자인 경우
        if (roleNames.contains("ROLE_USER")) {
            // 블랙리스트 or 탈퇴 회원인 경우
            if (authentication.getPrincipal() instanceof LoginUserDetails) {
                // principal을 LoginUserDetails 타입으로 캐스팅
                LoginUserDetails userDetails = (LoginUserDetails) authentication.getPrincipal();

                // 블랙리스트 상태 확인
                if (userDetails.getBlacklistCheck() == YesOrNo.Y) {
                    // 사용자가 블랙리스트에 있는 경우 처리
                    log.info("User is blacklisted");
                    response.sendRedirect("/blacklist");
                    return;
                }

                // 탈퇴한 회원인 경우 처리
                if (userDetails.getEnabled() == YesOrNo.Y) {
                    log.info("User account is a withdrawal account");
                    response.sendRedirect("/withdrawalAccount");
                    return;
                }
            }
            // 이전 페이지로 리디렉션
            // if (savedRequest != null) {
            //     if (!uri.contains("/error")) {
            //         log.info("=============== Redirecting to saved request: {}", uri);
            //         response.sendRedirect(uri);
            //         return;
            //     }
            // } else if (prevPage != null && !prevPage.equals("")) {
            if (prevPage != null && !prevPage.equals("")) {
                uri = prevPage;
                log.info("===============2nd : {}",uri);
                response.sendRedirect(uri);
                return;
            }
            // 이전 URL이 없으면 홈 페이지로 리디렉션
            response.sendRedirect("/");
            return;

        }
        response.sendRedirect("/");

    }

    // 로그인 실패 후 성공 시 남아있는 에러 세션 제거
    protected void clearSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        }
    }
    
}
