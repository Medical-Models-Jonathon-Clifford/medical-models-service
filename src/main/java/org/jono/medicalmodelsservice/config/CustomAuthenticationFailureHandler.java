package org.jono.medicalmodelsservice.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {
    private static final Log log = LogFactory.getLog(CustomAuthenticationFailureHandler.class);

    @Override
    public void onAuthenticationFailure(final HttpServletRequest request, final HttpServletResponse response,
            final AuthenticationException exception) throws IOException, ServletException {
        log.error("Authentication failed: " + exception.getMessage());
    }
}
