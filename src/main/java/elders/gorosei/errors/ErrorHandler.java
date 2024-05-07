package elders.gorosei.errors;

import com.fasterxml.jackson.core.JsonParseException;
import elders.gorosei.common.dao.Attrs;
import elders.gorosei.common.dto.ApiError;
import elders.gorosei.common.dto.ApiResponse;
import elders.gorosei.common.dto.ApiSuccess;
import elders.gorosei.errors.codes.Codes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class ErrorHandler {
  private final Logger logger = LoggerFactory.getLogger("application.errorController");
  @ExceptionHandler(ResponseException.class)
  @ResponseBody
  public ResponseEntity<ApiResponse> responseException(ResponseException err) {
    return ResponseEntity
        .status(err.getResponseCode())
        .body(new ApiError(err, MDC.get(Attrs.TRACE_ID)));
  }

  @ExceptionHandler(NoResourceFoundException.class)
  @ResponseBody
  public ResponseEntity<ApiResponse> noResourceFoundException(NoResourceFoundException err) {
    final ResponseException exception = new ResponseException(HttpStatus.NOT_FOUND.value(), Codes.NOT_FOUND, "Resource Not Found.", null);
    return ResponseEntity.status(exception.getResponseCode())
        .body(new ApiError(exception, MDC.get(Attrs.TRACE_ID)));
  }
  @ExceptionHandler(JsonParseException.class)
  @ResponseBody
  public ResponseEntity<ApiResponse> jsonParseException(JsonParseException err) {
    final ResponseException exception = new ResponseException(HttpStatus.BAD_REQUEST.value(), Codes.INVALID_JSON_BODY, "Invalid Json Body.", null);
    return ResponseEntity.status(exception.getResponseCode())
        .body(new ApiError(exception, MDC.get(Attrs.TRACE_ID)));
  }

  @ExceptionHandler(Exception.class)
  @ResponseBody
  public ResponseEntity<ApiResponse> somethingWentWrong(Exception err) {
    logger.error("error occurred: {}", err.getMessage(), err);
    final ResponseException exception = new ResponseException(HttpStatus.INTERNAL_SERVER_ERROR.value(), Codes.INTERNAL_SERVER_ERROR, "Something Went Wrong.", null);
    return ResponseEntity.status(exception.getResponseCode())
        .body(new ApiError(exception, MDC.get(Attrs.TRACE_ID)));
  }
}
