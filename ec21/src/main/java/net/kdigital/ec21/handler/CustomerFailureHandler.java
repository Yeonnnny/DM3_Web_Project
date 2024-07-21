package net.kdigital.ec21.handler;

import java.io.IOException;
import java.net.URLEncoder;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CustomerFailureHandler extends SimpleUrlAuthenticationFailureHandler{

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {
        log.info("로그인 실패 : {}", exception.getClass());
        
        String errMsg = "";
        if (exception instanceof BadCredentialsException) {
            // errMsg =exception.getMessage();
            errMsg+= "\nYour username or password is incorrect.";
        }else{
            // errMsg = exception.getMessage();
            errMsg += "\nLogin failed. Please contact the administrator.";
        }

        errMsg = URLEncoder.encode(errMsg, "UTF-8");

        setDefaultFailureUrl("/main/login?error=true&errMessage="+errMsg);

        super.onAuthenticationFailure(request, response, exception);
    }
    
}
