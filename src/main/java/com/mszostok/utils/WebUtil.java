package com.mszostok.utils;

import org.springframework.security.web.savedrequest.SavedRequest;

import javax.servlet.http.HttpServletRequest;

public final class WebUtil {
  private static final String XML_HTTP_REQUEST = "XMLHttpRequest";
  private static final String X_REQUESTED_WITH = "X-Requested-With";

  private static final String CONTENT_TYPE = "Content-type";
  private static final String CONTENT_TYPE_JSON = "application/json";

  private WebUtil() {
  }

  public static boolean isAjax(final HttpServletRequest request) {
    return XML_HTTP_REQUEST.equals(request.getHeader(X_REQUESTED_WITH));
  }

  public static boolean isAjax(final SavedRequest request) {
    return request.getHeaderValues(X_REQUESTED_WITH).contains(XML_HTTP_REQUEST);
  }

  public static boolean isContentTypeJson(final SavedRequest request) {
    return request.getHeaderValues(CONTENT_TYPE).contains(CONTENT_TYPE_JSON);
  }
}
