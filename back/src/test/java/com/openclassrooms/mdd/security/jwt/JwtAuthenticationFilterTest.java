package com.openclassrooms.mdd.security.jwt;


import com.openclassrooms.mdd.configuration.ApiDocProperties;
import com.openclassrooms.mdd.model.JwtToken;
import com.openclassrooms.mdd.security.service.CustomUserDetailsService;
import com.openclassrooms.mdd.security.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import java.io.PrintWriter;
import java.util.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * @author Wilhelm Zwertvaegher
 * Date:04/11/2024
 * Time:11:08
 */

@ExtendWith(MockitoExtension.class)
@Tag("Security")
public class JwtAuthenticationFilterTest {

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private JwtService jwtService;


    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private ApiDocProperties apiDocProperties;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();

    }
    @Test
    public void shouldReturnTrueWhenFilteringNotNecessaryOnEmptyRequest() {
        assertThrows(NullPointerException.class, () -> jwtAuthenticationFilter.shouldNotFilter(null));
    }


    @Test
    public void shouldReturnTrueWhenFilteringNotNecessaryForApi() {
        when(apiDocProperties.getApiDocsPath()).thenReturn("/api/doc");
        when(request.getRequestURI()).thenReturn("/api/doc");

        assertThat(jwtAuthenticationFilter.shouldNotFilter(request)).isTrue();
    }

    @Test
    public void shouldReturnTrueWhenFilteringNotNecessaryForAuth() {
        when(request.getRequestURI()).thenReturn("/api/auth/register");

        assertThat(jwtAuthenticationFilter.shouldNotFilter(request)).isTrue();
    }


    @Test
    public void shouldReturnTrueWhenFilteringNotNecessaryForSwagger() {
        when(apiDocProperties.getApiDocsPath()).thenReturn("/api/doc");
        when(apiDocProperties.getSwaggerPath()).thenReturn("/api/swagger");
        when(request.getRequestURI()).thenReturn("/api/swagger");

        assertThat(jwtAuthenticationFilter.shouldNotFilter(request)).isTrue();
    }

    @Test
    public void shouldReturnTrueWhenFilteringNotNecessaryForSwaggerUi() {
        when(apiDocProperties.getApiDocsPath()).thenReturn("/api/doc");
        when(apiDocProperties.getSwaggerPath()).thenReturn("/api/swagger");
        when(request.getRequestURI()).thenReturn("/swagger-ui/");

        assertThat(jwtAuthenticationFilter.shouldNotFilter(request)).isTrue();
    }

    @Test
    public void shouldReturnFalseWhenFilteringNecessary() {
        when(apiDocProperties.getApiDocsPath()).thenReturn("/api/doc");
        when(apiDocProperties.getSwaggerPath()).thenReturn("/api/swagger");

        when(request.getRequestURI()).thenReturn("/api/topics/");

        assertThat(jwtAuthenticationFilter.shouldNotFilter(request)).isFalse();
    }

    @Test
    public void shouldAuthenticateUser() throws Exception {
        when(customUserDetailsService.loadUserByUsername("test@example.com")).thenReturn(new User("test@example.com", "password", Collections.emptyList()));
        Claims claims = Jwts.claims().subject("test@example.com").build();
        JwtToken jwtToken = new JwtToken("test@example.com", claims);
        when(jwtService.extractTokenFromRequest(request)).thenReturn(Optional.of(jwtToken));

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
    }

    @Test
    public void shouldNotAuthenticateUserWhenAuthenticationNotNull() throws Exception {
        Claims claims = Jwts.claims().subject("test@example.com").build();
        JwtToken jwtToken = new JwtToken("test@example.com", claims);
        when(jwtService.extractTokenFromRequest(request)).thenReturn(Optional.of(jwtToken));

        SecurityContextHolder.getContext().setAuthentication(new Authentication() {
             @Override
             public Collection<? extends GrantedAuthority> getAuthorities() {
                 return List.of();
             }

             @Override
             public Object getCredentials() {
                 return null;
             }

             @Override
             public Object getDetails() {
                 return null;
             }

             @Override
             public Object getPrincipal() {
                 return null;
             }

             @Override
             public boolean isAuthenticated() {
                 return false;
             }

             @Override
             public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

             }

             @Override
             public String getName() {
                 return "";
             }
         });

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
    }

    @Test
    public void shouldNotAuthenticateUserWhenUserNotFoundInClaims() throws Exception {
        Claims claims = Jwts.claims().subject(null).build();
        JwtToken jwtToken = new JwtToken("test@example.com", claims);
        when(jwtService.extractTokenFromRequest(request)).thenReturn(Optional.of(jwtToken));

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    public void shouldNotAuthenticateUserWhenTokenExtractionThrowsException() throws Exception {
        when(jwtService.extractTokenFromRequest(request)).thenThrow(JwtException.class);
        doNothing().when(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        PrintWriter writer = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(writer);
        doNothing().when(writer).print("token_error");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    public void shouldNotAuthenticateUserWhenTokenInvalid() throws Exception {
        when(jwtService.extractTokenFromRequest(request)).thenReturn(Optional.empty());

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
}