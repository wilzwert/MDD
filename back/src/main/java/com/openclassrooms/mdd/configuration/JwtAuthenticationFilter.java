package com.openclassrooms.mdd.configuration;


import com.openclassrooms.mdd.model.JwtToken;
import com.openclassrooms.mdd.service.CustomUserDetailsService;
import com.openclassrooms.mdd.service.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

/**
 * @author Wilhelm Zwertvaegher
 * Date:07/11/2024
 * Time:16:01
 * Custom Jwt filter, added to security filter chain in SpringSecurityConfig
 * The goal here is to intercept a bearer token if present in the request
 * and use it to authenticate the current user
 */
@Component
@Slf4j
abstract public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtService jwtService;

    public JwtAuthenticationFilter(
            final CustomUserDetailsService customUserDetailsService,
            final JwtService jwtService
    ) {
        this.customUserDetailsService = customUserDetailsService;
        this.jwtService = jwtService;
    }

    /**
     * Try to get, decode a Bearer token if it is set in the request, and set current context authentication accordingly
     * @param request - the http request {@link HttpServletRequest}
     * @param response - the current {@link HttpServletResponse} http response
     * @param filterChain - the security filter chain
     * @throws ServletException - throws {@link ServletException}
     * @throws IOException - throws {@link IOException}
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // bypass filter if path should remain publicly accessible
        String path = request.getServletPath();
        if(path.matches("/api/auth/(login|register)")) {
            log.info("Path should remain publicly accessible, no need to handle Bearer token");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            Optional<JwtToken> token = jwtService.extractTokenFromRequest(request);
            // if not token found, security filter chain continues
            if(token.isEmpty()) {
                log.info("Token is empty");
                filterChain.doFilter(request, response);
                return;
            }

            // extract JWT Token and included email and try to authenticate the user
            JwtToken jwtToken = token.get();
            String userEmail = jwtToken.getClaims().getSubject();
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (userEmail != null && authentication == null) {
                UserDetails userDetails = this.customUserDetailsService.loadUserByUsername(userEmail);
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // pass authentication to the security context
                log.info("Token handled, set security context authentication");
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
            // security filter chain continues
            filterChain.doFilter(request, response);
        }
        // send appropriate http status code and messages to request response
        catch(MalformedJwtException e) {
            log.warn("Malformed token");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().print("malformed token");
        }
        catch(ExpiredJwtException e) {
            log.warn("Expired token");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().print("invalid_token");
        }
    }
}
