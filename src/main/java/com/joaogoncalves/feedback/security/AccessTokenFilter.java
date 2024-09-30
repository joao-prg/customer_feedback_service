package com.joaogoncalves.feedback.security;

import com.joaogoncalves.feedback.entity.User;
import com.joaogoncalves.feedback.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;

@Slf4j
public class AccessTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private UserRepository userRepository;

    @Value("${security.public-endpoints}")
    private String[] PUBLIC_ENDPOINTS;

    @Override
    protected void doFilterInternal(
            final HttpServletRequest request,
            final @NotNull HttpServletResponse response,
            final @NotNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String requestURI = request.getRequestURI();
        log.info("Processing request URI: {}", requestURI);
        for (String endpoint : PUBLIC_ENDPOINTS) {
            if (requestURI.startsWith(endpoint.replace("**", ""))) {
                log.info("Public endpoint matched [URI: {}]. Skipping token validation.", endpoint);
                filterChain.doFilter(request, response);
                return;
            }
        }
        final String accessToken = parseAccessToken(request);
        final String userId = jwtHelper.getUserIdFromAccessToken(accessToken);
        final User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException(
                        String.format("User not found! [Id: %s]", userId)
                ));
        final UsernamePasswordAuthenticationToken upat = new UsernamePasswordAuthenticationToken(
                user,
                null,
                user.getAuthorities()
        );
        upat.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(upat);
        log.info("User authenticated and set in the security context [username: {}]", user.getUsername());
        filterChain.doFilter(request, response);
    }

    private String parseAccessToken(@NotNull final HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        if (!StringUtils.hasText(authHeader)) {
            throw new IllegalArgumentException("Authorization header is missing");
        }
        if (!authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization header must start with 'Bearer '");
        }
        return authHeader.replace("Bearer ", "");
    }
}
