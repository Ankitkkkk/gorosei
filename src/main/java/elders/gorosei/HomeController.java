package elders.gorosei;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import elders.gorosei.common.dao.Attrs;
import elders.gorosei.common.dto.ApiResponse;
import elders.gorosei.common.dto.ApiSuccess;
import elders.gorosei.errors.ResponseException;
import elders.gorosei.errors.codes.Codes;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HomeController {
  private final Logger logger = LoggerFactory.getLogger("application.HomeController");
  @GetMapping("health")
  public ResponseEntity<ApiResponse> checkHealth(HttpServletRequest request)
      throws JsonProcessingException {
    final String traceId = (String) request.getAttribute(Attrs.TRACE_ID);
    logger.info("req att jb: {}", request.getAttribute(Attrs.JSON_BODY));
    JsonNode node = (JsonNode) request.getAttribute(Attrs.JSON_BODY);
    logger.info("Json body con: {}", node);
    return ResponseEntity.ok(new ApiSuccess( "200", "server up at "));
  }

}
