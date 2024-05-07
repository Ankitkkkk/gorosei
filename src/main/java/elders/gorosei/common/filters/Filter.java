package elders.gorosei.common.filters;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import elders.gorosei.common.dao.Attrs;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
@Component
public class Filter implements HandlerInterceptor {
  private final Logger preLogger = LoggerFactory.getLogger("application.preHandle");
  private final Logger postLogger = LoggerFactory.getLogger("application.postHandle");
  private final Logger afterLogger = LoggerFactory.getLogger("application.afterCompletion");
  private final ObjectMapper objectMapper;
  @Autowired
  public Filter(ObjectMapper mapper) {
    this.objectMapper = mapper;
  }
  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {
    final UUID uuid = UUID.randomUUID();
    MDC.put("traceId", uuid.toString());
    request.setAttribute(Attrs.TRACE_ID, uuid.toString());

    preLogger.info("{} {} {}", request.getRequestURI(), request.getRemoteAddr(), request.getLocalAddr());
    final String body = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
    JsonNode node = objectMapper.readTree(body);
    preLogger.info("json is: {}", node);
    request.setAttribute(Attrs.JSON_BODY, node);
    return HandlerInterceptor.super.preHandle(request, response, handler);
  }

  @Override
  public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
      ModelAndView modelAndView) throws Exception {
    postLogger.info("is this even able to be hit");
    HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
      Object handler, Exception ex) throws Exception {
    afterLogger.info("response code: {}", response.getStatus());
    HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
  }
}
