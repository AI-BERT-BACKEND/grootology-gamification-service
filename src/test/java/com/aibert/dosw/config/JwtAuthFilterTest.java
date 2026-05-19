package com.aibert.dosw.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

class JwtAuthFilterTest {

  private final JwtAuthFilter filter = new JwtAuthFilter();

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void doFilterInternal_validBearer_setsAuthentication() throws Exception {
    String secret = "01234567890123456789012345678901";
    ReflectionTestUtils.setField(filter, "secret", secret);

    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("Authorization", "Bearer " + tokenFor(secret, "student@aibert.edu"));
    MockHttpServletResponse response = new MockHttpServletResponse();
    MockFilterChain chain = new MockFilterChain();

    filter.doFilter(request, response, chain);

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    assertEquals("student@aibert.edu", authentication.getPrincipal());
  }

  @Test
  void doFilterInternal_invalidBearer_clearsAuthenticationAndContinues() throws Exception {
    String secret = "01234567890123456789012345678901";
    ReflectionTestUtils.setField(filter, "secret", secret);

    SecurityContextHolder.getContext().setAuthentication(new org.springframework.security.authentication.UsernamePasswordAuthenticationToken("pre-auth", null));

    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("Authorization", "Bearer invalid.token.value");
    MockHttpServletResponse response = new MockHttpServletResponse();
    MockFilterChain chain = new MockFilterChain();

    filter.doFilter(request, response, chain);

    assertNull(SecurityContextHolder.getContext().getAuthentication());
    assertTrue(response.getStatus() == 200 || response.getStatus() == 0);
  }

  @Test
  void doFilterInternal_withoutAuthorizationHeader_leavesContextEmpty() throws Exception {
    ReflectionTestUtils.setField(filter, "secret", "01234567890123456789012345678901");
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    MockFilterChain chain = new MockFilterChain();

    filter.doFilter(request, response, chain);

    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

  private String tokenFor(String secret, String subject) {
    Key key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    return Jwts.builder()
        .setSubject(subject)
        .setIssuedAt(new Date())
        .setExpiration(Date.from(Instant.now().plusSeconds(3600)))
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }
}
