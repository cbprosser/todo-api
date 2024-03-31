package com.cp.projects.todo.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseCookie.ResponseCookieBuilder;
import org.springframework.stereotype.Component;

import com.cp.projects.todo.model.table.RefreshToken;

import jakarta.servlet.http.HttpServletResponse;

@Component
public class CookieUtils {
  
  private static JwtUtil jwtUtil;
  
  @Autowired
  private void initSettings(JwtUtil jwtUtil) {
    CookieUtils.jwtUtil = jwtUtil;
  }
  
  public static enum COOKIE_TYPE {
    SECURE,
    UNSECURE,
  }
  
  public static void createCookie(
      String cookieName,
      String cookieContents,
      long expiration,
      HttpServletResponse response,
      COOKIE_TYPE type)
      throws Exception {
    if (cookieName == null || cookieContents == null)
      throw new Exception("Missing cookie name/contents");
    ResponseCookieBuilder cookieBuilder = ResponseCookie.from(cookieName, cookieContents)
        .httpOnly(true)
        .secure(false)
        .path("/")
        .maxAge(expiration)
        .sameSite("strict");
    switch (type) {
      case UNSECURE:
        cookieBuilder.httpOnly(false);
        break;
      default:
        cookieBuilder.httpOnly(true);
        break;
    }
    ResponseCookie cookie = cookieBuilder.build();

    response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
  }

  public static void createAllCookies(String fingerprint, HttpServletResponse response, RefreshToken refreshToken,
      String authToken)
      throws Exception {
    createCookie("authToken", authToken, jwtUtil.getSettings().getAuthExpiration(), response, COOKIE_TYPE.SECURE);
    createCookie(
        "fingerprint",
        fingerprint,
        LocalDateTime.of(9999, 12, 31, 11, 59, 59, 999).toEpochSecond(ZoneOffset.UTC),
        response,
        COOKIE_TYPE.SECURE);
    createCookie(
        "hasfgpt",
        "",
        LocalDateTime.of(9999, 12, 31, 11, 59, 59, 999).toEpochSecond(ZoneOffset.UTC),
        response,
        COOKIE_TYPE.UNSECURE);
    createCookie(
        "refreshToken",
        refreshToken.getToken(),
        Duration.between(LocalDateTime.now(), refreshToken.getExpireDate().atStartOfDay()).toSeconds(),
        response,
        COOKIE_TYPE.SECURE);
  }

  public static void deleteAllCookies(HttpServletResponse response) throws Exception {
    createCookie("authToken", "", 0, response, COOKIE_TYPE.SECURE);
    createCookie("fingerprint", "", 0, response, COOKIE_TYPE.SECURE);
    createCookie("hasfgpt", "", 0, response, COOKIE_TYPE.UNSECURE);
    createCookie("refreshToken", "", 0, response, COOKIE_TYPE.SECURE);
  }
}
